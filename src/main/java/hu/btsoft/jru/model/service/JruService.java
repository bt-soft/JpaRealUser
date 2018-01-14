/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    JruService.java
 *  Created: 2018.01.13. 22:14:33
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.jru.model.service;

import hu.btsoft.jru.core.jsf.ThreadLocalMap;
import hu.btsoft.jru.model.entity.JruTbl;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author BT
 */
@Stateless
@Slf4j
public class JruService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Tábla insert
     *
     * @param testdata    test adat
     * @param currentUser bejelentkezett user
     *
     * @return perzisztált entitás
     */
    public JruTbl doTest(String testdata, String currentUser) {

        log.trace("doTest('{}', '{}')", testdata, currentUser);
        ThreadLocalMap.put(ThreadLocalMap.KEY_CLIENT_ID, currentUser);

        JruTbl entity = new JruTbl();
        entity.setJpaUser(currentUser);
        entity.setTestData(testdata);
        em.persist(entity);

        ThreadLocalMap.removeAll();

        log.trace("doTest end");

        return entity;
    }
}
