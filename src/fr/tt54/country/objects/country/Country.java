package fr.tt54.country.objects.country;

import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.manager.RelationManager;
import fr.tt54.country.objects.permissions.ClaimPermission;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.stream.Collectors;

public class Country {

    private UUID uuid;
    private String name;
    private Map<UUID, Rank> members = new HashMap<>();
    private UUID leader;
    private int level;
    private int points;
    private int maxClaims;
    private List<Chunk> chunksClaimed = new ArrayList<>();
    private List<Rank> ranks;
    private boolean opened;

    private double maxMoney;
    private double money;
    private int levelPoints;

    private Map<UUID, Relations> relations = new HashMap<>();
    private Map<UUID, Relations> relationsRequests = new HashMap<>();
    private Map<Relations, List<ClaimPermission>> relationsPermissions = new HashMap<>();


    public Country(UUID uuid, String name, OfflinePlayer leader, List<Rank> ranks) {
        this.uuid = uuid;
        this.name = name;
        this.leader = leader.getUniqueId();
        this.ranks = ranks;
        this.members.put(leader.getUniqueId(), getMaxRank(ranks));
        this.opened = false;

        this.maxMoney = 200000;
    }

    public Country(UUID uuid, String name, Map<OfflinePlayer, Rank> members, OfflinePlayer leader, int level, int points, int maxClaims, List<Chunk> chunksClaimed, List<Rank> ranks, boolean opened) {
        this.uuid = uuid;
        this.name = name;
        for (OfflinePlayer player : members.keySet()) {
            this.members.put(player.getUniqueId(), members.get(player));
        }
        this.leader = leader.getUniqueId();
        this.level = level;
        this.points = points;
        this.maxClaims = maxClaims;
        this.chunksClaimed = chunksClaimed;
        this.ranks = ranks;
        this.opened = opened;

        CountryManager.calculLevel(this);
        //this.maxMoney = 200000 * this.getLevel();
    }

    public int getLevelPoints() {
        return levelPoints;
    }

    public void setLevelPoints(int levelPoints) {
        this.levelPoints = levelPoints;
    }

    public static Rank getMaxRank(List<Rank> ranks) {
        if (ranks.isEmpty())
            return null;
        Rank rank = ranks.get(0);
        for (int i = 1; i < ranks.size(); i++) {
            if (rank.getPower() < ranks.get(i).getPower())
                rank = ranks.get(i);
        }
        return rank;
    }

