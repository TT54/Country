package fr.tt54.country.cmd;

import fr.tt54.country.Main;
import fr.tt54.country.utils.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CountryCommand implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return SubCommand.helpCommand.onExecute(sender, SubCommand.helpCommand, SubCommand.helpCommand.getName(), new String[0]);
        } else if (SubCommand.existCommand(args[0])) {
            String[] cmdArgs = new String[args.length - 1];
            System.arraycopy(args, 1, cmdArgs, 0, args.length - 1);
            if (!Permission.hasPermission(sender, SubCommand.getCommand(args[0]).getPermission())) {
                sender.sendMessage(Main.getMessages().getMessage("notpermission"));
                return false;
            }
            return SubCommand.getCommand(args[0]).onExecute(sender, SubCommand.getCommand(args[0]), args[0], cmdArgs);
        }
        sender.sendMessage(Main.getMessages().getMessage("commandnotfound", "%cmd%", "/" + label + " " + args[0]));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> message = new ArrayList<>();
        if (args.length == 1) {
            message.addAll(SubCommand.commands.stream().filter(subCommand -> Permission.hasPermission(sender, subCommand.getPermission())).map(SubCommand::getName).collect(Collectors.toList()));
            final List<String> aliases = new ArrayList<>();
            SubCommand.commands.stream().filter(subCommand -> Permission.hasPermission(sender, subCommand.getPermission())).map(SubCommand::getAliases).forEach(strings -> aliases.addAll(Arrays.asList(strings)));
            message.addAll(aliases);
            message = message.stream().filter(name -> name.startsWith(args[0])).collect(Collectors.toList());
        } else if (args.length > 1 && SubCommand.existCommand(args[0])) {
            String[] cmdArgs = new String[args.length - 1];
            System.arraycopy(args, 1, cmdArgs, 0, args.length - 1);
            List<String> msg = SubCommand.getCommand(args[0]).onTabComplete(sender, SubCommand.getCommand(args[0]), args[0], cmdArgs);
            if (msg != null) {
                message.addAll(msg);
            }
        }
        return (message.isEmpty()) ? Collections.emptyList() : message;
    }
}
