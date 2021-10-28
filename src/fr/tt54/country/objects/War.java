package fr.tt54.country.objects;

import fr.tt54.country.objects.country.Country;

import java.util.UUID;

public class War {

    private UUID uuid;
    private Country country1;
    private Country country2;
    private Country winner;
    private int country1Kills;
    private int country2Kills;
    private long timeBegin;
    private boolean isFinished;

    public War(UUID uuid, Country country1, Country country2, Country winner, int country1Kills, int country2Kills, long timeBegin, boolean isFinished) {
        this.uuid = uuid;
        this.country1 = country1;
        this.country2 = country2;
        this.winner = winner;
        this.country1Kills = country1Kills;
        this.country2Kills = country2Kills;
        this.timeBegin = timeBegin;
        this.isFinished = isFinished;
    }

    public War(Country country1, Country country2) {
        this.country1 = country1;
        this.country2 = country2;

        this.uuid = UUID.randomUUID();
        this.timeBegin = System.currentTimeMillis();
        this.country1Kills = 0;
        this.country2Kills = 0;
        this.isFinished = false;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Country getCountry1() {
        return country1;
    }

    public Country getCountry2() {
        return country2;
    }

    public int getCountry1Kills() {
        return country1Kills;
    }

    public int getCountry2Kills() {
        return country2Kills;
    }

    public long getTimeBegin() {
        return timeBegin;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setCountry1Kills(int country1Kills) {
        this.country1Kills = country1Kills;
    }

    public void setCountry2Kills(int country2Kills) {
        this.country2Kills = country2Kills;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public Country getWinner() {
        return winner;
    }

    public void setWinner(Country winner) {
        this.winner = winner;
    }
}
