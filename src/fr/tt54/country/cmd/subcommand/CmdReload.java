package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CmdReload extends SubCommand {

    public CmdReload() {
        super("reload", new String[]{"rl"}, "Reload the plugin", "country.reload");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        Main.getInstance().reload();
        sender.sendMessage(Main.getMessages().getMessage("reload"));
        System.out.println(Main.getMessages().getMessage("reload"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        return null;
    }
}
