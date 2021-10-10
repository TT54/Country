package fr.tt54.country.objects.permissions;

import java.util.HashMap;
import java.util.Map;

public enum CountryPermission {

    INVITE_PLAYER("Allow to invite players in the faction."),
    OPEN_FACTION("Allow to open and close the faction."),
    CREATE_RANK("Allow to create new faction ranks."),
    DELETE_RANK("Allow to delete faction ranks."),
    SET_RANK("Allow to set a rank to a player."),
    EDIT_RANK("Allow to modify a faction rank."),
    EDIT_PERMISSIONS("Allow to modify rank permissions"),
    CLAIM("Allow to claim and unclaim chunks."),
    ACCESS_CLAIM("Allow to access players in claimed chunks."),
    BUILD("Allow to build in faction's claims."),
    OPEN_CHEST("Allow to open chests and containers in faction's claims."),
    USE_BUTTON("Use buttons and levers in faction's claims."),
    OPEN_DOOR("Open doors and trapdoors in faction's claims."),
    INTERACT("Interact with other blocks in faction's claims."),
    KICK("Kick players from the faction");

    private String description;

    CountryPermission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    private static Map<String, CountryPermission> permissions = new HashMap<>();

    static {
        for (CountryPermission countryPermission : CountryPermission.values()) {
            permissions.put(countryPermission.name(), countryPermission);
        }
    }

    public static CountryPermission getPermission(String name) {
        return permissions.get(name.toUpperCase());
    }

    public static Map<String, CountryPermission> getPermissions() {
        return permissions;
    }
}
