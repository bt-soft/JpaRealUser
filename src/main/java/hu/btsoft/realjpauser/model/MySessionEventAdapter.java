/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    MySessionEventAdapter.java
 *  Created: 2018.01.13. 21:18:32
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.realjpauser.model;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/**
 *
 * @author BT
 */
@Slf4j
public class MySessionEventAdapter extends SessionEventAdapter {

    /**
     * PUBLIC:
     * This event is raised when a ClientSession, with Isolated data, acquires
     * an exclusive connection.
     *
     * @param event
     */
    @Override
    public void postAcquireExclusiveConnection(SessionEvent event) {
        log.trace("postAcquireExclusiveConnection");
        super.postAcquireExclusiveConnection(event);
    }

    /**
     * PUBLIC:
     * This event is fired just before a Client Session, with isolated data,
     * releases its Exclusive Connection
     *
     * @param event
     */
    @Override
    public void preReleaseExclusiveConnection(SessionEvent event) {
        log.trace("preReleaseExclusiveConnection");
        super.preReleaseExclusiveConnection(event);
    }

    /**
     * PUBLIC:
     * This event is raised on when using the server/client sessions.
     * This event is raised after a connection is acquired from a connection pool.
     *
     * @param event
     */
    @Override
    public void postAcquireConnection(SessionEvent event) {
        log.trace("postAcquireConnection");
        super.postAcquireConnection(event);
    }

    /**
     * PUBLIC:
     * This event is raised on when using the server/client sessions.
     * This event is raised before a connection is released into a connection pool.
     *
     * @param event
     */
    @Override
    public void preReleaseConnection(SessionEvent event) {
        log.trace("preReleaseConnection");
        super.preReleaseConnection(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the client session after creation/acquiring.
     *
     * @param event
     */
    @Override
    public void postAcquireClientSession(SessionEvent event) {
        log.trace("postAcquireClientSession");
        super.postAcquireClientSession(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the client session after releasing.
     *
     * @param event
     */
    @Override
    public void postReleaseClientSession(SessionEvent event) {
        log.trace("postReleaseClientSession");
        super.postReleaseClientSession(event);
    }

}
