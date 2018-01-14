/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    ThreadLocalMap.java
 *  Created: 2018.01.14. 17:40:44
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.jru.core.jsf;

import java.util.HashMap;
import java.util.Map;

/**
 * Threadlocal Map implementáció
 *
 * Ötlet:
 *
 * @see
 * https://github.com/dlee0113/java_ee_patterns_and_best_practices/blob/master/ContextHolder/src/java/com/abien/patterns/kitchensink/contextholder/threadlocal/control/ServiceThreadLocal.java
 *
 * @author BT
 */
public class ThreadLocalMap {

    public static final String KEY_CLIENT_ID = "client_identifier";

    /**
     * ThreadLocal Map
     */
    private static final ThreadLocal<Map<String, Object>> THREADLOCAL_MAP = new ThreadLocal<Map<String, Object>>();

    /**
     * privát konstruktor
     */
    private ThreadLocalMap() {
    }

    /**
     * ThreadLocal Map get
     *
     * @param key   map kulcs
     * @param value érték
     */
    public static void put(String key, Object value) {
        if (THREADLOCAL_MAP.get() == null) {
            THREADLOCAL_MAP.set(new HashMap<>());
        }
        THREADLOCAL_MAP.get().put(key, value);
    }

    /**
     * ThreadLocal Map put
     *
     * @param key map kulcs
     *
     * @return érték
     */
    public static Object get(String key) {
        return THREADLOCAL_MAP.get() != null ? THREADLOCAL_MAP.get().get(key) : null;
    }

    /**
     * ThreadLocal Map törlése
     */
    public static void removeAll() {
        THREADLOCAL_MAP.remove();
    }
}
