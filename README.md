# RealJpaUser (JRU)
Test the transfer of the real user ID to the Oracle database when using connection pool


## A feladat

Hogyan lehet megoldani, hogy egy JPA pool-olt session kapcsolatban biztonságosan át tudjuk adni a DB felé a UI felületen bejelentkezett tényleges felhasználó azonosítóját?

Adott egy korábbi, több instance-ból álló Oracle RAC adatbázison(DB) alapuló rendszer, Oracle Forms felhasználói felülettel, sok ezer interaktív felhasználóval, háttérben futó batch jellegű job-okkal, stb.
Ehhez a környezethez kell illeszteni egy olyan újabb JEE7/JSF/JPA architektúrát - ami már szintén rendelkezik viszonylag számos alkalmazással - úgy, hogy a két alkalmazástér továbbra is párhuzamosan működjön tovább.

A jelenleg megoldandó problémát az jelenti, hogy a korábbi rendszerek olyan DB trigger (after insert or update or delete) alapú audit/journal megoldást használnak, ami az adatbázis `user` SQL függvény segítségével
állapítja meg az aktuális felhasználót. A JEE környezetben a JPA pooling miatt az Alkalmazásszerver (AS) és a DB között technikai user van, így egy módosító SQL esetén a DB triggerek a technikai user account-ját
írják be az audit táblamezőkbe a tényleges felhasználó helyett.
A klasszikus JPA entitás életciklus annotációk (@Pre/PostPersist|Update|Remove) a DB triggerek miatt nem segítenek.


### A környezet

| Komponens | Technológia | Implementáció |
|-----------|-------------|---------------|
| DB | Oracle | Oracle 11g |
| AS | JEE7 | GalssFish 4.1.x/5|
| ORM | JPA | EclipseLink 2.6.1/2.7.1 |
| UI | JSF  | JSF 2.1 + PrimeFaces 6.1|


## A lehetséges megoldás

Tehát azt kell megoldani, hogy az adatbázisban a tranzakció alatt biztosítsuk azt, a triggerek számára is elérhetővé váljék az aktuális felhasználó neve, mindezt pool-ot session-okban. A feladathoz kísértetiesen hasonlít az EclipseLink VPD (Virtual Private Database) támogatása, abban szintén meg kell oldani ugyan ezt a problémát. 

Az EclipseLink a VPD műveletek számára a `DBMS_SESSION.SET_IDENTIFIER` és `DBMS_SESSION.CLEAR_IDENTIFIER` tárolt eljárások segítségével állítja be/törli a `SYS_CONTEXT('USERENV','CLIENT_IDENTIFIER')` függvény által elérhető `client_identifier` session változót. Az EclipseLink az *AbstractSession* osztály *postAcquireConnection()* és *preReleaseConnection()* session event esemény metódusain keresztül - Oracle DB esetén - az *OraclePlatform* osztály a *getVPDSetIdentifierQuery()* és a *getVPDClearIdentifierQuery()* metódusai segítségével oldja meg.


```java
/**
 * INTERNAL:
 * Return an Oracle defined VPD set identifier query.
 */
@Override
public DatabaseQuery getVPDSetIdentifierQuery(String vpdIdentifier) {
    if (vpdSetIdentifierQuery == null) {
        vpdSetIdentifierQuery = new DataModifyQuery("CALL DBMS_SESSION.SET_IDENTIFIER('" + vpdIdentifier + "')");
    }
    
    return vpdSetIdentifierQuery;
}
/**
     * INTERNAL:
     * Return an Oracle defined VPD clear identifier query.
     */
    @Override
    public DatabaseQuery getVPDClearIdentifierQuery(String vpdIdentifier) {
        if (vpdClearIdentifierQuery == null) {
            vpdClearIdentifierQuery = new DataModifyQuery("CALL DBMS_SESSION.CLEAR_IDENTIFIER()");
        }
    
        return vpdClearIdentifierQuery;
    }
```

### Az implementáció

A jelenlegi megoldás is erre a lehetőségre támaszkodik, implementálja a *postAcquireConnection()* és *preReleaseConnection()* session esemény metódusokat. Ezt az EclipseLink *org.eclipse.persistence.sessions* csomag *SessionEventAdapter* osztályának leszármaztatásával végezhetjük el, felülírjuk a két fenti metódust az alábbiak szerint. 

#### JpaSessionEventAdapter osztály


