package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.utils.CountryPermission;
import fr.tt54.country.utils.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdClose extends SubCommand {

    public CmdClose() {
        super("close", new String[]{}, "Disallow players to join your country without an invitation");
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
        if (!Permission.hasFactionPermission(player, CountryPermission.OPEN_FACTION)) {
            player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
            return false;
        }
        if (!CountryManager.getPlayerCountry(player).isOpened()) {
            player.sendMessage(Main.getMessages().getMessage("alreadyclosed"));
            return false;
        }
        CountryManager.closeCountry(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        return null;
    }

}
