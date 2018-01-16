/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    EntityService.java
 *  Created: 2018.01.13. 22:14:33
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.jru.model.service;

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

/**
 * A két entitást kezelő SLSB bean
 *
 * @author BT
 */
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
        entity.setJpaUser(currentUser);
        entity.setTestData(testdata);
        em.persist(entity);

        log.trace("doTest end, id: {}", entity.getId());

        return entity;
    }

    /**
     * Tábla insert
     * A bejelentkezett usert paraméterként kapja
     *
     * @param testdata    teszt adat
     * @param currentUser bejelentkezett user
     *
     * @return perzisztált entitás
     */
    public JruTbl doTest(String testdata, String currentUser) {

        log.trace("-------------------------------------------------------------------------------------------------------------------------------------------");
        log.trace("doTest('{}', '{}')", testdata, currentUser);

        return persist(testdata, currentUser);

    }

    /**
     * Tábla insert
     * A bejelentkezett usert az EJB SessionContext-ből nyeri ki
     *
     * @param testdata teszt adat
     *
     * @return perzisztált entitás
     */
    public JruTbl doTest(String testdata) {

        Principal callerPrincipal = ctx.getCallerPrincipal();
        String currentUser = callerPrincipal.getName();

        log.trace("-------------------------------------------------------------------------------------------------------------------------------------------");
        log.trace("doTest('{}') -> callerPrincipal: {}", testdata, currentUser);

        return persist(testdata, currentUser);
    }

    /**
     * Hibák lekérése
     *
     * @return hibás rekordok listája
     */
    public List<JruJrnl> findAllErrors() {
        Query query = em.createNamedQuery("JruJrnl.findAllErrors");
        List<JruJrnl> result = query.getResultList();

        return result;
    }

}