```java
package hu.btsoft.jru.core.jpa.sessionevent

import hu.btsoft.jru.core.jsf.ThreadLocalMap;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/**
 * JPA Session event adapter
 *
 * A JPA postAcquireConnection()/preReleaseConnection() eseményeire ráköltözve
 * álítjuk be/töröljük ki a DB session-ból a 'client_identifier' userenv
 * változót (Az EclipseLink VPD is így csinálja, lehet, hogy nekünk is jó lesz)
 *
 * A DB trigger innen tudja majd kiszedni az aktuális felhasználót
 *
 * A többi esemény csak a log trace kedvéért implementált, és csak logol
 *
 * @author BT
 */
@Slf4j
public class JpaSessionEventAdapter extends SessionEventAdapter {

    public static final String KEY_CLIENT_ID = "client_identifier";

    /**
     * Client ID beállítása
     *
     * PUBLIC: This event is raised on when using the server/client sessions.
     * This event is raised after a connection is acquired from a connection
     * pool.
     *
     * @param event
     */
    @Override
    public void postAcquireConnection(SessionEvent event) {

        log.trace("postAcquireConnection: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
        String clientIdentifier = (String) ThreadLocalMap.get(KEY_CLIENT_ID);

        //Ha van definiált 'client_identifier', akkor beállítjuk a session-nak
        if (clientIdentifier != null) {

            DatabaseAccessor accessor = (DatabaseAccessor) event.getResult();

            try (Statement stmt = accessor.getConnection().createStatement();) {

                String sql = "BEGIN DBMS_SESSION.SET_IDENTIFIER('" + clientIdentifier + "'); " + " END;";
                stmt.execute(sql);

                log.trace("SQL: {}", sql);

            } catch (DatabaseException e) {
                log.error("DB Error", e);
            } catch (SQLException e) {
                log.error("SQL Error", e);
            }
        }

    }

    /**
     * Client ID törlése
     *
     * PUBLIC: This event is raised on when using the server/client sessions.
     * This event is raised before a connection is released into a connection
     * pool.
     *
     * @param event
     */
    @Override
    public void preReleaseConnection(SessionEvent event) {

        log.trace("preReleaseConnection: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));

        //Ha van definiált 'client_identifier', akkor töröljük a session-ból
        String clientIdentifier = (String) ThreadLocalMap.get(KEY_CLIENT_ID);
        if (clientIdentifier != null) {

            DatabaseAccessor accessor = (DatabaseAccessor) event.getResult();

            try (Statement stmt = accessor.getConnection().createStatement();) {

                final String SQL = "BEGIN DBMS_SESSION.CLEAR_IDENTIFIER; END;";
                stmt.execute(SQL);

                log.trace("SQL: {}", SQL);

            } catch (DatabaseException e) {
                log.error("DB Error", e);
            } catch (SQLException e) {
                log.error("SQL Error", e);
            }
        }
    }
```
<span style="font-size: 0.7em;">
(Mivel ez csak egy implementációs teszt, így sokat logol, és a hibakezelések szofisztikáltabb implementációja sem annyira
neuralgikus pont.) 
</span>


#### JPA persistence.xml
Az EclipseLink session event listener élesítése a *persistence.xml* fájlban megadott *eclipselink.session-event-listener* property-jében végezhető el.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.1" 
             xmlns="http://xmlns.jcp.org/xml/ns/persistence" 
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
    
    <persistence-unit name="JruTest-PU" transaction-type="JTA">
        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
        <jta-data-source>jdbc/jruTest</jta-data-source>
        <exclude-unlisted-classes>false</exclude-unlisted-classes>
        <properties>
            <property name="eclipselink.session-event-listener" value="hu.btsoft.jru.core.jpa.sessionevent.JpaSessionEventAdapter"/>

            <!-- GF 4.1.x esetén -->
            <!--<property name="eclipselink.logging.logger" value="JavaLogger"/>-->

            <!-- GF 5 esetén -->
            <property name="eclipselink.logging.logger" value="org.eclipse.persistence.logging.slf4j.SLF4JLogger"/>
