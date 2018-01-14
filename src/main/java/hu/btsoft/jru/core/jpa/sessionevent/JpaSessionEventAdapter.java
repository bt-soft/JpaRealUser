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
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/**
 *
 * @author BT
 */
@Slf4j
public class JpaSessionEventAdapter extends SessionEventAdapter {

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

        String clientIdentifier = (String) ThreadLocalMap.get(ThreadLocalMap.KEY_CLIENT_ID);
        if (clientIdentifier != null) {

            Session session = (Session) event.getSession();

            DatabaseAccessor accessor = (DatabaseAccessor) event.getResult();
            accessor.incrementCallCount((AbstractSession) session);

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
        log.trace("preReleaseConnection");

        Session session = (Session) event.getSession();
        String clientIdentifier = (String) session.getProperty("client_identifier");

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

            accessor.decrementCallCount();
        }

    }

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
