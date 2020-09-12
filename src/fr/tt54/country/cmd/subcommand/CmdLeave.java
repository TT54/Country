package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdLeave extends SubCommand {

    public CmdLeave() {
        super("leave", new String[]{"quit"}, "Leave your country", "country.leave");
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
        CountryManager.leaveCountry(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        return null;
    }

}
