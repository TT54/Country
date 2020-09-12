package fr.tt54.country.utils;

import java.util.HashMap;
import java.util.Map;

public enum CountryPermission {

    INVITE_PLAYER,
    OPEN_FACTION,
    BUILD,
    CREATE_RANK,
    DELETE_RANK,
    SET_RANK,
    EDIT_RANK;

    private static Map<String, CountryPermission> permissions = new HashMap<>();

    static {
        for (CountryPermission countryPermission : CountryPermission.values()) {
            permissions.put(countryPermission.name(), countryPermission);
        }
    }

    public static CountryPermission getPermission(String name) {
        return permissions.get(name);
    }

    public static Map<String, CountryPermission> getPermissions() {
        return permissions;
    }
}
