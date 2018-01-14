/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    JpaSessionEventAdapter.java
 *  Created: 2018.01.13. 21:18:32
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.jru.core.jpa.sessionevent;

import hu.btsoft.jru.core.jsf.ThreadLocalMap;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventListener;

/**
 *
 * @author BT
 */
@Slf4j
public class JpaSessionEventAdapter /*extends SessionEventAdapter*/ implements SessionEventListener {

    /**
     * PUBLIC:
     * This event is raised on the client session after creation/acquiring.
     *
     * @param event
     */
    @Override
    public void postAcquireClientSession(SessionEvent event) {
        log.trace("postAcquireClientSession: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    /**
     * PUBLIC:
     * This event is raised on the client session before releasing.
     *
     * @param event
     */
    @Override
    public void preReleaseClientSession(SessionEvent event) {
        log.trace("preReleaseClientSession: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    /**
     * PUBLIC:
     * This event is raised on the client session after releasing.
     *
     * @param event
     */
    @Override
    public void postReleaseClientSession(SessionEvent event) {
        log.trace("postReleaseClientSession: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }
//
//-------------------------------------------------------------------
//

    /**
     * PUBLIC:
     * This event is raised on when using the server/client sessions.
     * This event is raised after a connection is acquired from a connection pool.
     *
     * @param event
     */
    @Override
    public void postAcquireConnection(SessionEvent event) {
        log.trace("postAcquireConnection: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));

        String clientIdentifier = (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID);
        if (clientIdentifier != null) {

            DatabaseAccessor accessor = (DatabaseAccessor) event.getResult();

//XXX: ?
            //Session session = (Session) event.getSession();
            //accessor.incrementCallCount((AbstractSession) session);
//
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
     * PUBLIC:
     * This event is raised on when using the server/client sessions.
     * This event is raised before a connection is released into a connection pool.
     *
     * @param event
     */
    @Override
    public void preReleaseConnection(SessionEvent event) {
        log.trace("preReleaseConnection: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));

        Session session = (Session) event.getSession();
        String clientIdentifier = (String) session.getProperty("client_identifier");

        if (clientIdentifier != null) {

            DatabaseAccessor accessor = (DatabaseAccessor) event.getResult();

            try (Statement stmt = accessor.getConnection().createStatement();) {

                final String SQL = "BEGIN DBMS_SESSION.CLEAR_IDENTIFIER; END;";
                stmt.execute(SQL);

//XXX: ?
                //accessor.decrementCallCount();
//
                log.trace("SQL: {}", SQL);

            } catch (DatabaseException e) {
                log.error("DB Error", e);
            } catch (SQLException e) {
                log.error("SQL Error", e);
            }
        }
    }

//
//-------------------------------------------------------------------
//
    @Override
    public void postAcquireExclusiveConnection(SessionEvent event) {
        log.trace("postAcquireExclusiveConnection");
    }

    @Override
    public void preReleaseExclusiveConnection(SessionEvent event) {
        log.trace("preReleaseExclusiveConnection");
    }

    @Override
    public void preLogin(SessionEvent event) {
        log.trace("preLogin");
    }

    @Override
    public void postLogin(SessionEvent event) {
        log.trace("postLogin");
    }

    @Override
    public void preLogout(SessionEvent event) {
        log.trace("preLogout");
    }

    @Override
    public void postLogout(SessionEvent event) {
        log.trace("postLogout");
    }

    @Override
    public void preRollbackTransaction(SessionEvent event) {
        log.trace("preRollbackTransaction");
    }

    @Override
    public void postRollbackTransaction(SessionEvent event) {
        log.trace("postRollbackTransaction");
    }

    @Override
    public void preMergeUnitOfWorkChangeSet(SessionEvent event) {
        log.trace("preMergeUnitOfWorkChangeSet");
    }

    @Override
    public void postMergeUnitOfWorkChangeSet(SessionEvent event) {
        log.trace("postMergeUnitOfWorkChangeSet");
    }

    @Override
    public void preDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
        log.trace("preDistributedMergeUnitOfWorkChangeSet");
    }

    @Override
    public void postDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
        log.trace("postDistributedMergeUnitOfWorkChangeSet");
    }

    @Override
    public void preReleaseUnitOfWork(SessionEvent event) {
        log.trace("preReleaseUnitOfWork");
    }

    @Override
    public void postReleaseUnitOfWork(SessionEvent event) {
        log.trace("postReleaseUnitOfWork");
    }

    @Override
    public void preExecuteQuery(SessionEvent event) {
        log.trace("preExecuteQuery");
    }

    @Override
    public void postExecuteQuery(SessionEvent event) {
        log.trace("postExecuteQuery");
    }

    @Override
    public void preBeginTransaction(SessionEvent event) {
        log.trace("preBeginTransaction");
    }

    @Override
    public void postBeginTransaction(SessionEvent event) {
        log.trace("postBeginTransaction");
    }

    @Override
    public void preCommitTransaction(SessionEvent event) {
        log.trace("preCommitTransaction");
    }

    @Override
    public void postCommitTransaction(SessionEvent event) {
        log.trace("postCommitTransaction");
    }

    @Override
    public void preCalculateUnitOfWorkChangeSet(SessionEvent event) {
        log.trace("preCalculateUnitOfWorkChangeSet");
    }

    @Override
    public void postCalculateUnitOfWorkChangeSet(SessionEvent event) {
        log.trace("postCalculateUnitOfWorkChangeSet");
    }

    @Override
    public void preCommitUnitOfWork(SessionEvent event) {
        log.trace("preCommitUnitOfWork");
    }

    @Override
    public void postCommitUnitOfWork(SessionEvent event) {
        log.trace("postCommitUnitOfWork");
    }

//
//----------------------------------------------------------------------------------
//
//
//----
//
    @Override
    public void postResumeUnitOfWork(SessionEvent event) {
        log.trace("postResumeUnitOfWork");
    }

    @Override
    public void postAcquireUnitOfWork(SessionEvent event) {
        log.trace("postAcquireUnitOfWork");
    }

    @Override
    public void postConnect(SessionEvent event) {
        log.trace("postConnect");
    }

    //-----------------------
    @Override
    public void prepareUnitOfWork(SessionEvent event) {
        log.trace("prepareUnitOfWork");
    }

    @Override
    public void outputParametersDetected(SessionEvent event) {
        log.trace("outputParametersDetected");
    }

    @Override
    public void noRowsModified(SessionEvent event) {
        log.trace("noRowsModified");
    }

    @Override
    public void moreRowsDetected(SessionEvent event) {
        log.trace("moreRowsDetected");
    }

    @Override
    public void missingDescriptor(SessionEvent event) {
        log.trace("missingDescriptor");
    }

}
