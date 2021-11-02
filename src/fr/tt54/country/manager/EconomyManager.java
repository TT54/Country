package fr.tt54.country.manager;

import fr.tt54.country.Main;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.utils.FileManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EconomyManager {

    private static FileConfiguration economyFile;

    public static void enable() {
        reloadEconomy();
    }

    private static void reloadEconomy() {
        if (Main.isEconomySetup()) {
            economyFile = FileManager.getYmlFile("economy");
            for (String uuid : economyFile.getKeys(false)) {
                Country country = CountryManager.getCountry(UUID.fromString(uuid));
                if (country != null) country.setMoney(economyFile.getDouble(uuid + ".money"));
            }
        }
    }

    public static boolean playerDeposit(Player player, Country country, double amount) {
        if (Main.isEconomySetup()) {
            Economy economy = Main.getEconomy();
            if (economy.has(player, amount) && country.getMoney() + amount <= country.getMaxMoney()) {
                economy.withdrawPlayer(player, amount);
                country.addMoney(amount);
                saveMoney(country);
                return true;
            }
        }
        return false;
    }

    public static boolean playerWithdraw(Player player, Country country, double amount) {
        if (Main.isEconomySetup()) {
            Economy economy = Main.getEconomy();
            if (country.getMoney() >= amount) {
                economy.depositPlayer(player, amount);
                country.removeMoney(amount);
                saveMoney(country);
                return true;
            }
        }
        return false;
    }

    public static void saveMoney(Country country) {
        economyFile.set(country.getUuid().toString() + ".money", country.getMoney());
        saveEconomy();
    }

    private static void saveEconomy() {
        FileManager.saveFile(economyFile, "economy");
    }
}
