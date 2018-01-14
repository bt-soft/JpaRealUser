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
 *
 * @author BT
 */
@Stateless
@Slf4j
public class EntityService {

    @PersistenceContext
    private EntityManager em;

    @Resource
    SessionContext ctx;

    /**
     * Tábla insert
     *
     * @param testdata    test adat
     * @param currentUser bejelentkezett user
     *
     * @return perzisztált entitás
     */
    public JruTbl doTest(String testdata, String currentUser) {

        log.trace("-------------------------------------------------------------------------------------------------------------------------------------------");
        log.trace("doTest('{}', '{}')", testdata, currentUser);

        log.trace("ThreadLocalMap[clientIdentifier]: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));

        ThreadLocalMap.put(ThreadLocalMap.KEY_CLIENT_ID, currentUser);

        JruTbl entity = new JruTbl();
        entity.setJpaUser(currentUser);
        entity.setTestData(testdata);
        em.persist(entity);

        log.trace("doTest end, id: {}", entity.getId());

        return entity;

    }

    /**
     * Tábla insert
     *
     * @param testdata test adat
     *
     * @return perzisztált entitás
     */
    public JruTbl doTest(String testdata) {

        Principal callerPrincipal = ctx.getCallerPrincipal();

        log.trace("-------------------------------------------------------------------------------------------------------------------------------------------");

        log.trace("doTest('{}') -> callerPrincipal: {}", testdata, callerPrincipal.getName());

        log.trace("ThreadLocalMap[clientIdentifier]: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));

        ThreadLocalMap.put(ThreadLocalMap.KEY_CLIENT_ID, callerPrincipal.getName());

        JruTbl entity = new JruTbl();
        entity.setJpaUser(callerPrincipal.getName());
        entity.setTestData(testdata);
        em.persist(entity);

        log.trace("doTest end, id: {}", entity.getId());

        return entity;

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