```

A fentiekből következik, hogy az esemény figyelőnk példányosítását és kezelését az EclipseLink JPA provider végzi. Viszont az eseménymetódusok szignatúrája nem teszi lehetővé azt, hogy további információkat adjuk át a példánynak, így a felhasználó azonosítójának átadását még meg kell oldani. Erre egy JEE környezetben nem túl sok mód adódik, de talán a legegyszerűbb egy ThreadLocal változó bevetése.

#### JPA entitás műveletek osztályai (DAL/DAO/Service/stb...)

A fenti kód egy *ThreadLocalMap* osztályban veszi át az aktuális felhasználó azonosítóját a 'client_identifier' Map kulcson. Ha ez a kulcs létezik, akkor meghívja a tárolt eljárást az adatbázis művelet előtt és után, ha nem létezik akkor nem. Ezzel a módszerrel biztosítható az, hogy az alkalmazás korábbi kódbázisainak működésében ne okozzunk változást, új kódok esetén csak ezt a változót kell beállítani a módosító DB műveletek hívása előtt. (Bár akár egy SQL lekérdezésben is lehetnek olyan eljárás- vagy függvényhívások, amik audit logot vezethetnek, így talán érdemesebb minden olyan DB hívásba beépíteni, ahol már ismert az interaktív UI felhasználó)


```java
import hu.btsoft.jru.core.jpa.sessionevent.JpaSessionEventAdapter;
import hu.btsoft.jru.core.jsf.ThreadLocalMap;
import hu.btsoft.jru.model.entity.JruJrnl;
import hu.btsoft.jru.model.entity.JruTbl;
import java.security.Principal;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Stateless
@Slf4j
public class EntityService {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private SessionContext ctx;

