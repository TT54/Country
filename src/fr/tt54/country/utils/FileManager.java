package fr.tt54.country.utils;

import fr.tt54.country.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FileManager {

    private static void createFile(String name, JavaPlugin plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getFile(String name, JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.exists()) {
            createFile(name, plugin);
        }

        return file;
    }

    public static FileConfiguration getYmlFile(String fileName) {
        return YamlConfiguration.loadConfiguration(getFile(fileName, Main.getInstance()));
    }

    public static void saveFile(FileConfiguration file, String fileName) {
        try {
            file.save(getFile(fileName, Main.getInstance()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getInternalFile(String fileName) {
        URL url = FileManager.class.getClassLoader().getResource(fileName);
        if (url != null)
            return new File(url.getFile());
        return null;
    }

}
