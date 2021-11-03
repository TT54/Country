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

public class CmdPlayer extends SubCommand {

    public CmdPlayer() {
        super("player", new String[]{"p"}, "Show country's informations of a player");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Main.getMessages().getMessage("notplayer"));
                return false;
            }
            Player player = (Player) sender;
            if (!CountryManager.hasCountry(player)) {
                sender.sendMessage(Main.getMessages().getMessage("nocountry"));
                return false;
            }
            CmdInfo.sendInfosMessage(CountryManager.getPlayerCountry(player), player);

            return true;
        } else if (args.length == 1) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
            if (!CountryManager.hasCountry(player)) {
                sender.sendMessage(Main.getMessages().getMessage("playernotinacountry", "%player%", args[0]));
                return false;
            }
            CmdInfo.sendInfosMessage(CountryManager.getPlayerCountry(player), sender);
            return true;
        }
        sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " [country]"));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.toUpperCase().startsWith(args[0].toUpperCase())).collect(Collectors.toList());
        }
        return null;
    }
}
