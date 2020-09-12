package fr.tt54.country.utils;

import fr.tt54.country.manager.CountryManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class Permission {

    public static boolean hasPermission(CommandSender sender, String permission) {
        if (permission == null)
            return true;
        String parts[] = permission.split("\\.");
        for (int i = 0; i < parts.length - 1; i++) {
            StringBuilder splitPerm = new StringBuilder();
            for (int j = 0; j <= i; j++) {
                splitPerm.append(parts[j]).append(".");
            }
            splitPerm.append("*");
            if (sender.hasPermission(splitPerm.toString()))
                return true;
        }
        if (sender.hasPermission(permission))
            return true;
        return sender.hasPermission("*");
    }

    public static boolean hasFactionPermission(OfflinePlayer player, CountryPermission countryPermission) {
        if (CountryManager.hasCountry(player) && CountryManager.getRank(player) != null) {
            return CountryManager.getRank(player).hasPermission(countryPermission);
        }
        return false;
    }
}