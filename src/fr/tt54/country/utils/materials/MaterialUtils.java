package fr.tt54.country.utils.materials;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MaterialUtils {

    private static Map<MaterialCategory, List<InterVersionMaterial>> materials = new HashMap<>();
    private static Map<String, List<InterVersionMaterial>> materialsWithName = new HashMap<>();

    public static void registerMaterials() {
        registerMaterial(new InterVersionMaterial("BUTTON", MaterialCategory.BUTTON, "WOOD_BUTTON", -1, 12));
        registerMaterial(new InterVersionMaterial("BUTTON", MaterialCategory.BUTTON, "OAK_BUTTON", 13, -1));
        registerMaterial(new InterVersionMaterial("BIRCH_BUTTON", MaterialCategory.BUTTON, "BIRCH_BUTTON", 13, -1));
        registerMaterial(new InterVersionMaterial("ACACIA_BUTTON", MaterialCategory.BUTTON, "ACACIA_BUTTON", 13, -1));
        registerMaterial(new InterVersionMaterial("DARK_OAK_BUTTON", MaterialCategory.BUTTON, "DARK_OAK_BUTTON", 13, -1));
        registerMaterial(new InterVersionMaterial("JUNGLE_BUTTON", MaterialCategory.BUTTON, "JUNGLE_BUTTON", 13, -1));
        registerMaterial(new InterVersionMaterial("SPRUCE_BUTTON", MaterialCategory.BUTTON, "SPRUCE_BUTTON", 13, -1));
        registerMaterial(new InterVersionMaterial("WARPED_BUTTON", MaterialCategory.BUTTON, "WARPED_BUTTON", 16, -1));
        registerMaterial(new InterVersionMaterial("CRIMSON_BUTTON", MaterialCategory.BUTTON, "CRIMSON_BUTTON", 16, -1));
        registerMaterial(new InterVersionMaterial("POLISHED_BLACKSTONE_BUTTON", MaterialCategory.BUTTON, "POLISHED_BLACKSTONE_BUTTON", 16, -1));

        registerMaterial(new InterVersionMaterial("FIREBALL", MaterialCategory.ITEM, "FIREBALL", -1, 12));
        registerMaterial(new InterVersionMaterial("FIREBALL", MaterialCategory.ITEM, "FIRE_CHARGE", 13, -1));

        registerMaterial(new InterVersionMaterial("TRAPDOOR", MaterialCategory.DOOR, "TRAP_DOOR", -1, 12));
        registerMaterial(new InterVersionMaterial("TRAPDOOR", MaterialCategory.DOOR, "OAK_TRAPDOOR", 13, -1));
        registerMaterial(new InterVersionMaterial("ACACIA_TRAPDOOR", MaterialCategory.DOOR, "ACACIA_TRAPDOOR", 13, -1));
        registerMaterial(new InterVersionMaterial("BIRCH_TRAPDOOR", MaterialCategory.DOOR, "BIRCH_TRAPDOOR", 13, -1));
        registerMaterial(new InterVersionMaterial("DARK_OAK_TRAPDOOR", MaterialCategory.DOOR, "DARK_OAK_TRAPDOOR", 13, -1));
        registerMaterial(new InterVersionMaterial("JUNGLE_TRAPDOOR", MaterialCategory.DOOR, "JUNGLE_TRAPDOOR", 13, -1));
        registerMaterial(new InterVersionMaterial("SPRUCE_TRAPDOOR", MaterialCategory.DOOR, "SPRUCE_TRAPDOOR", 13, -1));
        registerMaterial(new InterVersionMaterial("CRIMSON_TRAPDOOR", MaterialCategory.DOOR, "CRIMSON_TRAPDOOR", 16, -1));
        registerMaterial(new InterVersionMaterial("WARPED_TRAPDOOR", MaterialCategory.DOOR, "WARPED_TRAPDOOR", 16, -1));

        registerMaterial(new InterVersionMaterial("DOOR", MaterialCategory.DOOR, "WOODEN_DOOR", -1, 12));
        registerMaterial(new InterVersionMaterial("DOOR", MaterialCategory.DOOR, "OAK_DOOR", 13, -1));
        registerMaterial(new InterVersionMaterial("DARK_OAK_DOOR", MaterialCategory.DOOR, "DARK_OAK_DOOR", 8, -1));
        registerMaterial(new InterVersionMaterial("ACACIA_DOOR", MaterialCategory.DOOR, "ACACIA_DOOR", 8, -1));
        registerMaterial(new InterVersionMaterial("BIRCH_DOOR", MaterialCategory.DOOR, "BIRCH_DOOR", 8, -1));
        registerMaterial(new InterVersionMaterial("JUNGLE_DOOR", MaterialCategory.DOOR, "JUNGLE_DOOR", 8, -1));
        registerMaterial(new InterVersionMaterial("SPRUCE_DOOR", MaterialCategory.DOOR, "SPRUCE_DOOR", 8, -1));
        registerMaterial(new InterVersionMaterial("CRIMSON_DOOR", MaterialCategory.DOOR, "CRIMSON_DOOR", 16, -1));
        registerMaterial(new InterVersionMaterial("WARPED_DOOR", MaterialCategory.DOOR, "WARPED_DOOR", 16, -1));

        registerMaterial(new InterVersionMaterial("FENCE_GATE", MaterialCategory.DOOR, "FENCE_GATE", -1, 12));
        registerMaterial(new InterVersionMaterial("FENCE_GATE", MaterialCategory.DOOR, "OAK_FENCE_GATE", 8, -1));
        registerMaterial(new InterVersionMaterial("SPRUCE_FENCE_GATE", MaterialCategory.DOOR, "SPRUCE_FENCE_GATE", 8, -1));
        registerMaterial(new InterVersionMaterial("DARK_OAK_FENCE_GATE", MaterialCategory.DOOR, "DARK_OAK_FENCE_GATE", 8, -1));
        registerMaterial(new InterVersionMaterial("JUNGLE_FENCE_GATE", MaterialCategory.DOOR, "JUNGLE_FENCE_GATE", 8, -1));
        registerMaterial(new InterVersionMaterial("BIRCH_FENCE_GATE", MaterialCategory.DOOR, "BIRCH_FENCE_GATE", 8, -1));
        registerMaterial(new InterVersionMaterial("ACACIA_FENCE_GATE", MaterialCategory.DOOR, "ACACIA_FENCE_GATE", 8, -1));
        registerMaterial(new InterVersionMaterial("CRIMSON_FENCE_GATE", MaterialCategory.DOOR, "CRIMSON_FENCE_GATE", 16, -1));
        registerMaterial(new InterVersionMaterial("WARPED_FENCE_GATE", MaterialCategory.DOOR, "WARPED_FENCE_GATE", 16, -1));
    }

    private static void registerMaterial(InterVersionMaterial material) {
        List<InterVersionMaterial> mat = materials.getOrDefault(material.getMaterialCategory(), new ArrayList<>());
        mat.add(material);
        materials.put(material.getMaterialCategory(), mat);

        mat = materialsWithName.getOrDefault(material.getName(), new ArrayList<>());
        mat.add(material);
        materialsWithName.put(material.getName(), mat);
    }

    public static boolean existInVersion(String material, String version) {
        try {
            return existInVersion(material, Integer.parseInt(version.split("\\.")[1]));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getActualVersion() {
        try {
            return Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public static boolean existInVersion(String material, int version) {
        /*List<InterVersionMaterial> mats = materialsWithName.get(material);
        for(InterVersionMaterial mat : mats) {
            if (mat == null)
                continue;
            if (mat.getStartVersion() <= version && mat.getEndVersion() >= version)
                return true;
            if (mat.getStartVersion() < 0 && mat.getEndVersion() >= version)
                return true;
            if(mat.getStartVersion() <= version && mat.getEndVersion() < 0)
                return true;
        }*/
        return getMaterial(material, version) != Material.AIR;
    }

    public static List<InterVersionMaterial> getMaterials(String name) {
        return materialsWithName.getOrDefault(name, new ArrayList<>());
    }

    public static Material getMaterial(String name, int version) {
        for (InterVersionMaterial mat : getMaterials(name)) {
            if (mat == null)
                continue;
            if (mat.getStartVersion() <= version && mat.getEndVersion() >= version)
                return Material.getMaterial(mat.getMaterialName());
            if (mat.getStartVersion() < 0 && mat.getEndVersion() >= version)
                return Material.getMaterial(mat.getMaterialName());
            if (mat.getStartVersion() <= version && mat.getEndVersion() < 0)
                return Material.getMaterial(mat.getMaterialName());
        }
        return Material.AIR;
    }

    public static List<InterVersionMaterial> getCategoryMaterials(MaterialCategory category) {
        return materials.getOrDefault(category, new ArrayList<>());
    }

    public static List<InterVersionMaterial> getCategoryMaterialsInVersion(MaterialCategory category, int version) {
        return getCategoryMaterials(category).stream().filter(material -> existInVersion(material.getName(), version)).collect(Collectors.toList());
    }

    public static List<Material> getMaterialsInVersion(MaterialCategory category, int version) {
        return getCategoryMaterialsInVersion(category, version).stream().map(material -> Material.getMaterial(material.getMaterialName())).collect(Collectors.toList());
    }
}
