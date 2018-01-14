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
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventListener;

/**
 *
 *
 *
 *
 * @author BT
 */
@Slf4j
public class JpaSessionEventAdapter /*extends SessionEventAdapter*/ implements SessionEventListener {

// org.eclipse.persistence.internal.sessions.AbstractSession:
//
//    /**
//     * INTERNAL:
//     * This method rises appropriate for the session event(s)
//     * right after connection is acquired.
//     */
//    public void postAcquireConnection(Accessor accessor) {
//        if (getProject().hasVPDIdentifier(this)) {
//            if (getPlatform().supportsVPD()) {
//                DatabaseQuery query = getPlatform().getVPDSetIdentifierQuery(getProject().getVPDIdentifier());
//                List argValues = new ArrayList();
//                query.addArgument(getProject().getVPDIdentifier());
//                argValues.add(getProperty(getProject().getVPDIdentifier()));
//                executeQuery(query, argValues);
//            } else {
//                throw ValidationException.vpdNotSupported(getPlatform().getClass().getName());
//            }
//        }
//
//        if (this.eventManager != null) {
//            this.eventManager.postAcquireConnection(accessor);
//        }
//    }
//
//    /**
//     * INTERNAL:
//     * This method rises appropriate for the session event(s)
//     * right before the connection is released.
//     */
//    public void preReleaseConnection(Accessor accessor) {
//        if (getProject().hasVPDIdentifier(this)) {
//            if (getPlatform().supportsVPD()) {
//                DatabaseQuery query = getPlatform().getVPDClearIdentifierQuery(getProject().getVPDIdentifier());
//                List argValues = new ArrayList();
//                query.addArgument(getProject().getVPDIdentifier());
//                argValues.add(getProperty(getProject().getVPDIdentifier()));
//                executeQuery(query, argValues);
//            } else {
//                throw ValidationException.vpdNotSupported(getPlatform().getClass().getName());
//            }
//        }
//
//        if (this.eventManager != null) {
//            this.eventManager.preReleaseConnection(accessor);
//        }
//    }
//
    /**
     * Client ID beállítása
     *
     * @param event event
     */
    private void setClientId(SessionEvent event) {
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
     * Client ID törlése
     *
     * @param event event
     */
    private void clearClientid(SessionEvent event) {

//        Session session = (Session) event.getSession();
//        String clientIdentifier = (String) session.getProperty("client_identifier");
//
        String clientIdentifier = (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID);
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
        ThreadLocalMap.remove(ThreadLocalMap.KEY_CLIENT_ID);
        log.trace("postReleaseClientSession: ThreadLocalMap.KEY_CLIENT_ID törölve -> {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));

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
        log.trace("postAcquireConnection: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));

        setClientId(event);

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

        clearClientid(event);

    }

//
//-------------------------------------------------------------------
//
    @Override
    public void postAcquireExclusiveConnection(SessionEvent event) {
        log.trace("postAcquireExclusiveConnection: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void preReleaseExclusiveConnection(SessionEvent event) {
        log.trace("preReleaseExclusiveConnection: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void preLogin(SessionEvent event) {
        log.trace("preLogin: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postLogin(SessionEvent event) {
        log.trace("postLogin: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void preLogout(SessionEvent event) {
        log.trace("preLogout: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postLogout(SessionEvent event) {
        log.trace("postLogout: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void preRollbackTransaction(SessionEvent event) {
        log.trace("preRollbackTransaction: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postRollbackTransaction(SessionEvent event) {
        log.trace("postRollbackTransaction: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void preMergeUnitOfWorkChangeSet(SessionEvent event) {
        log.trace("preMergeUnitOfWorkChangeSet: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postMergeUnitOfWorkChangeSet(SessionEvent event) {
        log.trace("postMergeUnitOfWorkChangeSet: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void preDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
        log.trace("preDistributedMergeUnitOfWorkChangeSet: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
        log.trace("postDistributedMergeUnitOfWorkChangeSet: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void preReleaseUnitOfWork(SessionEvent event) {
        log.trace("preReleaseUnitOfWork: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postReleaseUnitOfWork(SessionEvent event) {
        log.trace("postReleaseUnitOfWork: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void preExecuteQuery(SessionEvent event) {
        log.trace("preExecuteQuery: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postExecuteQuery(SessionEvent event) {
        log.trace("postExecuteQuery: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void preBeginTransaction(SessionEvent event) {
        log.trace("preBeginTransaction: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postBeginTransaction(SessionEvent event) {
        log.trace("postBeginTransaction: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void preCommitTransaction(SessionEvent event) {
        log.trace("preCommitTransaction: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postCommitTransaction(SessionEvent event) {
        log.trace("postCommitTransaction: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void preCalculateUnitOfWorkChangeSet(SessionEvent event) {
        log.trace("preCalculateUnitOfWorkChangeSet: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postCalculateUnitOfWorkChangeSet(SessionEvent event) {
        log.trace("postCalculateUnitOfWorkChangeSet: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void preCommitUnitOfWork(SessionEvent event) {
        log.trace("preCommitUnitOfWork: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postCommitUnitOfWork(SessionEvent event) {
        log.trace("postCommitUnitOfWork: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

//
//----------------------------------------------------------------------------------
//
//
//----
//
    @Override
    public void postResumeUnitOfWork(SessionEvent event) {
        log.trace("postResumeUnitOfWork: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postAcquireUnitOfWork(SessionEvent event) {
        log.trace("postAcquireUnitOfWork: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void postConnect(SessionEvent event) {
        log.trace("postConnect: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    //-----------------------
    @Override
    public void prepareUnitOfWork(SessionEvent event) {
        log.trace("prepareUnitOfWork: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void outputParametersDetected(SessionEvent event) {
        log.trace("outputParametersDetected: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void noRowsModified(SessionEvent event) {
        log.trace("noRowsModified: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void moreRowsDetected(SessionEvent event) {
        log.trace("moreRowsDetected: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

    @Override
    public void missingDescriptor(SessionEvent event) {
        log.trace("missingDescriptor: {}", (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID));
    }

}
