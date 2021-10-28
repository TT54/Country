package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.objects.country.Country;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CmdList extends SubCommand {

    public CmdList() {
        super("list", new String[]{}, "Get the list of all countries");
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
        sender.sendMessage("§2---- List [" + (page + 1) + "/" + (CountryManager.countriesMap.size() / Main.MAX_COMMANDS_IN_HELP + 1) + "]----");
        for (String str : getListMessage(page)) {
            sender.sendMessage(str);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        return null;
    }

    public static List<String> getListMessage(int page) {
        List<String> message = new ArrayList<>();
        List<Country> countries = new ArrayList<>(CountryManager.countriesMap.values());
        countries = countries.stream().sorted((Comparator.comparingInt(o -> (int) -o.getMembers().keySet().stream().filter(OfflinePlayer::isOnline).count()))).collect(Collectors.toList());
        for (int i = page * Main.MAX_COMMANDS_IN_HELP; i < Math.min((page + 1) * Main.MAX_COMMANDS_IN_HELP, countries.size()); i++) {
            Country country = countries.get(i);
            message.add("§2" + country.getName() + " : §a" + (int) country.getMembers().keySet().stream().filter(OfflinePlayer::isOnline).count() + "/" + country.getMembers().size());
        }
        int max = message.size() - (page * Main.MAX_COMMANDS_IN_HELP);
        return message.subList(page * Main.MAX_COMMANDS_IN_HELP, (page * Main.MAX_COMMANDS_IN_HELP) + Math.min(max, Main.MAX_COMMANDS_IN_HELP));
    }

}
