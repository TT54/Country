package fr.tt54.country;

import fr.tt54.country.cmd.CountryCommand;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.listener.ClaimListener;
import fr.tt54.country.listener.ConnectionListener;
import fr.tt54.country.listener.InventoryListener;
import fr.tt54.country.listener.PlayerListener;
import fr.tt54.country.manager.*;
import fr.tt54.country.objects.country.Rank;
import fr.tt54.country.utils.Messages;
import fr.tt54.country.utils.Permission;
import fr.tt54.country.utils.materials.MaterialUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin {

    public static final int MAX_COMMANDS_IN_HELP = 10;

    private static final Messages MESSAGES = new Messages();
    private static final String MESSAGES_VERSION = "1.0";
    private static final String CONFIG_VERSION = "1.0";

    private static Main instance;
    private static Economy econ = null;
    private static boolean economyEnable;

    private Logger log;
    private boolean loggingEnable;
    private double forfeitCost;
    private String noCountryName = "None";

    @Override
    public void onEnable() {
        this.log = this.getLogger();
        instance = this;

        this.reload();

        this.getCommand("country").setExecutor(new CountryCommand());
        this.getCommand("country").setTabCompleter(new CountryCommand());

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
        this.getServer().getPluginManager().registerEvents(new ClaimListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        SubCommand.registerCommands();

        log.info(getMessages().getMessage("pluginenable"));
    }

    @Override
    public void onDisable() {
        log.info(getMessages().getMessage("plugindisable"));
    }

    public void reload() {
        this.saveDefaultConfig();
        this.reloadConfig();
        MESSAGES.enable(this, this.getConfig().getString("messagenotfound"));
        Rank.loadDefaultRanks();
        CountryManager.enable();
        InviteManager.enable();
        ClaimManager.enable();
        RelationManager.enable();
        WarManager.enable();
        MaterialUtils.registerMaterials();

        this.noCountryName = this.getConfig().getString("nocountryname");
        this.loggingEnable = this.getConfig().getBoolean("enablelogging");
        economyEnable = this.getConfig().getBoolean("enable_economy");

        if (economyEnable) {
            if (!setupEconomy()) {
                log.warning("Vault is not found ! The economy system will be disabled !");
            } else {
                EconomyManager.enable();
                forfeitCost = this.getConfig().getDouble("forfeit_cost");
            }
        }

        if (!this.getConfig().getString("version").equalsIgnoreCase(CONFIG_VERSION)) {
            log.warning(MESSAGES.getMessage("badconfigversion", "%configversion%", this.getConfig().getString("configversion"), "%newversion%", CONFIG_VERSION));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Permission.hasPermission(player, "topluck.reload")) {
                    player.sendMessage("");
                    player.sendMessage(MESSAGES.getMessage("badconfigversion", "%configversion%", this.getConfig().getString("configversion"), "%newversion%", CONFIG_VERSION));
                }
            }
        }
        if (!MESSAGES.getMessage("version").equalsIgnoreCase(MESSAGES_VERSION)) {
            log.warning(MESSAGES.getMessage("badmessagesversion", "%messagesversion%", MESSAGES.getMessage("version"), "%newversion%", MESSAGES_VERSION));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Permission.hasPermission(player, "topluck.reload")) {
                    player.sendMessage("");
                    player.sendMessage(MESSAGES.getMessage("badmessagesversion", "%messagesversion%", MESSAGES.getMessage("version"), "%newversion%", MESSAGES_VERSION));
                }
            }
        }
    }

    public static Messages getMessages() {
        return MESSAGES;
    }

    public static Main getInstance() {
        return instance;
    }

    public String getNoCountryName() {
        return this.noCountryName;
    }

    public Logger getLog() {
        return this.log;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        economyEnable = econ != null;
        return econ != null;
    }

    public static boolean isEconomySetup() {
        return economyEnable;
    }

    public double getForfeitCost() {
        return forfeitCost;
    }

    public void log(String message) {
        if (this.loggingEnable)
            this.getLog().info(message);
    }

    public void logAlert(String message) {
        if (this.loggingEnable)
            this.getLog().warning(message);
    }

    public static Economy getEconomy() {
        return econ;
    }
}
