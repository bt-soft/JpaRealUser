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

Az EclipseLink a VPD műveletek számára a `DBMS_SESSION.SET_IDENTIFIER` és `DBMS_SESSION.CLEAR_IDENTIFIER` tárolt eljárások segítségével állítja/törli a `SYS_CONTEXT('USERENV','CLIENT_IDENTIFIER')` függvény által elérhető `client_identifier` session változót. Az EclipseLink az AbstractSession osztály session event események postAcquireConnection() és preReleaseConnection() metódusain keresztül - Oracle DB esetén - az OraclePlatform osztály getVPDSetIdentifierQuery() és getVPDClearIdentifierQuery() metódusai segítségével oldja meg.



```java
/**
 * INTERNAL:
 * Return an Oracle defined VPD set identifier query.
 */
@Override
public DatabaseQuery getVPDSetIdentifierQuery(String vpdIdentifier) {
    if (vpdSetIdentifierQuery == null) {
        vpdSetIdentifierQuery = new DataModifyQuery("CALL DBMS_SESSION.SET_IDENTIFIER(#" + vpdIdentifier + ")");
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





