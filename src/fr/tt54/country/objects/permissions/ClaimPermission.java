package fr.tt54.country.objects.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ClaimPermission {

    OPEN_CHEST,
    BUILD,
    USE_BUTTON,
    USE_PRESSURE_PLATE,
    INTERACT;

    private static Map<String, ClaimPermission> permissions = new HashMap<>();

    static {
        for (ClaimPermission countryPermission : ClaimPermission.values()) {
            permissions.put(countryPermission.name(), countryPermission);
        }
    }

    public static ClaimPermission getPermission(String name) {
        return permissions.get(name);
    }

    public static boolean existPermission(String name) {
        return permissions.containsKey(name);
    }

    public static Map<String, ClaimPermission> getPermissions() {
        return permissions;
    }

    public static String getStringFromList(List<ClaimPermission> list) {
        String str = "";
        for (ClaimPermission permission : list) {
            str += permission.name() + ",";
        }
        return str.substring(0, str.length() - 1);
    }

    public static List<ClaimPermission> getListFromString(String str) {
        String splitted[] = str.split(",");
        List<ClaimPermission> permissions = new ArrayList<>();
        for (String perm : splitted) {
            if (existPermission(perm)) {
                permissions.add(getPermission(perm));
            }
        }

        return permissions;
    }
}
