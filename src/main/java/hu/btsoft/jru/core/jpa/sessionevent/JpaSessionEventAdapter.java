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

////<editor-fold defaultstate="collapsed" desc="Csak logoló, amúgy másra nem használt események">
////
////-------------------------------------------------------------------
////
//    /**
//     * PUBLIC:
//     * This event is raised on the client session after creation/acquiring.
//     *
//     * @param event
//     */
//    @Override
//    public void postAcquireClientSession(SessionEvent event) {
//        log.trace("postAcquireClientSession: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    /**
//     * PUBLIC:
//     * This event is raised on the client session before releasing.
//     *
//     * @param event
//     */
//    @Override
//    public void preReleaseClientSession(SessionEvent event) {
//        log.trace("preReleaseClientSession: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
////
////-------------------------------------------------------------------
////
//
//    @Override
//    public void postAcquireExclusiveConnection(SessionEvent event) {
//        log.trace("postAcquireExclusiveConnection: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void preReleaseExclusiveConnection(SessionEvent event) {
//        log.trace("preReleaseExclusiveConnection: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void preLogin(SessionEvent event) {
//        log.trace("preLogin: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postLogin(SessionEvent event) {
//        log.trace("postLogin: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void preLogout(SessionEvent event) {
//        log.trace("preLogout: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postLogout(SessionEvent event) {
//        log.trace("postLogout: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void preRollbackTransaction(SessionEvent event) {
//        log.trace("preRollbackTransaction: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postRollbackTransaction(SessionEvent event) {
//        log.trace("postRollbackTransaction: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void preMergeUnitOfWorkChangeSet(SessionEvent event) {
//        log.trace("preMergeUnitOfWorkChangeSet: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postMergeUnitOfWorkChangeSet(SessionEvent event) {
//        log.trace("postMergeUnitOfWorkChangeSet: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void preDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
//        log.trace("preDistributedMergeUnitOfWorkChangeSet: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
//        log.trace("postDistributedMergeUnitOfWorkChangeSet: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void preReleaseUnitOfWork(SessionEvent event) {
//        log.trace("preReleaseUnitOfWork: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postReleaseUnitOfWork(SessionEvent event) {
//        log.trace("postReleaseUnitOfWork: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void preExecuteQuery(SessionEvent event) {
//        log.trace("preExecuteQuery: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postExecuteQuery(SessionEvent event) {
//        log.trace("postExecuteQuery: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void preBeginTransaction(SessionEvent event) {
//        log.trace("preBeginTransaction: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postBeginTransaction(SessionEvent event) {
//        log.trace("postBeginTransaction: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void preCommitTransaction(SessionEvent event) {
//        log.trace("preCommitTransaction: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postCommitTransaction(SessionEvent event) {
//        log.trace("postCommitTransaction: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void preCalculateUnitOfWorkChangeSet(SessionEvent event) {
//        log.trace("preCalculateUnitOfWorkChangeSet: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postCalculateUnitOfWorkChangeSet(SessionEvent event) {
//        log.trace("postCalculateUnitOfWorkChangeSet: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void preCommitUnitOfWork(SessionEvent event) {
//        log.trace("preCommitUnitOfWork: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postCommitUnitOfWork(SessionEvent event) {
//        log.trace("postCommitUnitOfWork: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
////
////----------------------------------------------------------------------------------
////
////
////----
////
//    @Override
//    public void postResumeUnitOfWork(SessionEvent event) {
//        log.trace("postResumeUnitOfWork: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postAcquireUnitOfWork(SessionEvent event) {
//        log.trace("postAcquireUnitOfWork: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void postConnect(SessionEvent event) {
//        log.trace("postConnect: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    //-----------------------
//    @Override
//    public void prepareUnitOfWork(SessionEvent event) {
//        log.trace("prepareUnitOfWork: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void outputParametersDetected(SessionEvent event) {
//        log.trace("outputParametersDetected: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void noRowsModified(SessionEvent event) {
//        log.trace("noRowsModified: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void moreRowsDetected(SessionEvent event) {
//        log.trace("moreRowsDetected: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
//
//    @Override
//    public void missingDescriptor(SessionEvent event) {
//        log.trace("missingDescriptor: {}", (String) ThreadLocalMap.get(KEY_CLIENT_ID));
//    }
////</editor-fold>
}
