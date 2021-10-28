package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CmdLeader extends SubCommand {

    public CmdLeader() {
        super("leader", new String[]{"lead"}, "Give to someone the leadership of your country");
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
        if (CountryManager.getPlayerCountry(player).getLeader().getUniqueId() != player.getUniqueId()) {
            player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " <player>"));
            return false;
        }

        OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
        if (p.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(Main.getMessages().getMessage("executeyourself"));
            return false;
        }
        if (CountryManager.hasCountry(p) && CountryManager.getPlayerCountry(p) == CountryManager.getPlayerCountry(player)) {
            CountryManager.changeLeader(CountryManager.getPlayerCountry(player), p);
        } else {
            player.sendMessage(Main.getMessages().getMessage("playernotinyourcountry"));
            return false;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (CountryManager.hasCountry(player)) {
                if (CountryManager.getPlayerCountry(player).getLeader().getUniqueId() == player.getUniqueId()) {
                    if (args.length == 1)
                        return CountryManager.getPlayerCountry(player).getMembers().keySet().stream().map(OfflinePlayer::getName).filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
                }
            }
        }
        return null;
    }
}
