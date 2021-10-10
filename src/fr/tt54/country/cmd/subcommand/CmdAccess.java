package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.ClaimManager;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.objects.country.Claim;
import fr.tt54.country.objects.permissions.ClaimPermission;
import fr.tt54.country.objects.permissions.CountryPermission;
import fr.tt54.country.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CmdAccess extends SubCommand {
    public CmdAccess() {
        super("access", new String[]{"accesschunk"}, "Allow to give permission to players in different claims");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }
        Player player = (Player) sender;

        if (!CountryManager.hasCountry(player)) {
            player.sendMessage(Main.getMessages().getMessage("nocountry"));
            return false;
        }
        if (!Permission.hasCountryPermission(player, CountryPermission.ACCESS_CLAIM)) {
            player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " <view|player|list>"));
            return false;
        }

        Claim claim = ClaimManager.getClaim(player.getLocation().getChunk());

        if (args[0].equalsIgnoreCase("list")) {
            player.sendMessage("§2---- The different claims permissions are : ----");
            for (ClaimPermission permission : ClaimPermission.values()) {
                player.sendMessage("§2 - " + permission.name() + " : §a" + permission.getDescription());
            }
            return true;
        } else {
            if (!ClaimManager.isInClaimedChunk(player.getLocation())) {
                player.sendMessage(Main.getMessages().getMessage("mustbeinclaim"));
                return false;
            }
            if (args[0].equalsIgnoreCase("view")) {
                int page = 1;
                if (args.length == 2) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignore) {
                    }
                } else if (args.length > 2) {
                    player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " view <page>"));
                    return false;
                }
                List<OfflinePlayer> players = claim.getPlayersPermissions().keySet().stream().map(Bukkit::getOfflinePlayer).collect(Collectors.toList());
                if (players.isEmpty()) {
                    player.sendMessage(Main.getMessages().getMessage("noaccessedplayers"));
                    return true;
                }
                List<OfflinePlayer> finalList = players.subList((page - 1) * 8, Math.min(page * 8, players.size()));
                player.sendMessage("§2---- The accessed players in this chunk are : ----");
                for (OfflinePlayer p : finalList) {
                    player.sendMessage("§2 - " + p.getName() + " : §a" + Arrays.toString(claim.getPlayersPermissions().get(p.getUniqueId()).toArray()));
                }
            } else if (args[0].equalsIgnoreCase("p") || args[0].equalsIgnoreCase("player")) {
                if (args.length != 3) {
                    player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " p <player> <permission>"));
                    return false;
                }

                if (!Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore() && Bukkit.getPlayer(args[1]) == null) {
                    player.sendMessage(Main.getMessages().getMessage("neverconnected", "%player%", args[1]));
                    return false;
                }

                if (!Arrays.asList(Arrays.stream(ClaimPermission.values()).map(ClaimPermission::name).toArray()).contains(args[2].toUpperCase())) {
                    player.sendMessage(Main.getMessages().getMessage("countrypermissionnotexist", "%player%", args[1]));
                    return false;
                }

                OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
                ClaimPermission permission = ClaimPermission.getPermission(args[2].toUpperCase());

                if (claim.hasPermission(p, permission)) {
                    claim.removePlayerPermission(p, permission);
                    player.sendMessage(Main.getMessages().getMessage("unaccessed", "%player%", args[1], "%permission%", args[2].toLowerCase()));
                } else {
                    claim.addPlayerPermission(p, permission);
                    player.sendMessage(Main.getMessages().getMessage("accessed", "%player%", args[1], "%permission%", args[2].toLowerCase()));
                }
            }
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (CountryManager.hasCountry(player)) {
                if (args.length == 1)
                    return Stream.of("list", "player", "view").filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
                else {
                    if (!Permission.hasCountryPermission(player, CountryPermission.ACCESS_CLAIM))
                        return null;
                    if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("view"))
                            return Stream.of("1", "2").filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                        else if (args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("p")) {
                            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                        }
                    } else if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("p")) {
                            return Arrays.stream(ClaimPermission.values()).map(ClaimPermission::name).filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
                        }
                    }
                }
            }
        }

        return null;
    }
}
