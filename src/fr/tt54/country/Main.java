package fr.tt54.country;

import fr.tt54.country.cmd.CountryCommand;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.listener.ClaimListener;
import fr.tt54.country.listener.ConnectionListener;
import fr.tt54.country.listener.InventoryListener;
import fr.tt54.country.listener.PlayerListener;
import fr.tt54.country.manager.ClaimManager;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.manager.InviteManager;
import fr.tt54.country.objects.country.Rank;
import fr.tt54.country.utils.Messages;
import fr.tt54.country.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final int MAX_COMMANDS_IN_HELP = 10;

    private static final Messages MESSAGES = new Messages();
    private static final String MESSAGES_VERSION = "1.0";
    private static final String CONFIG_VERSION = "1.0";

    private static Main instance;

    private String noCountryName = "None";

    @Override
    public void onEnable() {
        instance = this;
        this.reload();

        this.getCommand("country").setExecutor(new CountryCommand());
        this.getCommand("country").setTabCompleter(new CountryCommand());

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
        this.getServer().getPluginManager().registerEvents(new ClaimListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        SubCommand.registerCommands();
    }

    @Override
    public void onDisable() {

    }

    public void reload() {
        this.saveDefaultConfig();
        this.reloadConfig();
        MESSAGES.enable(this, this.getConfig().getString("messagenotfound"));
        Rank.loadDefaultRanks();
        CountryManager.enable();
        InviteManager.enable();
        ClaimManager.enable();

        this.noCountryName = this.getConfig().getString("nocountryname");

        if (!this.getConfig().getString("version").equalsIgnoreCase(CONFIG_VERSION)) {
            System.out.println(MESSAGES.getMessage("badconfigversion", "%configversion%", this.getConfig().getString("configversion"), "%newversion%", CONFIG_VERSION));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Permission.hasPermission(player, "topluck.reload")) {
                    player.sendMessage("");
                    player.sendMessage(MESSAGES.getMessage("badconfigversion", "%configversion%", this.getConfig().getString("configversion"), "%newversion%", CONFIG_VERSION));
                }
            }
        }
        if (!MESSAGES.getMessage("version").equalsIgnoreCase(MESSAGES_VERSION)) {
            System.out.println(MESSAGES.getMessage("badmessagesversion", "%messagesversion%", MESSAGES.getMessage("version"), "%newversion%", MESSAGES_VERSION));
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
}
