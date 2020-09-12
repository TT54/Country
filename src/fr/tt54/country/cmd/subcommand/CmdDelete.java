package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdDelete extends SubCommand {

    public CmdDelete() {
        super("delete", new String[]{"disband"}, "Delete your own country");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }
        Player player = (Player) sender;
        if (args.length != 0) {
            player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command));
            return false;
        }
        if (!CountryManager.hasCountry(player)) {
            player.sendMessage(Main.getMessages().getMessage("nocountry"));
            return false;
        }
        if (!CountryManager.getPlayerCountry(player).getLeader().getUniqueId().toString().equals(player.getUniqueId().toString())) {
            player.sendMessage(Main.getMessages().getMessage("notleader"));
            return false;
        }
        CountryManager.removeCountry(player, CountryManager.getPlayerCountry(player).getUuid());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        return null;
    }
}