    public static Rank getMinRank(List<Rank> ranks) {
        if (ranks.isEmpty())
            return null;
        Rank rank = ranks.get(0);
        for (int i = 1; i < ranks.size(); i++) {
            if (rank.getPower() > ranks.get(i).getPower())
                rank = ranks.get(i);
        }
        return rank;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<OfflinePlayer, Rank> getMembers() {
        Map<OfflinePlayer, Rank> members = new HashMap<>();
        for (UUID player : this.members.keySet()) {
            members.put(Bukkit.getOfflinePlayer(player), this.members.get(player));
        }
        return members;
    }

    public void addMember(OfflinePlayer member) {
        if (!this.hasMember(member))
            this.members.put(member.getUniqueId(), getMinRank(this.ranks));
    }

    public void removeMember(OfflinePlayer player) {
        if (this.hasMember(player))
            this.members.remove(player.getUniqueId());
    }

    public boolean hasMember(OfflinePlayer player) {
        return this.members.containsKey(player.getUniqueId());
    }

    public OfflinePlayer getLeader() {
        return Bukkit.getOfflinePlayer(leader);
    }

    public void setLeader(OfflinePlayer leader) {
        List<Rank> r = this.ranks.subList(0, this.ranks.size());
        r.sort((r1, r2) -> r2.getPower() - r1.getPower());
        this.setRank(Bukkit.getOfflinePlayer(this.leader), r.get(1));
        this.leader = leader.getUniqueId();
        this.setRank(leader, r.get(0));
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        CountryManager.calculMaxClaims(this);
    }

    public void addPoints(int amount) {
        this.points += amount;
        CountryManager.calculLevel(this);
        CountryManager.saveCountry(this);
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
        CountryManager.calculLevel(this);
        CountryManager.saveCountry(this);
    }

    public int getMaxClaims() {
        return maxClaims;
    }

    public void setMaxClaims(int maxClaims) {
        this.maxClaims = maxClaims;
    }

    public List<Chunk> getChunksClaimed() {
        return chunksClaimed;
    }

    public void setChunksClaimed(List<Chunk> chunksClaimed) {
        this.chunksClaimed = chunksClaimed;
    }

    public void claimChunk(Chunk chunk) {
        if (!this.chunksClaimed.contains(chunk)) {
            this.chunksClaimed.add(chunk);
        }
    }

    public void unclaimChunk(Chunk chunk) {
        this.chunksClaimed.remove(chunk);
    }

    public List<Rank> getRanks() {
        return ranks;
    }

    public void setRanks(List<Rank> ranks) {
        this.ranks = ranks;
    }

    public void setMembers(Map<OfflinePlayer, Rank> members) {
        for (OfflinePlayer player : members.keySet()) {
            this.members.put(player.getUniqueId(), members.get(player));
        }
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public void addRank(Rank rank) {
        if (!this.hasRank(rank.getName())) {
            this.ranks.add(rank);
        }
    }

    public boolean hasRank(String rankName) {
        for (Rank rank : this.ranks) {
            if (rank.getName().equalsIgnoreCase(rankName))
                return true;
        }
        return false;
    }

    public Rank getRank(String name) {
        for (Rank rank : this.ranks) {
            if (rank.getName().equalsIgnoreCase(name))
                return rank;
        }
        return null;
    }

    public boolean isOpened() {
        return opened;
    }

    public void removeRank(String name) {
        int rankToRemove = -1;
        for (Rank rank : this.ranks) {
            if (rank.getName().equalsIgnoreCase(name)) {
                rankToRemove = this.ranks.indexOf(rank);
            }
        }

        for (OfflinePlayer player : this.getMembers().entrySet().stream().filter(offlinePlayerRankEntry -> offlinePlayerRankEntry.getValue().getName().equalsIgnoreCase(name)).collect(Collectors
                .toList()).stream().map(Map.Entry::getKey).collect(Collectors.toList())) {
            setRank(player, this.getRank("member"));
        }
        if (rankToRemove >= 0)
            this.ranks.remove(rankToRemove);
    }

    public void setRank(OfflinePlayer player, Rank rank) {
        this.members.put(player.getUniqueId(), rank);
    }

    public void setRankPower(String name, int power) {
        if (hasRank(name)) {
            getRank(name).setPower(power);
        }
    }

    public void setRankPrefix(String name, String prefix) {
        if (hasRank(name)) {
            getRank(name).setPrefix(prefix);
        }
    }

    public void clearRelations() {
        this.relations.clear();
    }

    public void clearRelationsRequests() {
        this.relationsRequests.clear();
    }

    public void setRelationWith(UUID country, Relations relation) {
        this.relations.put(country, relation);
    }

    public void setRelationRequestWith(UUID country, Relations relation) {
        this.relationsRequests.put(country, relation);
        RelationManager.saveRelations(this);
    }

    public boolean hasRelation(UUID country, Relations relation) {
        if (relation == Relations.NEUTRAL)
            return !this.relations.containsKey(country);
        return this.relations.get(country) == relation;
    }

    public boolean hasRelationRequests(UUID country, Relations relation) {
        return this.relationsRequests.get(country) == relation;
    }

    public void removeRelation(UUID country) {
        this.relations.remove(country);
    }

    public void removeRelationRequests(UUID country) {
        this.relationsRequests.remove(country);
        RelationManager.saveRelations(this);
    }

    public Relations getRelationWith(UUID country) {
        return this.relations.getOrDefault(country, Relations.NEUTRAL);
    }

    public Relations getRelationRequestsWith(UUID country) {
        return this.relationsRequests.getOrDefault(country, Relations.NEUTRAL);
    }

    public Map<UUID, Relations> getRelations() {
        return relations;
    }

    public List<UUID> getRelations(Relations relation) {
        return relations.keySet().stream().filter(uuid -> getRelationWith(uuid) == relation).map(uuid -> CountryManager.getCountry(uuid).getUuid()).collect(Collectors.toList());
    }

    public Map<UUID, Relations> getRelationsRequests() {
        return relationsRequests;
    }

    public Map<Relations, List<ClaimPermission>> getRelationsPermissions() {
        return relationsPermissions;
    }

    public List<ClaimPermission> getRelationPermissions(Relations relation) {
        return relationsPermissions.getOrDefault(relation, new ArrayList<>());
    }

    public void setRelationPermissions(Relations relation, List<ClaimPermission> permissions) {
        this.relationsPermissions.put(relation, permissions);
    }

    public boolean hasRelationPermission(Relations relation, ClaimPermission permission) {
        return this.getRelationPermissions(relation).contains(permission);
    }

    public void addRelationPermission(Relations relation, ClaimPermission permission) {
        List<ClaimPermission> permissions = this.getRelationPermissions(relation);
        permissions.add(permission);
        this.setRelationPermissions(relation, permissions);
    }

    public void removeRelationPermission(Relations relation, ClaimPermission permission) {
        this.getRelationPermissions(relation).remove(permission);
    }

    public double getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(double maxMoney) {
        this.maxMoney = maxMoney;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void addMoney(double amount) {
        this.money += amount;
    }

    public void removeMoney(double amount) {
        this.money -= amount;
    }
}
