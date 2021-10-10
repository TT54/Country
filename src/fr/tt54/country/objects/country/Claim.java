package fr.tt54.country.objects.country;

import fr.tt54.country.manager.ClaimManager;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.objects.permissions.ClaimPermission;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class Claim {

    private final Chunk chunk;
    private Country owner;
    private Map<UUID, List<ClaimPermission>> playersPermissions = new HashMap<>(); //<PlayerUUID, PermissionsList>

    public Claim(Chunk chunk, Country owner) {
        this.chunk = chunk;
        this.owner = owner;
    }

    public Claim(Chunk chunk, String ownerName) {
        this.chunk = chunk;
        this.owner = CountryManager.getCountry(ownerName);
    }

    public Claim(Chunk chunk, UUID uuid, Map<UUID, List<ClaimPermission>> playersPermissions) {
        this.chunk = chunk;
        this.owner = CountryManager.getCountry(uuid);
        this.playersPermissions = playersPermissions;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public Country getOwner() {
        return owner;
    }

    public void setOwner(Country owner) {
        this.owner = owner;
    }

    public void addPlayerPermission(OfflinePlayer player, ClaimPermission permission) {
        List<ClaimPermission> permissions = playersPermissions.getOrDefault(player.getUniqueId(), new ArrayList<>());
        if (!permissions.contains(permission)) {
            permissions.add(permission);
            this.playersPermissions.put(player.getUniqueId(), permissions);
            ClaimManager.saveClaim(this, true);
        }
    }

    public void removePlayerPermission(OfflinePlayer player, ClaimPermission permission) {
        List<ClaimPermission> permissions = playersPermissions.getOrDefault(player.getUniqueId(), new ArrayList<>());
        if (permissions.contains(permission)) {
            permissions.remove(permission);
            if (permissions.isEmpty()) {
                this.playersPermissions.remove(player.getUniqueId());
            } else {
                this.playersPermissions.put(player.getUniqueId(), permissions);
            }
            ClaimManager.saveClaim(this, true);
        }
    }

    public boolean hasPermission(OfflinePlayer player, ClaimPermission permission) {
        return playersPermissions.getOrDefault(player.getUniqueId(), new ArrayList<>()).contains(permission);
    }

    public Map<UUID, List<ClaimPermission>> getPlayersPermissions() {
        return playersPermissions;
    }
}
