package fr.tt54.country.manager;

import fr.tt54.country.Main;
import fr.tt54.country.objects.War;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Relations;
import fr.tt54.country.utils.FileManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

public class WarManager {

    private static FileConfiguration warsFile;
    private static final Map<UUID, War> wars = new HashMap<>();
    private static final Map<UUID, List<War>> warsPerCountry = new HashMap<>();
    private static final Map<UUID, Integer> countriesTotalWars = new HashMap<>();
    private static final Map<UUID, Integer> countriesWonWars = new HashMap<>();

    public static void enable() {
        reloadWars();
    }

    private static void reloadWars() {
        warsFile = FileManager.getYmlFile("wars");
        wars.clear();
        warsPerCountry.clear();
        countriesTotalWars.clear();
        countriesWonWars.clear();

        ConfigurationSection section = warsFile.getConfigurationSection("wars");
        if (section != null) {
            for (String warUUID : section.getKeys(false)) {
                ConfigurationSection warSection = section.getConfigurationSection(warUUID);
                UUID country1 = UUID.fromString(warSection.getString("country1"));
                UUID country2 = UUID.fromString(warSection.getString("country2"));
                UUID winner = (warSection.getString("winner") == null) ? null : UUID.fromString(warSection.getString("winner"));

                int country1Kills = warSection.getInt("country1_kills");
                int country2Kills = warSection.getInt("country2_kills");

                long timeBegin = warSection.getLong("time_begin");
                boolean isFinished = warSection.getBoolean("is_finished");

                War war = new War(UUID.fromString(warUUID), CountryManager.getCountry(country1), CountryManager.getCountry(country2), (winner == null) ? null : CountryManager.getCountry(winner), country1Kills, country2Kills, timeBegin, isFinished);
                wars.put(UUID.fromString(warUUID), war);
                List<War> country1Wars = getWars(country1);
                country1Wars.add(war);
                warsPerCountry.put(country1, country1Wars);

                List<War> country2Wars = warsPerCountry.getOrDefault(country2, new ArrayList<>());
                country2Wars.add(war);
                warsPerCountry.put(country2, country2Wars);

                checkWin(war);
            }
        }

        section = warsFile.getConfigurationSection("total_wars");
        if (section != null) {
            for (String country : section.getKeys(false)) {
                countriesTotalWars.put(UUID.fromString(country), section.getInt(country));
            }
        }

        section = warsFile.getConfigurationSection("won_wars");
        if (section != null) {
            for (String country : section.getKeys(false)) {
                countriesWonWars.put(UUID.fromString(country), section.getInt(country));
            }
        }
    }

    public static void startWar(Country country1, Country country2) {
        War war = new War(country1, country2);
        wars.put(war.getUuid(), war);

        List<War> country1Wars = warsPerCountry.getOrDefault(country1.getUuid(), new ArrayList<>());
        country1Wars.add(war);
        warsPerCountry.put(country1.getUuid(), country1Wars);

        List<War> country2Wars = warsPerCountry.getOrDefault(country2.getUuid(), new ArrayList<>());
        country2Wars.add(war);
        warsPerCountry.put(country2.getUuid(), country2Wars);

        countriesTotalWars.put(country1.getUuid(), getTotalWars(country1) + 1);
        countriesTotalWars.put(country2.getUuid(), getTotalWars(country2) + 1);

        saveWar(war);
    }

    public static List<War> getWarsBetween(Country country1, Country country2) {
        return getWars(country1.getUuid()).stream().filter(war -> war.getCountry2() == country2 || war.getCountry1() == country2).collect(Collectors.toList());
    }

    public static War getActualWarBetween(Country country1, Country country2) {
        List<War> warsBetweenCountries = getWarsBetween(country1, country2);
        for (War war : warsBetweenCountries) {
            if (!war.isFinished())
                return war;
        }
        return null;
    }

    public static boolean areInWar(Country country1, Country country2) {
        return getActualWarBetween(country1, country2) != null;
    }

    public static void addKillForWar(Country killer, Country killed) {
        if (areInWar(killer, killed)) {
            War war = getActualWarBetween(killer, killed);
            if (war.getCountry1() == killer) {
                war.setCountry1Kills(war.getCountry1Kills() + 1);
            } else {
                war.setCountry2Kills(war.getCountry2Kills() + 1);
            }
            checkWin(war);
        }
    }

    public static List<War> getWars(UUID country) {
        return warsPerCountry.getOrDefault(country, new ArrayList<>());
    }

    public static void checkWin(War war) {
        if (war.getCountry1Kills() >= 20) {
            onWarWin(war.getCountry1(), war);
            return;
        } else if (war.getCountry2Kills() >= 20) {
            onWarWin(war.getCountry2(), war);
            return;
        } else if (war.getTimeBegin() + (1000 * 60 * 60 * 24 * 14) < System.currentTimeMillis()) {
            if (war.getCountry1Kills() != war.getCountry2Kills()) {
                onWarWin((war.getCountry1Kills() > war.getCountry2Kills()) ? war.getCountry1() : war.getCountry2(), war);
                return;
            }
        }
        saveWar(war);
    }

