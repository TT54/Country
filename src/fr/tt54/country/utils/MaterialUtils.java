package fr.tt54.country.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public enum MaterialUtils {

    AIR("AIR", "AIR"),
    WOODEN_DOOR("WOODEN_DOOR", "OAK_DOOR"),
    TRAPDOOR("TRAP_DOOR", "OAK_TRAPDOOR"),
    FENCE_GATE("FENCE_GATE", "OAK_FENCE_GATE"),
    FIREBALL("FIREBALL", "FIRE_CHARGE"),
    BUTTON("WOOD_BUTTON", "OAK_BUTTON");

    private String ancientName;
    private String newName;
    private static Map<String, MaterialUtils> materials = new HashMap<>();

    static {
        for (MaterialUtils mat : MaterialUtils.values()) {
            materials.put(mat.getAncientName(), mat);
            materials.put(mat.getNewName(), mat);
        }
    }

    public static Material getNewMaterial(MaterialUtils material) {
        return Material.getMaterial(material.getNewName());
    }

    public static Material getNewMaterial(String name) {
        return Material.getMaterial(materials.getOrDefault(name, AIR).getNewName());
    }

    public static Material getAncientMaterial(MaterialUtils material) {
        return Material.getMaterial(material.getAncientName());
    }

    public static Material getAncientMaterial(String name) {
        return Material.getMaterial(materials.getOrDefault(name, AIR).getAncientName());
    }

    public static Material getMaterial(String name) {
        return Material.getMaterial(name);
    }

    public static Material getMaterial(MaterialUtils mat) {
        return (Integer.parseInt(Bukkit.getVersion().split("\\.")[1]) < 13) ? Material.getMaterial(mat.getAncientName()) : Material.getMaterial(mat.getNewName());
    }

    MaterialUtils(String ancientName, String newName) {
        this.ancientName = ancientName;
        this.newName = newName;
    }

    public String getAncientName() {
        return ancientName;
    }

    public String getNewName() {
        return newName;
    }
}
