package fr.tt54.country.manager;

import fr.tt54.country.objects.Claim;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.permissions.ClaimPermission;
import fr.tt54.country.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

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

        saveClaim(claim, true);
    }

    public static void claimChunk(Country country, Location location) {
        claimChunk(country, location.getChunk());
    }

    public static void saveClaim(Claim claim, boolean save) {
        claimsFileConfig.set(claim.getChunk().getWorld().getUID().toString() + "." + claim.getChunk().getX() + ";" + claim.getChunk().getZ() + ".country", claim.getOwner().getUuid().toString());

        Map<UUID, List<ClaimPermission>> playersPermissions = claim.getPlayersPermissions();
        for (UUID uuid : playersPermissions.keySet()) {
            claimsFileConfig.set(claim.getChunk().getWorld().getName() + "." + claim.getChunk().getX() + ";" + claim.getChunk().getZ() + ".permissions." + uuid.toString(),
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

                    System.out.println(claim.getOwner().getName());

                    chunkClaimed.put(chunk, claim.getOwner().getUuid());
                    claims.put(chunk, claim);
                }
            } catch (NullPointerException npe) {
                System.err.println("World '" + worldUUID + "' can't be loaded !");
            }
        }
    }

    public static boolean isInClaimedChunk(Location location) {
        return chunkClaimed.containsKey(location.getChunk());
    }

}
