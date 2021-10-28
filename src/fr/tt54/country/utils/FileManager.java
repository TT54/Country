package fr.tt54.country.utils;

import fr.tt54.country.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

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

    public static void updateYmlFile(String internalName, String externalName) {
        File internal = getInternalFile(internalName);
        FileConfiguration internalFile = YamlConfiguration.loadConfiguration(internal);
        FileConfiguration externalFile = YamlConfiguration.loadConfiguration(getFile(externalName, Main.getInstance()));
        boolean edited = false;

        for (String key : internalFile.getKeys(false)) {
            if (externalFile.get(key) == null) {
                externalFile.set(key, internalFile.get(key));
                edited = true;
            }
        }
        externalFile.set("version", internalFile.get("version"));

        internal.delete();

        if (edited)
            saveFile(externalFile, externalName);
    }

    public static File getInternalFile(String fileName) {
        if (fileName != null && !fileName.equals("")) {
            String resourcePath = fileName + ".yml";
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = Main.getInstance().getResource(resourcePath);

            if (in != null) {
                File outFile = new File(Main.getInstance().getDataFolder(), "copy" + resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(Main.getInstance().getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (!outFile.exists())
                        outFile.createNewFile();

                    OutputStream out = new FileOutputStream(outFile);
                    byte[] buf = new byte[1024];

                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    out.close();
                    in.close();

                    return outFile;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new File(Main.getInstance().getDataFolder(), fileName + ".yml");
    }

}
