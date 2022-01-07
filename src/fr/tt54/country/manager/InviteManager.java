package fr.tt54.country.manager;

import fr.tt54.country.Main;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Invite;
import fr.tt54.country.objects.permissions.CountryPermission;
import fr.tt54.country.utils.FileManager;
import fr.tt54.country.utils.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InviteManager {

    private static FileConfiguration invitesFile;
    private static final List<Invite> invites = new ArrayList<>();

    public static void enable() {
        reloadInvites();
    }

    public static void reloadInvites() {
        invitesFile = FileManager.getYmlFile("invites");
        invites.clear();

        for (String playerUUID : invitesFile.getKeys(false)) {
            ConfigurationSection section = invitesFile.getConfigurationSection(playerUUID);
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    if (CountryManager.getCountry(UUID.fromString(section.getString(key + ".factionuuid"))) != null) {
                        if (section.getString(key + ".senderuuid") != null) {
                            Invite invite = new Invite(UUID.fromString(playerUUID), UUID.fromString(section.getString(key + ".senderuuid")), CountryManager.getCountry(UUID.fromString(section.getString(key + ".factionuuid"))));
                            invites.add(invite);
                        }
                    }
                }
            }
        }
        saveInvites();
    }

    public static void saveInvites() {
        FileManager.saveFile(invitesFile, "invites");
    }

    public static boolean isInvited(OfflinePlayer player, Country country) {
        for (Invite invite : invites) {
            if (invite.getPlayerUUID().toString().equals(player.getUniqueId().toString()) && invite.getCountry().getUuid().toString().equals(country.getUuid().toString()))
                return true;
        }
        return false;
    }

    public static boolean isInvited(OfflinePlayer player, UUID faction) {
        for (Invite invite : invites) {
            if (invite.getPlayerUUID().toString().equals(player.getUniqueId().toString()) && faction.toString().equals(invite.getCountry().getUuid().toString()))
                return true;
        }
        return false;
    }

    public static void invite(OfflinePlayer target, Player sender) {
        if (CountryManager.hasCountry(sender) && !CountryManager.hasCountry(target) && Permission.hasCountryPermission(sender, CountryPermission.INVITE_PLAYER)) {
            invites.add(new Invite(target.getUniqueId(), sender.getUniqueId(), CountryManager.getPlayerCountry(sender)));
            String uuid = UUID.randomUUID().toString();
            invitesFile.set(target.getUniqueId().toString() + "." + uuid + ".factionuuid", CountryManager.getPlayerCountry(sender).getUuid().toString());
            invitesFile.set(target.getUniqueId().toString() + "." + uuid + ".senderuuid", sender.getUniqueId().toString());
            saveInvites();
            sender.sendMessage(Main.getMessages().getMessage("youinviteplayer", "%player%", target.getName()));
            if (target.isOnline()) {
                target.getPlayer().sendMessage(Main.getMessages().getMessage("invited", "%sender%", sender.getName(), "%country%", CountryManager.getPlayerCountry(sender).getName()));
            }
            for (OfflinePlayer offlinePlayer : CountryManager.getPlayerCountry(sender).getMembers().keySet()) {
                if (offlinePlayer.isOnline()) {
                    offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("playerinvited", "%player%", target.getName(), "%inviter%", sender.getName()));
                }
            }
            Main.getInstance().log(target.getName() + " was invited to join " + sender + "'s country");
        }
    }

    public static void clearInvites(OfflinePlayer player) {
        invitesFile.set(player.getUniqueId().toString(), null);
        saveInvites();
        List<Invite> remove = new ArrayList<>();
        for (Invite invite : invites) {
            if (invite.getPlayerUUID().toString().equals(player.getUniqueId().toString())) {
                remove.add(invite);
            }
        }
        for (Invite invite : remove) {
            invites.remove(invite);
        }
    }

    public static void uninvite(OfflinePlayer player, Country country) {
        for (int i = 0; i < invites.size(); i++) {
            if (invites.get(i).getCountry() == country && invites.get(i).getPlayerUUID().equals(player.getUniqueId())) {
                invites.remove(i);
                break;
            }
        }

        invitesFile.set(player.getUniqueId().toString(), null);
        for (Invite invite : invites) {
            String uuid = UUID.randomUUID().toString();
            invitesFile.set(invite.getPlayerUUID().toString() + "." + uuid + ".factionuuid", invite.getCountry().getUuid().toString());
            invitesFile.set(invite.getPlayerUUID().toString() + "." + uuid + ".senderuuid", invite.getSenderUUID().toString());
        }
        saveInvites();

        if (player.isOnline()) {
            player.getPlayer().sendMessage(Main.getMessages().getMessage("uninvited", "%country%", country.getName()));
        }
        for (OfflinePlayer offlinePlayer : country.getMembers().keySet()) {
            if (offlinePlayer.isOnline()) {
                offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("playeruninvited", "%player%", player.getName()));
            }
        }
        Main.getInstance().log(player.getName() + " was uninvited to join " + country.getName());
    }

}
