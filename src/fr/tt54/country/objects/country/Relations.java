package fr.tt54.country.objects.country;

import java.util.HashMap;
import java.util.Map;

public enum Relations {

    IN_WAR("In War", "§c"),
    ENEMY("Enemy", "§c"),
    NEUTRAL("Neutral", "§7"),
    ENTENTE("Entente", "§e"),
    ALLY("Ally", "§d");

    private static Map<String, Relations> relationsMap = new HashMap<>();

    static {
        for (Relations rel : Relations.values()) {
            relationsMap.put(rel.name().toUpperCase(), rel);
        }
    }

    public static Relations getRelation(String name) {
        return relationsMap.getOrDefault(name.toUpperCase(), NEUTRAL);
    }

    private String display;
    private String color;

    Relations(String display, String color) {
        this.display = display;
        this.color = color;
    }

    public String getDisplay() {
        return display;
    }

    public String getColor() {
        return color;
    }
}
