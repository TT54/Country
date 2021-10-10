package fr.tt54.country.objects.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ClaimPermission {

    BUILD("Allow to build in this claim."),
    OPEN_CHEST("Allow to open chests and containers in this claim."),
    USE_BUTTON("Use buttons and levers in this claim."),
    OPEN_DOOR("Open doors and trapdoors in this claim."),
    INTERACT("Interact with other blocks in this claim.");


    private String description;

    ClaimPermission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

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
        return str.substring(0, Math.max(str.length() - 1, 0));
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
