package com.uguke.permission;

import java.util.HashMap;
import java.util.Map;

final class HiCache {

    private static HiCache instance;

    static HiCache getInstance() {
        if (instance == null)
            instance = new HiCache();
        return instance;
    }

    private Map<String, HiPermission> map;

    private HiCache() {
        map = new HashMap<>();
    }

    synchronized void put(String key, HiPermission hiPermission) {
        map.put(key, hiPermission);
    }

    synchronized HiPermission get(String key) {
        return map.get(key);
    }

    synchronized void remove(String key) {
        map.remove(key);
    }
}
