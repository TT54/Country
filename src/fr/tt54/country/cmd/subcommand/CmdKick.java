package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.objects.permissions.CountryPermission;
import fr.tt54.country.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CmdKick extends SubCommand {
    public CmdKick() {
        super("kick", new String[]{}, "Allow to kick players from the country");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }
        Player player = (Player) sender;

        if (!CountryManager.hasCountry(player)) {
            player.sendMessage(Main.getMessages().getMessage("nocountry"));
            return false;
        }
        if (!Permission.hasCountryPermission(player, CountryPermission.KICK)) {
            player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " <player>"));
            return false;
        }

        OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
        if (CountryManager.hasCountry(p) && CountryManager.getPlayerCountry(p) == CountryManager.getPlayerCountry(player)) {
            if (CountryManager.getRank(p).getPower() < CountryManager.getRank(player).getPower()) {
                CountryManager.kickCountry(p, player);
                return true;
            }
            player.sendMessage(Main.getMessages().getMessage("cantkickwithmorepower"));
            return false;
        } else {
            player.sendMessage(Main.getMessages().getMessage("playernotinyourcountry"));
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (CountryManager.hasCountry(player)) {
                if (Permission.hasCountryPermission(player, CountryPermission.KICK)) {
                    if (args.length == 1)
                        return CountryManager.getPlayerCountry(player).getMembers().keySet().stream().map(OfflinePlayer::getName).filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
                }
            }
        }
        return null;
    }
}