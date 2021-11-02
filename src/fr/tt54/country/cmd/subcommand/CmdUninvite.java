package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.manager.InviteManager;
import fr.tt54.country.objects.permissions.CountryPermission;
import fr.tt54.country.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CmdUninvite extends SubCommand {

    public CmdUninvite() {
        super("uninvite", new String[]{"uninv"}, "Remove the invite for a player to your country");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " <player>"));
            return false;
        }
        if (player.getName().equalsIgnoreCase(args[0])) {
            player.sendMessage(Main.getMessages().getMessage("executeyourself"));
            return false;
        }
        if (!CountryManager.hasCountry(player)) {
            player.sendMessage(Main.getMessages().getMessage("nocountry"));
            return false;
        }
        if (!Permission.hasCountryPermission(player, CountryPermission.INVITE_PLAYER)) {
            player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
            return false;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
            player.sendMessage(Main.getMessages().getMessage("neverconnected", "%player%", args[0]));
            return false;
        }
        if (CountryManager.getPlayerCountry(offlinePlayer) != null) {
            player.sendMessage(Main.getMessages().getMessage("playerhascountry", "%player%", args[0]));
            return false;
        }
        if (!InviteManager.isInvited(offlinePlayer, CountryManager.getPlayerCountry(player))) {
            player.sendMessage(Main.getMessages().getMessage("playernotinvited", "%player%", args[0]));
            return false;
        }
        InviteManager.uninvite(offlinePlayer, CountryManager.getPlayerCountry(player));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
