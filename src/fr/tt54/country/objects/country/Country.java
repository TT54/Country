package fr.tt54.country.objects.country;

import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class Country {

    private UUID uuid;
    private String name;
    private Map<OfflinePlayer, Rank> members = new HashMap<>();
    private OfflinePlayer leader;
    private int level;
    private int maxClaims;
    private List<Chunk> chunksClaimed = new ArrayList<>();
    private List<Rank> ranks;
    private boolean opened;


    public Country(UUID uuid, String name, OfflinePlayer leader, List<Rank> ranks) {
        this.uuid = uuid;
        this.name = name;
        this.leader = leader;
        this.ranks = ranks;
        this.members.put(leader, getMaxRank(ranks));
        this.opened = false;
    }

    public Country(UUID uuid, String name, Map<OfflinePlayer, Rank> members, OfflinePlayer leader, int level, int maxClaims, List<Chunk> chunksClaimed, List<Rank> ranks, boolean opened) {
        this.uuid = uuid;
        this.name = name;
        this.members = members;
        this.leader = leader;
        this.level = level;
        this.maxClaims = maxClaims;
        this.chunksClaimed = chunksClaimed;
        this.ranks = ranks;
        this.opened = opened;
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
        return members;
    }

    public void addMember(OfflinePlayer member) {
        if (!this.hasMember(member))
            this.members.put(member, getMinRank(this.ranks));
    }

    public void removeMember(OfflinePlayer player) {
        if (this.hasMember(player))
            this.members.remove(player);
    }

    public boolean hasMember(OfflinePlayer player) {
        return this.members.containsKey(player);
    }

    public OfflinePlayer getLeader() {
        return leader;
    }

    public void setLeader(OfflinePlayer leader) {
        this.leader = leader;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public List<Rank> getRanks() {
        return ranks;
    }

    public void setRanks(List<Rank> ranks) {
        this.ranks = ranks;
    }

    public void setMembers(Map<OfflinePlayer, Rank> members) {
        this.members = members;
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
        if (rankToRemove >= 0)
            this.ranks.remove(rankToRemove);
    }

    public void setRank(OfflinePlayer player, Rank rank) {
        this.members.put(player, rank);
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
}
