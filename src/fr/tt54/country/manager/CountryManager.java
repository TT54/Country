package fr.tt54.country.manager;

import fr.tt54.country.Main;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Rank;
import fr.tt54.country.objects.permissions.CountryPermission;
import fr.tt54.country.utils.FileManager;
import fr.tt54.country.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CountryManager {

    private static FileConfiguration countries;
    public static Map<String, Country> countriesMap = new HashMap<>(); //<UUID, country>
    public static Map<String, Country> playersCountry = new HashMap<>(); //<UUID, country>

    public static void enable() {
        reloadCountries();
    }

    public static void createCountry(Player player, String name) {
        if (hasCountry(player)) {
            player.sendMessage(Main.getMessages().getMessage("alreadyincountry", "%country%", getCountryName(player)));
            return;
        }
        if (existCountry(name)) {
            player.sendMessage(Main.getMessages().getMessage("countryalreadyexist", "%country%", name));
            return;
        }

        Country country = new Country(UUID.randomUUID(), name, player, Rank.defaultRanks);
        countriesMap.put(country.getUuid().toString(), country);
        playersCountry.put(player.getUniqueId().toString(), country);

        countries.set(country.getUuid().toString() + ".name", country.getName());
        countries.set(country.getUuid().toString() + ".leader", player.getUniqueId().toString());
        for (OfflinePlayer offlinePlayer : country.getMembers().keySet()) {
            countries.set(country.getUuid().toString() + ".members." + offlinePlayer.getUniqueId().toString(), country.getMembers().get(offlinePlayer).getName());
        }
        countries.set(country.getUuid().toString() + ".level", country.getLevel());
        countries.set(country.getUuid().toString() + ".maxclaims", country.getMaxClaims());
        countries.set(country.getUuid().toString() + ".isopened", country.isOpened());
        for (int i = 0; i < country.getChunksClaimed().size(); i++) {
            countries.set(country.getUuid().toString() + ".chunks." + i + ".x", country.getChunksClaimed().get(i).getX());
            countries.set(country.getUuid().toString() + ".chunks." + i + ".z", country.getChunksClaimed().get(i).getZ());
            countries.set(country.getUuid().toString() + ".chunks." + i + ".world", country.getChunksClaimed().get(i).getWorld());
        }

        for (Rank rank : country.getRanks()) {
            countries.set(country.getUuid().toString() + ".ranks." + rank.getName() + ".prefix", rank.getPrefix());
            countries.set(country.getUuid().toString() + ".ranks." + rank.getName() + ".power", rank.getPower());
            countries.set(country.getUuid().toString() + ".ranks." + rank.getName() + ".permissions", rank.getCountryPermissions().stream().map(CountryPermission::name).collect(Collectors.toList()));
        }
        saveCountries();
        player.sendMessage(Main.getMessages().getMessage("countrycreated", "%country%", getCountryName(player)));
    }

    private static void saveCountries() {
        FileManager.saveFile(countries, "countries");
    }

    public static boolean existCountry(String name) {
        for (Country country : countriesMap.values()) {
            if (country.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public static void changeLeader(Country country, OfflinePlayer newLeader) {
        country.setLeader(newLeader);
        if (newLeader.isOnline()) {
            newLeader.getPlayer().sendMessage(Main.getMessages().getMessage("rankupleader"));
        }

        for (OfflinePlayer offlinePlayer : CountryManager.getCountry(country.getUuid()).getMembers().keySet()) {
            if (offlinePlayer.isOnline()) {
                offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("newleader", "%player%", newLeader.getName()));
            }
        }
    }

    public static boolean existCountry(UUID uuid) {
        return countriesMap.containsKey(uuid.toString());
    }

    public static void reloadCountries() {
        countries = FileManager.getYmlFile("countries");
        countriesMap.clear();
        playersCountry.clear();
        for (String fuuid : countries.getKeys(false)) {
            ConfigurationSection section = countries.getConfigurationSection(fuuid);
            String name = section.getString("name");
            OfflinePlayer leader = null;
            try {
                leader = Bukkit.getOfflinePlayer(UUID.fromString(section.getString("leader")));
            } catch (NullPointerException e) {
                removeCountry(UUID.fromString(fuuid));
            }

            if (leader == null) {
                removeCountry(UUID.fromString(fuuid));
                continue;
            }

            ConfigurationSection ranksSection = section.getConfigurationSection("ranks");
            Map<String, Rank> ranks = new HashMap<>();
            if (ranksSection != null) {
                for (String rankName : ranksSection.getKeys(false)) {
                    List<String> perms = ranksSection.getStringList(rankName + ".permissions");
                    List<CountryPermission> countryPermissions = perms.stream().map(CountryPermission::getPermission).collect(Collectors.toList());
                    ranks.put(rankName, new Rank(rankName, ranksSection.getString(rankName + ".prefix"), ranksSection.getInt(rankName + ".power"), countryPermissions));
                }
            }


            ConfigurationSection memberSection = section.getConfigurationSection("members");
            Map<OfflinePlayer, Rank> members = new HashMap<>();
            if (memberSection != null) {
                for (String playerUUID : memberSection.getKeys(false)) {
                    members.put(Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)), ranks.get(memberSection.getString(playerUUID)));
                }
            }

            if (!members.containsKey(leader)) {
                removeCountry(UUID.fromString(fuuid));
                continue;
            }

            int level = section.getInt("level");
            int maxClaims = section.getInt("maxclaims");
            boolean opened = section.getBoolean("isopened");

            List<Chunk> chunks = new ArrayList<>();
            /*ConfigurationSection chunksSection = section.getConfigurationSection("chunks");
            if (chunksSection != null) {
                for (String i : chunksSection.getKeys(false)) {
                    World world = Bukkit.getWorld(chunksSection.getString(i + ".world"));
                    chunks.add(world.getChunkAt(chunksSection.getInt(i + ".x"), chunksSection.getInt(i + ".z")));
                }
            }*/

            Country country = new Country(UUID.fromString(fuuid), name, members, leader, level, maxClaims, chunks, new ArrayList<>(ranks.values()), opened);
            countriesMap.put(fuuid, country);
            for (OfflinePlayer offlinePlayer : country.getMembers().keySet()) {
                playersCountry.put(offlinePlayer.getUniqueId().toString(), country);
            }
        }
    }

    public static Country getPlayerCountry(OfflinePlayer offlinePlayer) {
        Country country = playersCountry.get(offlinePlayer.getUniqueId().toString());
        if (country == null)
            return null;
        return countriesMap.get(country.getUuid().toString());
    }

    public static boolean hasCountry(OfflinePlayer player) {
        return playersCountry.containsKey(player.getUniqueId().toString());
    }

    public static String getCountryName(OfflinePlayer player) {
        if (hasCountry(player))
            return getPlayerCountry(player).getName();
        return Main.getInstance().getNoCountryName();
    }

    public static void removeCountry(UUID uuid) {
        Country country = getCountry(uuid);
        List<Location> chunks = country.getChunksClaimed().stream().map(chunk -> chunk.getBlock(1, 1, 1).getLocation()).collect(Collectors.toList());
        for (Location location : chunks) {
            ClaimManager.unclaimChunk(country, location);
        }
        if (countriesMap.get(uuid.toString()) != null && countriesMap.get(uuid.toString()).getMembers() != null) {
            for (OfflinePlayer offlinePlayer : countriesMap.get(uuid.toString()).getMembers().keySet()) {
                playersCountry.remove(offlinePlayer.getUniqueId().toString());
            }
        }
        countriesMap.remove(uuid.toString());
        countries.set(uuid.toString(), null);
        saveCountries();
    }

    public static Country getCountry(UUID uuid) {
        if (!countriesMap.containsKey(uuid.toString()))
            return null;
        return countriesMap.get(uuid.toString());
    }

    public static Country getCountry(String name) {
        for (Country country : countriesMap.values()) {
            if (country.getName().equalsIgnoreCase(name))
                return country;
        }
        return null;
    }

    public static void removeCountry(Player player, UUID uuid) {
        if (getCountry(uuid) == null) {
            player.sendMessage(Main.getMessages().getMessage("countryuuidnotexist", "%countryuuid%", uuid.toString()));
            return;
        }
        String name = getCountry(uuid).getName();
        removeCountry(uuid);
        player.sendMessage(Main.getMessages().getMessage("countryremoved", "%country%", name));
    }

    public static void joinCountry(Player player, UUID factionUUID) {
        if (!hasCountry(player) && getCountry(factionUUID) != null) {
            if (!getCountry(factionUUID).isOpened() && !InviteManager.isInvited(player, factionUUID)) {
                return;
            }
            countriesMap.get(factionUUID.toString()).getMembers().put(Bukkit.getOfflinePlayer(player.getUniqueId()), Country.getMinRank(countriesMap.get(factionUUID.toString()).getRanks()));
            ConfigurationSection memberSection = countries.getConfigurationSection(factionUUID.toString() + ".members");
            Map<OfflinePlayer, Rank> members = countriesMap.get(factionUUID.toString()).getMembers();
            for (OfflinePlayer offlinePlayer : members.keySet()) {
                countries.set(factionUUID.toString() + ".members." + offlinePlayer.getUniqueId().toString(), members.get(offlinePlayer).getName());
            }
            playersCountry.put(player.getUniqueId().toString(), countriesMap.get(factionUUID.toString()));
            saveCountries();
            InviteManager.clearInvites(player);
            player.sendMessage(Main.getMessages().getMessage("countryjoined", "%country%", getCountry(factionUUID).getName()));
            for (OfflinePlayer offlinePlayer : getCountry(factionUUID).getMembers().keySet()) {
                if (offlinePlayer.isOnline()) {
                    offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("playerjoincountry", "%player%", player.getName()));
                }
            }
        }
    }

    public static void leaveCountry(Player player) {
        if (hasCountry(player)) {
            Country country = getPlayerCountry(player);
            if (player.equals(country.getLeader().getPlayer())) {
                player.sendMessage(Main.getMessages().getMessage("leadercantquit"));
                return;
            }
            countriesMap.get(country.getUuid().toString()).getMembers().remove(Bukkit.getOfflinePlayer(player.getUniqueId()));
            ConfigurationSection memberSection = countries.getConfigurationSection(country.getUuid().toString() + ".members");
            Map<OfflinePlayer, Rank> members = countriesMap.get(country.getUuid().toString()).getMembers();
            countries.set(country.getUuid().toString() + ".members", null);
            for (OfflinePlayer offlinePlayer : members.keySet()) {
                countries.set(country.getUuid().toString() + ".members." + offlinePlayer.getUniqueId().toString(), members.get(offlinePlayer).getName());
            }
            playersCountry.remove(player.getUniqueId().toString());
            saveCountries();
            player.sendMessage(Main.getMessages().getMessage("quitcountry", "%country%", country.getName()));
            for (OfflinePlayer offlinePlayer : CountryManager.getCountry(country.getUuid()).getMembers().keySet()) {
                if (offlinePlayer.isOnline()) {
                    offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("playerleave", "%player%", player.getName()));
                }
            }
        }
    }

    public static Rank getRank(OfflinePlayer player) {
        if (hasCountry(player)) {
            Country country = getPlayerCountry(player);
            return country.getMembers().get(player);
        }
        return null;
    }

    public static void openCountry(OfflinePlayer player) {
        String message = "internalerror";
        if (hasCountry(player) && Permission.hasCountryPermission(player, CountryPermission.OPEN_FACTION)) {
            Country country = getPlayerCountry(player);
            countriesMap.get(country.getUuid().toString()).setOpened(true);
            countries.set(country.getUuid().toString() + ".isopened", country.isOpened());
            saveCountries();
            message = "opencountry";
            for (OfflinePlayer offlinePlayer : country.getMembers().keySet()) {
                if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null)
                    offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("countryopened", "%player%", player.getName()));
            }
        }
        if (player.isOnline()) {
            player.getPlayer().sendMessage(Main.getMessages().getMessage(message));
        }
    }

    public static void closeCountry(OfflinePlayer player) {
        String message = "internalerror";
        if (hasCountry(player) && Permission.hasCountryPermission(player, CountryPermission.OPEN_FACTION)) {
            Country country = getPlayerCountry(player);
            countriesMap.get(country.getUuid().toString()).setOpened(false);
            countries.set(country.getUuid().toString() + ".isopened", country.isOpened());
            saveCountries();
            message = "closecountry";
            for (OfflinePlayer offlinePlayer : country.getMembers().keySet()) {
                if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null)
                    offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("countryclosed", "%player%", player.getName()));
            }
        }
        if (player.isOnline()) {
            player.getPlayer().sendMessage(Main.getMessages().getMessage(message));
        }
    }

    public static boolean createCountryRank(Country country, Rank rank) {
        if (!country.hasRank(rank.getName())) {
            country.addRank(rank);
            saveCountry(country);
            return true;
        }
        return false;
    }

    public static boolean deleteCountryRank(Country country, String name) {
        if (country.hasRank(name)) {
            country.removeRank(name);
            saveCountry(country);
            return true;
        }
        return false;
    }

    public static boolean setPlayerRank(OfflinePlayer player, Rank rank) {
        if (hasCountry(player)) {
            Country country = getPlayerCountry(player);
            Rank actualRank = getRank(player);
            if (actualRank != null && rank != null && !actualRank.equals(rank)) {
                country.setRank(player, rank);
                saveCountry(country);
            }
            return true;
        }
        return false;
    }

    public static boolean setNewRankPower(UUID countryUUID, Rank rankToEdit, int power) {
        if (existCountry(countryUUID)) {
            Country country = getCountry(countryUUID);
            if (country.hasRank(rankToEdit.getName())) {
                country.setRankPower(rankToEdit.getName(), power);
                saveCountry(country);
                return true;
            }
        }
        return false;
    }

    public static boolean setNewRankPrefix(UUID countryUUID, Rank rankToEdit, String prefix) {
        if (existCountry(countryUUID)) {
            Country country = getCountry(countryUUID);
            if (country.hasRank(rankToEdit.getName())) {
                country.setRankPrefix(rankToEdit.getName(), prefix);
                saveCountry(country);
                return true;
            }
        }
        return false;
    }

    public static void saveCountry(Country country) {
        UUID uuid = country.getUuid();
        if (existCountry(uuid)) {
            countriesMap.remove(uuid.toString());
            countriesMap.put(uuid.toString(), country);

            countries.set(uuid.toString(), null);

            countries.set(uuid.toString() + ".name", country.getName());
            countries.set(uuid.toString() + ".leader", country.getLeader().getUniqueId().toString());
            for (OfflinePlayer offlinePlayer : country.getMembers().keySet()) {
                countries.set(uuid.toString() + ".members." + offlinePlayer.getUniqueId().toString(), country.getMembers().get(offlinePlayer).getName());
            }
            countries.set(uuid.toString() + ".level", country.getLevel());
            countries.set(uuid.toString() + ".maxclaims", country.getMaxClaims());
            countries.set(uuid.toString() + ".isopened", country.isOpened());
            /*for (int i = 0; i < country.getChunksClaimed().size(); i++) {
                countries.set(uuid.toString() + ".chunks." + i + ".x", country.getChunksClaimed().get(i).getX());
                countries.set(uuid.toString() + ".chunks." + i + ".z", country.getChunksClaimed().get(i).getZ());
                countries.set(uuid.toString() + ".chunks." + i + ".world", country.getChunksClaimed().get(i).getWorld().getUID().toString());
            }*/

            for (Rank rank : country.getRanks()) {
                countries.set(uuid.toString() + ".ranks." + rank.getName() + ".prefix", rank.getPrefix());
                countries.set(uuid.toString() + ".ranks." + rank.getName() + ".power", rank.getPower());
                countries.set(uuid.toString() + ".ranks." + rank.getName() + ".permissions", rank.getCountryPermissions().stream().map(CountryPermission::name).collect(Collectors.toList()));
            }
            saveCountries();
        }
    }

    public static void kickCountry(OfflinePlayer player, Player kicker) {
        if (hasCountry(player)) {
            Country country = getPlayerCountry(player);
            countriesMap.get(country.getUuid().toString()).getMembers().remove(Bukkit.getOfflinePlayer(player.getUniqueId()));
            ConfigurationSection memberSection = countries.getConfigurationSection(country.getUuid().toString() + ".members");
            Map<OfflinePlayer, Rank> members = countriesMap.get(country.getUuid().toString()).getMembers();
            countries.set(country.getUuid().toString() + ".members", null);
            for (OfflinePlayer offlinePlayer : members.keySet()) {
                countries.set(country.getUuid().toString() + ".members." + offlinePlayer.getUniqueId().toString(), members.get(offlinePlayer).getName());
            }
            playersCountry.remove(player.getUniqueId().toString());
            saveCountries();
            if (player.isOnline()) {
                player.getPlayer().sendMessage(Main.getMessages().getMessage("kicked"));
            }
            for (OfflinePlayer offlinePlayer : CountryManager.getCountry(country.getUuid()).getMembers().keySet()) {
                if (offlinePlayer.isOnline()) {
                    offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("playerkick", "%player%", player.getName(), "%kicker%", kicker.getName()));
                }
            }
        }
    }
}
