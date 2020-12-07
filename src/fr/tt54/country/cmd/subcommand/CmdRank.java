package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Rank;
import fr.tt54.country.objects.permissions.CountryPermission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CmdRank extends SubCommand {
    private int maxChars;

    public CmdRank() {
        super("rank", new String[0], "Manage your country's ranks");
        this.maxChars = Main.getInstance().getConfig().getInt("maxchars");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " list|create|delete|set|edit"));
            return false;
        }

        switch (args[0]) {
            case "list":
                if (args.length != 1) {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " list"));
                    return false;
                }
                if (!CountryManager.hasCountry(player)) {
                    player.sendMessage(Main.getMessages().getMessage("nocountry"));
                    return false;
                }
                String[] ranks = CountryManager.getPlayerCountry(player).getRanks().stream().map(Rank::getName).toArray(String[]::new);
                player.sendMessage(Main.getMessages().getMessage("ranklist", "%ranks%", Arrays.toString(ranks)));
                return true;

            case "create": //create <nom> <power> [prefix]
                if (args.length == 3) {
                    if (!CountryManager.hasCountry(player)) {
                        player.sendMessage(Main.getMessages().getMessage("nocountry"));
                        return false;
                    }
                    Rank rank = CountryManager.getRank(player);
                    Country country = CountryManager.getPlayerCountry(player);
                    if (!rank.hasPermission(CountryPermission.CREATE_RANK)) {
                        player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
                        return false;
                    }

                    String name = args[1];
                    if (name.length() > maxChars) {
                        player.sendMessage(Main.getMessages().getMessage("tomanychars", "%chars%", name.length() + "", "%maxchars%", maxChars + ""));
                        return false;
                    }
                    int power = 0;
                    try {
                        power = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " create <name> <power> [prefix]"));
                        return false;
                    }

                    if (rank.getPower() <= power) {
                        player.sendMessage(Main.getMessages().getMessage("cantcreatewithmorepower"));
                        return false;
                    }
                    if (country.hasRank(name)) {
                        player.sendMessage(Main.getMessages().getMessage("rankalreadyexist", "%rank%", name));
                        return false;
                    }
                    if (CountryManager.createCountryRank(country, new Rank(name, power))) {
                        player.sendMessage(Main.getMessages().getMessage("rankcreated", "%rank%", name));
                        return true;
                    }
                } else if (args.length == 4) {
                    if (!CountryManager.hasCountry(player)) {
                        player.sendMessage(Main.getMessages().getMessage("nocountry"));
                        return false;
                    }
                    Rank rank = CountryManager.getRank(player);
                    Country country = CountryManager.getPlayerCountry(player);
                    if (!rank.hasPermission(CountryPermission.CREATE_RANK)) {
                        player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
                        return false;
                    }

                    String name = args[1];
                    if (name.length() > maxChars) {
                        player.sendMessage(Main.getMessages().getMessage("tomanychars", "%chars%", name.length() + "", "%maxchars%", maxChars + ""));
                        return false;
                    }
                    int power = 0;
                    try {
                        power = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " create <name> <power> [prefix]"));
                        return false;
                    }
                    String prefix = args[3];

                    if (rank.getPower() <= power) {
                        player.sendMessage(Main.getMessages().getMessage("cantcreatewithmorepower"));
                        return false;
                    }
                    if (country.hasRank(name)) {
                        player.sendMessage(Main.getMessages().getMessage("rankalreadyexist", "%rank%", name));
                        return false;
                    }

                    if (prefix.length() > maxChars) {
                        player.sendMessage(Main.getMessages().getMessage("tomanychars", "%chars%", prefix.length() + "", "%maxchars%", maxChars + ""));
                        return false;
                    }

                    if (CountryManager.createCountryRank(country, new Rank(name, prefix, power))) {
                        player.sendMessage(Main.getMessages().getMessage("rankcreated", "%rank%", name));
                        return true;
                    }
                } else {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " create <name> <power> [prefix]"));
                }
                return false;

            case "delete":
                if (args.length == 2) {
                    if (!CountryManager.hasCountry(player)) {
                        player.sendMessage(Main.getMessages().getMessage("nocountry"));
                        return false;
                    }
                    Rank rank = CountryManager.getRank(player);
                    Country country = CountryManager.getPlayerCountry(player);
                    if (!rank.hasPermission(CountryPermission.DELETE_RANK)) {
                        player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
                        return false;
                    }

                    String name = args[1];

                    if (!country.hasRank(name)) {
                        player.sendMessage(Main.getMessages().getMessage("rankdoesntexist", "%rank%", name));
                        return false;
                    }

                    Rank rankToRemove = country.getRank(name);

                    if (rank.getPower() <= rankToRemove.getPower()) {
                        player.sendMessage(Main.getMessages().getMessage("cantdeletewithmorepower"));
                        return false;
                    }

                    if (rankToRemove.getName().equalsIgnoreCase("member")) {
                        player.sendMessage(Main.getMessages().getMessage("cantdeletemember"));
                        return false;
                    }

                    if (CountryManager.deleteCountryRank(country, name)) {
                        player.sendMessage(Main.getMessages().getMessage("rankdeleted", "%rank%", name));
                        return true;
                    }
                } else {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " delete <name>"));
                }
                return false;

            case "set":
                // /country rank set <Player> <Rank>
                if (args.length == 3) {
                    if (!CountryManager.hasCountry(player)) {
                        player.sendMessage(Main.getMessages().getMessage("nocountry"));
                        return false;
                    }
                    Rank rank = CountryManager.getRank(player);
                    Country country = CountryManager.getPlayerCountry(player);
                    if (!rank.hasPermission(CountryPermission.SET_RANK)) {
                        player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
                        return false;
                    }

                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    if (!CountryManager.hasCountry(target)) {
                        player.sendMessage(Main.getMessages().getMessage("playernotinyourcountry"));
                        return false;
                    }
                    if (!CountryManager.getPlayerCountry(player).getUuid().equals(CountryManager.getPlayerCountry(target).getUuid())) {
                        player.sendMessage(Main.getMessages().getMessage("playernotinyourcountry"));
                        return false;
                    }

                    String name = args[2];

                    if (!country.hasRank(name)) {
                        player.sendMessage(Main.getMessages().getMessage("rankdoesntexist", "%rank%", name));
                        return false;
                    }

                    Rank rankToSet = country.getRank(name);

                    if (rank.getPower() <= rankToSet.getPower()) {
                        player.sendMessage(Main.getMessages().getMessage("cantsetrankwithmorepower"));
                        return false;
                    }

                    if (CountryManager.setPlayerRank(target, rankToSet)) {
                        player.sendMessage(Main.getMessages().getMessage("rankset", "%rank%", name, "%player%", target.getName()));
                        return true;
                    }
                } else {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " set <player> <rank>"));
                }
                return false;

            case "edit":
                if (args.length == 4) {
                    return editRank(player, command, args);
                } else {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " edit <rank> <power|prefix> <new setting>"));
                    return false;
                }

            case "info":
                if (args.length == 2) {
                    if (!CountryManager.hasCountry(player)) {
                        player.sendMessage(Main.getMessages().getMessage("nocountry"));
                        return false;
                    }
                    Country country = CountryManager.getPlayerCountry(player);
                    if (!country.hasRank(args[1])) {
                        player.sendMessage(Main.getMessages().getMessage("rankdoesntexist", "%rank%", args[1]));
                        return false;
                    }
                    Rank rank = country.getRank(args[1]);
                    player.sendMessage("§2[" + rank.getName() + "]");
                    player.sendMessage("§2Power : §a" + rank.getPower());
                    player.sendMessage("§2Prefix : §a" + rank.getPrefix().replace("&", "§"));
                    return true;
                } else {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " info <rank>"));
                    return false;
                }

            default:
                sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " list|create|delete|add|remove|edit"));
                return false;
        }
    }

    private boolean editRank(Player player, String command, String[] args) {
        if (!CountryManager.hasCountry(player)) {
            player.sendMessage(Main.getMessages().getMessage("nocountry"));
            return false;
        }
        Rank rank = CountryManager.getRank(player);
        Country country = CountryManager.getPlayerCountry(player);
        if (!rank.hasPermission(CountryPermission.EDIT_RANK)) {
            player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
            return false;
        }

        String rankName = args[1];
        String param = args[2];
        String newSetting = args[3];

        if (!country.hasRank(rankName)) {
            player.sendMessage(Main.getMessages().getMessage("rankdoesntexist", "%rank%", rankName));
            return false;
        }

        Rank rankToEdit = country.getRank(rankName);

        if (rank.getPower() <= rankToEdit.getPower()) {
            player.sendMessage(Main.getMessages().getMessage("canteditrankwithmorepower"));
            return false;
        }

        if (param.equalsIgnoreCase("power")) {
            try {
                int power = Integer.parseInt(newSetting);

                if (CountryManager.setNewRankPower(country.getUuid(), rankToEdit, power)) {
                    player.sendMessage(Main.getMessages().getMessage("rankpowerset", "%rank%", rankName, "%power%", power + ""));
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " edit <rank> <power> <new power>"));
                return false;
            }
        } else if (param.equalsIgnoreCase("prefix")) {
            if (newSetting.length() > maxChars) {
                player.sendMessage(Main.getMessages().getMessage("tomanychars", "%chars%", newSetting.length() + "", "%maxchars%", maxChars + ""));
                return false;
            }

            if (CountryManager.setNewRankPrefix(country.getUuid(), rankToEdit, newSetting)) {
                player.sendMessage(Main.getMessages().getMessage("rankprefixset", "%rank%", rankName, "%prefix%", newSetting + ""));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (CountryManager.hasCountry(player)) {
                if (args.length == 1) {
                    return Arrays.asList("list", "create", "delete", "set", "edit", "info").stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
                } else if (args.length == 2) {
                    switch (args[0]) {
                        case "delete":

                        case "edit":
                            return CountryManager.getPlayerCountry(player).getRanks().stream().filter(rank -> rank.getPower() < CountryManager.getRank(player).getPower()).map(Rank::getName).filter(name -> name.startsWith(args[1])).collect(Collectors.toList());

                        case "set":
                            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.startsWith(args[1])).collect(Collectors.toList());

                        case "info":
                            return CountryManager.getPlayerCountry(player).getRanks().stream().map(Rank::getName).filter(name -> name.startsWith(args[1])).collect(Collectors.toList());
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("set")) {
                        return CountryManager.getPlayerCountry(player).getRanks().stream().filter(rank -> rank.getPower() < CountryManager.getRank(player).getPower()).map(Rank::getName).filter(name -> name.startsWith(args[2])).collect(Collectors.toList());
                    } else if (args[0].equalsIgnoreCase("edit")) {
                        return Arrays.asList("prefix", "power").stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
                    }
                }
            }
        }
        return null;
    }
}
