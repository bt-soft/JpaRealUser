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
 *
 * @author BT
 */
public class ThreadLocalMap {

    /**
     * ThreadLocal Map
     */
    private static final ThreadLocal<Map<String, Object>> THREADLOCAL_MAP = new ThreadLocal<>();

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
     * ThreadLocal Map bejegyzés törlése kulcs alapján
     *
     * @param key map kulcs
     */
    public static void remove(String key) {
        if (THREADLOCAL_MAP.get() != null && THREADLOCAL_MAP.get().containsKey(key)) {
            THREADLOCAL_MAP.get().remove(key);
        }
    }

    /**
     * ThreadLocal Map törlése
     */
    public static void clearAll() {
        if (THREADLOCAL_MAP.get() != null) {
            THREADLOCAL_MAP.get().clear();
        }
    }
}
