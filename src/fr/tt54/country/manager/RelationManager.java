package fr.tt54.country.manager;

import fr.tt54.country.Main;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Relations;
import fr.tt54.country.utils.FileManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RelationManager {

    private static FileConfiguration relations;

    public static void enable() {
        reloadRelations();
    }

    private static void reloadRelations() {
        relations = FileManager.getYmlFile("relations");

        for (String cUUID : relations.getKeys(false)) {
            Country country = CountryManager.getCountry(UUID.fromString(cUUID));
            if (country != null) {
                country.clearRelations();
                country.clearRelationsRequests();

                ConfigurationSection requestSection = relations.getConfigurationSection(cUUID + ".requests");
                if (requestSection != null) {
                    for (String requestedCountry : requestSection.getKeys(false)) {
                        country.setRelationRequestWith(UUID.fromString(requestedCountry), Relations.getRelation(requestSection.getString(requestedCountry)));
                    }
                }

                ConfigurationSection relationsSection = relations.getConfigurationSection(cUUID + ".relations");
                if (relationsSection != null) {
                    for (String relationCountry : relationsSection.getKeys(false)) {
                        country.setRelationWith(UUID.fromString(relationCountry), Relations.getRelation(relationsSection.getString(relationCountry)));
                    }
                }
            }
        }
    }

    public static void sendAllyRequest(Country sender, Country receiver) {
        String messageReceiver = "";
        String messageSender = "";
        if (receiver.hasRelationRequests(sender.getUuid(), Relations.ALLY)) {
            receiver.removeRelationRequests(sender.getUuid());
            messageReceiver = "allyrequestrevoked";
            messageSender = "yourevokedallyrequest";
        } else if (sender.hasRelationRequests(receiver.getUuid(), Relations.ALLY)) {
            setRelation(sender, receiver, Relations.ALLY);
            messageReceiver = "ally";
            messageSender = "ally";
            Main.getInstance().log(sender.getName() + " is now allied with " + receiver.getName());
        } else {
            receiver.setRelationRequestWith(sender.getUuid(), Relations.ALLY);
            messageReceiver = "allyrequestreceived";
            messageSender = "allyrequestsent";
        }

        for (OfflinePlayer offlinePlayer : receiver.getMembers().keySet()) {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage(messageReceiver, "%country%", sender.getName()));
            }
        }
        for (OfflinePlayer offlinePlayer : sender.getMembers().keySet()) {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage(messageSender, "%country%", receiver.getName()));
            }
        }
    }

    public static void sendEntenteRequest(Country sender, Country receiver) {
        String messageReceiver = "";
        String messageSender = "";
        if (receiver.hasRelationRequests(sender.getUuid(), Relations.ENTENTE)) {
            receiver.removeRelationRequests(sender.getUuid());
            messageReceiver = "ententerequestrevoked";
            messageSender = "yourevokedententerequest";
        } else if (sender.hasRelationRequests(receiver.getUuid(), Relations.ENTENTE)) {
            setRelation(sender, receiver, Relations.ENTENTE);
            messageReceiver = "entente";
            messageSender = "entente";
            Main.getInstance().log(sender.getName() + " is now in entente with " + receiver.getName());
        } else {
            receiver.setRelationRequestWith(sender.getUuid(), Relations.ENTENTE);
            messageReceiver = "ententerequestreceived";
            messageSender = "ententerequestsent";
        }

        for (OfflinePlayer offlinePlayer : receiver.getMembers().keySet()) {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage(messageReceiver, "%country%", sender.getName()));
            }
        }
        for (OfflinePlayer offlinePlayer : sender.getMembers().keySet()) {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage(messageSender, "%country%", receiver.getName()));
            }
        }
    }

    public static void setEnemy(Country sender, Country receiver) {
        if (sender.getRelationWith(receiver.getUuid()) != Relations.ENEMY) {
            setRelation(sender, receiver, Relations.ENEMY);
            Main.getInstance().log(sender.getName() + " is now enemy with " + receiver.getName());

            for (OfflinePlayer offlinePlayer : receiver.getMembers().keySet()) {
                if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                    offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("enemyreceived", "%country%", sender.getName()));
                }
            }
            for (OfflinePlayer offlinePlayer : sender.getMembers().keySet()) {
                if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                    offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("enemysent", "%country%", receiver.getName()));
                }
            }
        }
    }

    public static void declareWar(Country sender, Country victim) {
        if (sender.getRelationWith(victim.getUuid()) == Relations.ENEMY) {
            setInWar(victim, sender, Main.getMessages().getMessage("inwar", "%country%", sender.getName()));
            for (UUID ententeUUID : victim.getRelations(Relations.ENTENTE)) {
                Country entente = CountryManager.getCountry(ententeUUID);
                setInWar(entente, sender, Main.getMessages().getMessage("warentente", "%country%", sender.getName(), "%entente%", victim.getName()));
            }
            for (UUID allyUUID : victim.getRelations(Relations.ALLY)) {
                Country ally = CountryManager.getCountry(allyUUID);
                setInWar(ally, sender, Main.getMessages().getMessage("warally", "%country%", sender.getName(), "%ally%", victim.getName()));
            }
            for (UUID allyUUID : sender.getRelations(Relations.ALLY)) {
                Country ally = CountryManager.getCountry(allyUUID);
                setInWar(ally, victim, Main.getMessages().getMessage("warally", "%country%", sender.getName(), "%ally%", victim.getName()));
            }
        }
    }

    public static void setInWar(Country starter, Country country, String reason) {
        setRelation(starter, country, Relations.IN_WAR);
        WarManager.startWar(starter, country);
        Main.getInstance().log(starter.getName() + " is now in war with " + country.getName());
        for (OfflinePlayer offlinePlayer : starter.getMembers().keySet()) {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(reason);
            }
        }
        for (OfflinePlayer offlinePlayer : country.getMembers().keySet()) {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage("inwar", "%country%", starter.getName()));
            }
        }
    }

    public static void setNeutral(Player player, Country sender, Country receiver) {
        String messageReceiver = "";
        String messageSender = "";

        if (sender.getRelationWith(receiver.getUuid()) == Relations.IN_WAR) {
            player.sendMessage(Main.getMessages().getMessage("useforfeit"));
        } else if (sender.getRelationWith(receiver.getUuid()) == Relations.ENEMY) {
            if (sender.getRelationRequestsWith(receiver.getUuid()) == Relations.NEUTRAL) {
                setRelation(sender, receiver, Relations.NEUTRAL);
                messageReceiver = "neutral";
                messageSender = "neutral";
                Main.getInstance().log(sender.getName() + " is now neutral with " + receiver.getName());
            } else {
                receiver.setRelationRequestWith(sender.getUuid(), Relations.NEUTRAL);
                messageReceiver = "neutralrequestreceived";
                messageSender = "neutralrequestsent";
            }
        } else {
            setRelation(sender, receiver, Relations.NEUTRAL);
            messageReceiver = "neutral";
            messageSender = "neutral";
            Main.getInstance().log(sender.getName() + " is now neutral with " + receiver.getName());
        }

        for (OfflinePlayer offlinePlayer : receiver.getMembers().keySet()) {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage(messageReceiver, "%country%", sender.getName()));
            }
        }
        for (OfflinePlayer offlinePlayer : sender.getMembers().keySet()) {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(Main.getMessages().getMessage(messageSender, "%country%", receiver.getName()));
            }
        }
    }

    public static void disbandRelations(Country country) {
        for (UUID uuid : country.getRelations().keySet()) {
            Country target = CountryManager.getCountry(uuid);
            target.removeRelation(country.getUuid());
            saveRelations(target);
        }
        for (UUID uuid : country.getRelationsRequests().keySet()) {
            Country target = CountryManager.getCountry(uuid);
            target.removeRelationRequests(country.getUuid());
            saveRelations(target);
        }
        country.clearRelationsRequests();
        country.clearRelations();
        WarManager.onDisband(country);
        saveRelations(country);
    }

    public static void setRelation(Country country1, Country country2, Relations relation) {
        if (relation == Relations.NEUTRAL) {
            country1.removeRelation(country2.getUuid());
            country2.removeRelation(country1.getUuid());
        } else {
            country1.setRelationWith(country2.getUuid(), relation);
            country2.setRelationWith(country1.getUuid(), relation);
        }
        country1.removeRelationRequests(country2.getUuid());
        country2.removeRelationRequests(country1.getUuid());
        saveRelations(country1);
        saveRelations(country2);
    }

    public static void saveRelations(Country country) {
        relations.set(country.getUuid().toString(), null);
        for (UUID uuid : country.getRelations().keySet()) {
            relations.set(country.getUuid().toString() + ".relations." + uuid.toString(), country.getRelationWith(uuid).name());
        }
        for (UUID uuid : country.getRelationsRequests().keySet()) {
            relations.set(country.getUuid().toString() + ".requests." + uuid.toString(), country.getRelationRequestsWith(uuid).name());
        }
        saveRelations();
    }

    private static void saveRelations() {
        FileManager.saveFile(relations, "relations");
    }

}
