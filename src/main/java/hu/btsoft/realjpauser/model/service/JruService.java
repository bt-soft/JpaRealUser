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
package hu.btsoft.realjpauser.model.service;

import hu.btsoft.realjpauser.model.entity.JruTbl;
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

    public JruTbl doTest(String testdata) {

        JruTbl entity = new JruTbl();
        entity.setTestData(testdata);
        em.persist(entity);

        return entity;
    }
}
