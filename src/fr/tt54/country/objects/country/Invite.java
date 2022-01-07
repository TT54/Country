package fr.tt54.country.objects.country;

import fr.tt54.country.manager.CountryManager;
import org.bukkit.Bukkit;

import java.util.UUID;

public class Invite {

    private final UUID playerUUID;
    private final UUID senderUUID;
    private final Country country;

    public Invite(UUID playerUUID, UUID senderUUID, Country country) {
        this.playerUUID = playerUUID;
        this.senderUUID = senderUUID;
        this.country = country;
    }

    public Invite(UUID playerUUID, UUID senderUUID) {
        this.playerUUID = playerUUID;
        this.senderUUID = senderUUID;
        this.country = CountryManager.getPlayerCountry(Bukkit.getOfflinePlayer(senderUUID));
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public UUID getSenderUUID() {
        return senderUUID;
    }

    public Country getCountry() {
        return country;
    }
}
