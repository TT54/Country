package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.utils.Permission;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CmdHelp extends SubCommand {

    public CmdHelp() {
        super("help", new String[]{"?"}, "Display the help message");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        sender.sendMessage("");
        int page = 0;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException ignore) {
            }
        }
        if (page < 0)
            page = 0;
        sender.sendMessage("§2---- HELP [" + (page + 1) + "]----");
        for (String str : getHelpMessage(sender, page)) {
            sender.sendMessage(str);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        return Collections.emptyList();
    }

    public static List<String> getHelpMessage(CommandSender sender, int page) {
        List<String> message = new ArrayList<>();
        for (SubCommand subCommand : SubCommand.commands) {
            if (Permission.hasPermission(sender, subCommand.getPermission()))
                message.add("§2" + subCommand.getName() + " " + Arrays.toString(subCommand.getAliases()) + ": §a" + subCommand.getDescription());
        }
        if ((page * Main.MAX_COMMANDS_IN_HELP) >= message.size())
            page = ((message.size() - 1) / Main.MAX_COMMANDS_IN_HELP);
        if (page < 0)
            page = 0;
        int max = message.size() - (page * Main.MAX_COMMANDS_IN_HELP);
        return message.subList(page * Main.MAX_COMMANDS_IN_HELP, (page * Main.MAX_COMMANDS_IN_HELP) + Math.min(max, Main.MAX_COMMANDS_IN_HELP));
    }
}