    /**
     * Entitás létrehozása és elmentése
     *
     * @param testdata    teszt adat
     * @param currentUser bejelentkezett user
     *
     * @return mentett entitás
     */
    private JruTbl persist(String testdata, String currentUser) {

        //Eltesszük egy threadlocal változóba a kliend ID-t
        ThreadLocalMap.put(JpaSessionEventAdapter.KEY_CLIENT_ID, currentUser);

        JruTbl entity = new JruTbl();
        entity.setParamUser(currentUser);
        entity.setTestData(testdata);
        em.persist(entity);

        log.trace("doTest end, id: {}", entity.getId());

        return entity;
    }
```


Persze pont ez az Achilles sarka ennek a megoldásnak: ha a *ThreadLocalMap.put(JpaSessionEventAdapter.KEY_CLIENT_ID, currentUser);* hívást elfelejtjük kiadni, akkor marad az eredeti probléma: az audit a JPA technikai user-el fog megtörténni. Bár ezen lehetne segíteni egy az osztályra/metódusra ráhúzott inteceptor segítségével...

Biztosítani kell még azt (mivel pool-olt a session), hogy a kliens session kapcsolat elengedésével mindenképpen törlődjön a *ThreadLocalMap* ezen kulcsa. Ezt a *JpaSessionEventAdapter* osztály *postReleaseClientSession()* metódusában végezzük. 

```java
/**
     * A 'client_identifier' ThreadLocal változót a biztonság kedvéért
     * mindenképpen töröljük!
     *
     * PUBLIC: This event is raised on the client session after releasing.
     *
     * @param event
     */
    @Override
    public void postReleaseClientSession(SessionEvent event) {

        //Ha a klien kilép az adatbázisból, akkor töröljük a ThreadLocal 'client_identifier' változót
        log.trace("postReleaseClientSession: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
        ThreadLocalMap.remove(KEY_CLIENT_ID);

        log.trace("postReleaseClientSession: KEY_CLIENT_ID törölve -> {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
    }
```
A fenti *EntityService* demó osztály *persist()* metódusában ne is próbálkozzunk a törléssel: mikor a metódus már visszaadta a vezérlést, addig még bőven folynak JPA műveletek az adatbázisban.



### A teszt futtatás

A teszt során a felhúzott session event listener már végzi a dolgát: minden releváns SQL utasítás előtt/után végrehajtódik a `client_identifier` DB oldali változó automatikus beállítása és törlése, valamint az adott szál *ThreadLocalMap*  kulcsának a törlése is.

<span style="font-size:0.6em">
<pre><code>
Info:   15:33:23.733 TRACE - [http-listener-1(4)] - hu.btsoft.jru.model.service.EntityService(doTest:77)         - -------------------------------------------------------------------------------------------------------------------------------------------
Info:   15:33:23.733 TRACE - [http-listener-1(4)] - hu.btsoft.jru.model.service.EntityService(doTest:78)         - doTest('test data', 'user001')
Info:   15:33:23.749 DEBUG - [http-listener-1(4)] - eclipselink.logging.connection(log:59)                       - client acquired: 1873423915
Info:   15:33:23.755 TRACE - [http-listener-1(4)] - eclipselink.logging.connection(log:47)                       - Connection acquired from connection pool [read]
Info:   15:33:23.755 TRACE - [http-listener-1(4)] - eclipselink.logging.connection(log:47)                       - reconnecting to external connection pool
Info:   15:33:23.814 TRACE - [http-listener-1(4)] - essionevent.JpaSessionEventAdapter(postAcquireConnection:53) - postAcquireConnection: user001
Info:   15:33:23.848 TRACE - [http-listener-1(4)] - essionevent.JpaSessionEventAdapter(postAcquireConnection:66) - SQL: <b>BEGIN DBMS_SESSION.SET_IDENTIFIER('user001');  END;</b>
Info:   15:33:23.848 DEBUG - [http-listener-1(4)] - eclipselink.logging.sql(log:59)                              - SELECT SCHEMAOWNER.JRU_SEQ.NEXTVAL FROM DUAL
Info:   15:33:23.915 TRACE - [http-listener-1(4)] - sessionevent.JpaSessionEventAdapter(preReleaseConnection:88) - preReleaseConnection: user001
Info:   15:33:23.929 TRACE - [http-listener-1(4)] - essionevent.JpaSessionEventAdapter(preReleaseConnection:101) - SQL: <b>BEGIN DBMS_SESSION.CLEAR_IDENTIFIER; END;</b>
Info:   15:33:23.931 TRACE - [http-listener-1(4)] - eclipselink.logging.connection(log:47)                       - Connection released to connection pool [read].
Info:   15:33:23.932 TRACE - [http-listener-1(4)] - eclipselink.logging.sequencing(log:47)                       - sequencing preallocation for JRU_SEQ: objects: 50 , first: 7, last: 56
Info:   15:33:23.932 TRACE - [http-listener-1(4)] - eclipselink.logging.sequencing(log:47)                       - assign sequence to the object (7 -> JruTbl(id=null, paramUser=user001, testData=test data, jruJrnl=null))
Info:   15:33:23.936 TRACE - [http-listener-1(4)] - hu.btsoft.jru.model.service.EntityService(persist:61)        - doTest end, id: 7
Info:   15:33:23.947 TRACE - [http-listener-1(4)] - eclipselink.logging.connection(log:47)                       - Connection acquired from connection pool [default].
Info:   15:33:23.947 TRACE - [http-listener-1(4)] - eclipselink.logging.connection(log:47)                       - reconnecting to external connection pool
Info:   15:33:23.954 TRACE - [http-listener-1(4)] - essionevent.JpaSessionEventAdapter(postAcquireConnection:53) - postAcquireConnection: user001
Info:   15:33:23.955 TRACE - [http-listener-1(4)] - essionevent.JpaSessionEventAdapter(postAcquireConnection:66) - SQL: <b>BEGIN DBMS_SESSION.SET_IDENTIFIER('user001');  END;</b>
Info:   15:33:23.955 DEBUG - [http-listener-1(4)] - eclipselink.logging.sql(log:59)                              - INSERT INTO SCHEMAOWNER.JRU_TBL (ID, PARAM_USER, TXT) VALUES (?, ?, ?)
	bind => [7, user001, test data]
Info:   15:33:23.958 TRACE - [http-listener-1(4)] - sessionevent.JpaSessionEventAdapter(preReleaseConnection:88) - preReleaseConnection: user001
Info:   15:33:23.962 TRACE - [http-listener-1(4)] - essionevent.JpaSessionEventAdapter(preReleaseConnection:101) - SQL: <b>BEGIN DBMS_SESSION.CLEAR_IDENTIFIER; END;</b>
Info:   15:33:23.962 TRACE - [http-listener-1(4)] - eclipselink.logging.connection(log:47)                       - Connection released to connection pool [default].
Info:   15:33:23.964 DEBUG - [http-listener-1(4)] - eclipselink.logging.connection(log:59)                       - client released
Info:   15:33:23.964 TRACE - [http-listener-1(4)] - onevent.JpaSessionEventAdapter(postReleaseClientSession:123) - postReleaseClientSession: user001
Info:   15:33:23.964 TRACE - [http-listener-1(4)] - onevent.JpaSessionEventAdapter(postReleaseClientSession:126) - postReleaseClientSession: KEY_CLIENT_ID törölve -> null
</pre></code></span>


A megoldás terheléses tesztje egy egyszerre 100 interaktív felhasználó szimuláló (felhasználónként 50 véletlenszerű tesztadatot beküldő és mindezt 5 iterációban megismétlő) környezetben is jól szerepelt, így valószínűleg érdemes vele tovább foglalkozni.  


## A környezet kialakítása

Az egyszerű tesztelői környezetről bővebben a `setup` mappában olvashatunk.

## Stress teszt

A megoldás működőképességének komolyabb tesztelését egy JMeter projekt segítségével lehet ellenőrizi.
Erről bővebben a `jmeter` mappában lehet olvasni.

## UI felület

A fejlesztéshez és a próbálgatáshoz készült, a feladat szempontjából nincs túl nagy jelentősége.




