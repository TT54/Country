package fr.tt54.country.objects.country;

import fr.tt54.country.objects.permissions.CountryPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rank {

    private String name;
    private String prefix;
    private int power;
    private List<CountryPermission> countryPermissions = new ArrayList<>();
    public static List<Rank> defaultRanks = new ArrayList<>();

    public Rank(String name, String prefix, int power) {
        this.name = name;
        this.prefix = prefix;
        this.power = power;
    }

    public Rank(String name, String prefix, int power, List<CountryPermission> countryPermissions) {
        this.name = name;
        this.prefix = prefix;
        this.power = power;
        this.countryPermissions.addAll(countryPermissions);
    }

    public Rank(String name, int power) {
        this.name = name;
        this.power = power;
        this.prefix = "";
    }

    public static void loadDefaultRanks() {
        defaultRanks.add(new Rank("leader", "[Leader]", 10, new ArrayList<>(CountryPermission.getPermissions().values())));
        defaultRanks.add(new Rank("officer", "[Officer]", 9, Arrays.asList(CountryPermission.INVITE_PLAYER, CountryPermission.OPEN_FACTION, CountryPermission.BUILD, CountryPermission.OPEN_CHEST, CountryPermission.USE_BUTTON, CountryPermission.OPEN_DOOR, CountryPermission.INTERACT, CountryPermission.CREATE_RANK, CountryPermission.DELETE_RANK, CountryPermission.EDIT_RANK, CountryPermission.SET_RANK, CountryPermission.CLAIM, CountryPermission.ACCESS_CLAIM, CountryPermission.KICK, CountryPermission.MANAGE_RELATIONS, CountryPermission.BANK_DEPOSIT, CountryPermission.BANK_WITHDRAW)));
        defaultRanks.add(new Rank("warrior", "[Warrior]", 1, Arrays.asList(CountryPermission.BUILD, CountryPermission.OPEN_CHEST, CountryPermission.BANK_DEPOSIT)));
        defaultRanks.add(new Rank("member", "[Member]", 0, Arrays.asList(CountryPermission.BANK_DEPOSIT)));
    }

    public boolean hasPermission(CountryPermission countryPermission) {
        return this.countryPermissions.contains(countryPermission);
    }

    public void addPermission(CountryPermission countryPermission) {
        if (!this.countryPermissions.contains(countryPermission))
            this.countryPermissions.add(countryPermission);
    }

    public void removePermission(CountryPermission countryPermission) {
        this.countryPermissions.remove(countryPermission);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public List<CountryPermission> getCountryPermissions() {
        return countryPermissions;
    }
}