    public static void onWarWin(Country country, War war) {
        war.setWinner(country);
        war.setFinished(true);

        if (war.getCountry1() == country) {
            war.getCountry1().addPoints(war.getCountry1Kills() * 4 + 50);
            war.getCountry2().addPoints(war.getCountry2Kills());
            Main.getInstance().log(country.getName() + " won the war against " + war.getCountry2().getName());
        } else {
            war.getCountry2().addPoints(war.getCountry2Kills() * 4 + 50);
            war.getCountry1().addPoints(war.getCountry1Kills());
            Main.getInstance().log(country.getName() + " won the war against " + war.getCountry1().getName());
        }

        for (OfflinePlayer offlinePlayer : war.getCountry1().getMembers().keySet()) {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("warwon", "%winner%", country.getName(), "%country1%", war.getCountry1().getName(), "%country2%", war.getCountry2().getName()));
            }
        }
        for (OfflinePlayer offlinePlayer : war.getCountry2().getMembers().keySet()) {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("warwon", "%winner%", country.getName(), "%country1%", war.getCountry1().getName(), "%country2%", war.getCountry2().getName()));
            }
        }

        RelationManager.setRelation(war.getCountry1(), war.getCountry2(), Relations.ENEMY);
        saveWar(war);
    }

    public static void onDisband(Country country) {
        List<War> toRemove = new ArrayList<>(getWars(country.getUuid()));
        for (War war : toRemove) {
            deleteWar(war, country);
        }
        warsPerCountry.remove(country.getUuid());
        countriesTotalWars.remove(country.getUuid());
        countriesWonWars.remove(country.getUuid());
    }

    public static void deleteWar(War war, Country deleter) {
        warsPerCountry.get(war.getCountry1().getUuid()).remove(war);
        warsPerCountry.get(war.getCountry2().getUuid()).remove(war);
        List<UUID> toRemove = new ArrayList<>();
        for (UUID uuid : wars.keySet()) {
            if (wars.get(uuid) == war) {
                toRemove.add(uuid);
            }
        }
        for (UUID uuid : toRemove) {
            wars.remove(uuid);
        }

        if (war.getCountry1() != deleter) {
            countriesWonWars.put(war.getCountry1().getUuid(), getWonWars(war.getCountry1()) + 1);
            warsFile.set("won_wars." + war.getCountry1().getUuid().toString(), getWonWars(war.getCountry1()));
        } else {
            countriesWonWars.put(war.getCountry2().getUuid(), getWonWars(war.getCountry2()) + 1);
            warsFile.set("won_wars." + war.getCountry2().getUuid().toString(), getWonWars(war.getCountry2()));
        }

        warsFile.set("wars." + war.getUuid().toString(), null);
        warsFile.set("total_wars." + deleter.getUuid().toString(), null);
        warsFile.set("won_wars." + deleter.getUuid().toString(), null);

        saveWars();
    }

    public static int getTotalWars(Country country) {
        return countriesTotalWars.getOrDefault(country.getUuid(), 0);
    }

    public static int getWonWars(Country country) {
        return countriesWonWars.getOrDefault(country.getUuid(), 0);
    }

    public static void saveWar(War war) {
        warsFile.set("wars." + war.getUuid().toString(), null);
        warsFile.set("wars." + war.getUuid().toString() + ".country1", war.getCountry1().getUuid().toString());
        warsFile.set("wars." + war.getUuid().toString() + ".country2", war.getCountry2().getUuid().toString());
        warsFile.set("wars." + war.getUuid().toString() + ".winner", (war.getWinner() == null) ? null : war.getWinner().getUuid().toString());
        warsFile.set("wars." + war.getUuid().toString() + ".country1_kills", war.getCountry1Kills());
        warsFile.set("wars." + war.getUuid().toString() + ".country2_kills", war.getCountry2Kills());
        warsFile.set("wars." + war.getUuid().toString() + ".time_begin", war.getTimeBegin());
        warsFile.set("wars." + war.getUuid().toString() + ".is_finished", war.isFinished());

        warsFile.set("total_wars." + war.getCountry1().getUuid().toString(), getTotalWars(war.getCountry1()));
        warsFile.set("total_wars." + war.getCountry2().getUuid().toString(), getTotalWars(war.getCountry2()));

        warsFile.set("won_wars." + war.getCountry1().getUuid().toString(), getWonWars(war.getCountry1()));
        warsFile.set("won_wars." + war.getCountry2().getUuid().toString(), getWonWars(war.getCountry2()));

        saveWars();
    }

    private static void saveWars() {
        FileManager.saveFile(warsFile, "wars");
    }

}
