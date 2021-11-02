package fr.tt54.country.manager;

import fr.tt54.country.Main;
import fr.tt54.country.objects.country.Claim;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.permissions.ClaimPermission;
import fr.tt54.country.objects.permissions.CountryPermission;
import fr.tt54.country.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClaimManager {

    private static FileConfiguration claimsFileConfig;

    public static Map<Chunk, UUID> chunkClaimed = new HashMap<>(); //<Chunk claimed, countryUUID>
    public static Map<Chunk, Claim> claims = new HashMap<>(); //<Chunk, Claim>

    public static void enable() {
        reloadClaims();
    }

    public static void claimChunk(Country country, Chunk chunk) {
        chunkClaimed.put(chunk, country.getUuid());
        Claim claim = new Claim(chunk, country);
        claims.put(chunk, claim);

        country.claimChunk(chunk);
        Main.getInstance().log(country.getName() + " claimed a chunk in " + chunk.getX() * 16 + " " + chunk.getZ() * 16);

        saveClaim(claim, true);
    }

    public static Country getClaimCountry(Chunk chunk) {
        return getClaim(chunk).getOwner();
    }

    public static Country getClaimCountry(Claim claim) {
        return claim.getOwner();
    }

    public static Claim getClaim(Chunk chunk) {
        return claims.get(chunk);
    }

    public static void claimChunk(Country country, Location location) {
        claimChunk(country, location.getChunk());
    }

    public static void unclaimChunk(Country country, Location location) {
        if (isInClaimedChunk(location))
            unclaimChunk(country, location.getChunk());
    }

    private static void unclaimChunk(Country country, Chunk chunk) {
        if (getClaimCountry(chunk).getUuid().toString().equalsIgnoreCase(country.getUuid().toString())) {
            removeClaimInFile(getClaim(chunk), true);
            chunkClaimed.remove(chunk);
            claims.remove(chunk);
            country.unclaimChunk(chunk);

            Main.getInstance().log(country.getName() + " unclaimed a chunk in " + chunk.getX() * 16 + " " + chunk.getZ() * 16);
        }
    }

    public static void removeClaimInFile(Claim claim, boolean save) {
        claimsFileConfig.set(claim.getChunk().getWorld().getUID().toString() + "." + claim.getChunk().getX() + ";" + claim.getChunk().getZ(), null);
        if (save)
            saveClaimFile();
    }

    public static void saveClaim(Claim claim, boolean save) {
        claimsFileConfig.set(claim.getChunk().getWorld().getUID().toString() + "." + claim.getChunk().getX() + ";" + claim.getChunk().getZ() + ".country", claim.getOwner().getUuid().toString());

        Map<UUID, List<ClaimPermission>> playersPermissions = claim.getPlayersPermissions();
        for (UUID uuid : playersPermissions.keySet()) {
            claimsFileConfig.set(claim.getChunk().getWorld().getUID().toString() + "." + claim.getChunk().getX() + ";" + claim.getChunk().getZ() + ".permissions." + uuid.toString(),
                    ClaimPermission.getStringFromList(playersPermissions.get(uuid)));
        }
        if (save)
            saveClaimFile();
    }

    public static void saveClaims() {
        for (Claim claim : claims.values()) {
            saveClaim(claim, false);
        }
    }

    public static void saveClaimFile() {
        FileManager.saveFile(claimsFileConfig, "claims");
    }

    public static void reloadClaims() {
        claimsFileConfig = FileManager.getYmlFile("claims");
        chunkClaimed.clear();
        claims.clear();

        for (String worldUUID : claimsFileConfig.getKeys(false)) {
            try {
                World world = Bukkit.getWorld(UUID.fromString(worldUUID));
                ConfigurationSection section = claimsFileConfig.getConfigurationSection(worldUUID);

                for (String coordinates : section.getKeys(false)) {
                    int x = Integer.parseInt(coordinates.split(";")[0]);
                    int z = Integer.parseInt(coordinates.split(";")[1]);

                    Chunk chunk = world.getChunkAt(x, z);

                    ConfigurationSection permSection = section.getConfigurationSection(coordinates + ".permissions");
                    Map<UUID, List<ClaimPermission>> playersPermissions = new HashMap<>();
                    if (permSection != null) {
                        for (String playerUUID : permSection.getKeys(false)) {
                            playersPermissions.put(UUID.fromString(playerUUID), ClaimPermission.getListFromString(permSection.getString(playerUUID)));
                        }
                    }

                    Claim claim = new Claim(chunk, UUID.fromString(section.getString(coordinates + ".country")), playersPermissions);

                    claim.getOwner().claimChunk(chunk);

                    chunkClaimed.put(chunk, claim.getOwner().getUuid());
                    claims.put(chunk, claim);
                }
            } catch (NullPointerException npe) {
                Main.getInstance().logAlert("World '" + worldUUID + "' can't be loaded !");
            }
        }
    }

    public static boolean isInClaimedChunk(Location location) {
        return chunkClaimed.containsKey(location.getChunk());
    }

    public static boolean hasPermission(Player player, Claim claim, ClaimPermission claimPermission, CountryPermission countryPermission) {
        if (claim.hasPermission(player, claimPermission))
            return true;
        if (!CountryManager.hasCountry(player))
            return false;
        Country country = CountryManager.getPlayerCountry(player);
        if (claim.getOwner() != country && country != null) {
            return claim.getOwner().hasRelationPermission(claim.getOwner().getRelationWith(country.getUuid()), claimPermission);
        }
        return CountryManager.getRank(player).hasPermission(countryPermission);
    }

}
