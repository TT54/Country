package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdCreate extends SubCommand {

    public CmdCreate() {
        super("create", new String[]{"c"}, "Create a country", "country.create");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " <name>"));
            return false;
        }
        CountryManager.createCountry(player, args[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        return null;
    }
}
