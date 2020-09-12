package fr.tt54.country.cmd;

import fr.tt54.country.cmd.subcommand.*;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {

    private String name;
    private String description;
    private String[] aliases;
    private String permission;
    public static List<SubCommand> commands = new ArrayList<>();
    public static SubCommand helpCommand;

    public abstract boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args);

    public SubCommand(String name, String[] aliases, String description) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
    }

    public SubCommand(String name, String[] aliases, String description, String permission) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
        this.permission = permission;
    }

    public static void registerCommands() {
        registerCommand(new CmdHelp(), true);
        registerCommand(new CmdCreate());
        registerCommand(new CmdReload());
        registerCommand(new CmdDelete());
        registerCommand(new CmdJoin());
        registerCommand(new CmdLeave());
        registerCommand(new CmdInvite());
        registerCommand(new CmdOpen());
        registerCommand(new CmdClose());
        registerCommand(new CmdRank());
        registerCommand(new CmdInfo());
    }

    public static void registerCommand(SubCommand subCommand) {
        registerCommand(subCommand, false);
    }

    public static void registerCommand(SubCommand subCommand, boolean isHelpCommand) {
        commands.add(subCommand);
        if (isHelpCommand)
            registerHelpCommand(subCommand);
    }

    public static void registerHelpCommand(SubCommand subCommand) {
        helpCommand = subCommand;
    }

    public static boolean existCommand(String command) {
        for (SubCommand subCommand : commands) {
            if (subCommand.getName().equalsIgnoreCase(command)) {
                return true;
            } else {
                for (String alias : subCommand.getAliases()) {
                    if (alias.equalsIgnoreCase(command)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static SubCommand getCommand(String command) {
        for (SubCommand subCommand : commands) {
            if (subCommand.getName().equalsIgnoreCase(command)) {
                return subCommand;
            } else {
                for (String alias : subCommand.getAliases()) {
                    if (alias.equalsIgnoreCase(command)) {
                        return subCommand;
                    }
                }
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
