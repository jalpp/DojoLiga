package dojo.bot.Model;

import chariot.Client;
import chariot.ClientAuth;
import dojo.bot.Controller.Type;
import dojo.bot.Runner.Main;

public class Tournament {

    private String name;
    private String description;
    private Type tournamentType;
    private String password;
    private int clockTime;
    private int clockIncrement;
    private String FEN;
    private int waitMinsBeforeTourney;
    private int durationInMins;
    private boolean isRated;
    private int MIN_RATING_RULE;
    private int MAX_RATING_RULE;
    private int NUMBER_OF_GAMES_RATED;



    private ClientAuth client = Client.auth(Main.botToken);

    public Tournament(String name, Type type, String description, int clockTime, int clockIncrement, boolean isRated) {
        this.name = name;
        this.description = description;
        this.clockTime = clockTime;
        this.clockIncrement = clockIncrement;
        this.isRated = isRated;
        this.tournamentType = type;
    }

    public Tournament(String name, Type type, String description, String password, int clockTime, int clockIncrement, String FEN, int waitMinsBeforeTourney, int durationInMins, boolean isRated, int MIN_RATING_RULE, int MAX_RATING_RULE, int NUMBER_OF_GAMES_RATED) {
        this.name = name;
        this.description = description;
        this.password = password;
        this.clockTime = clockTime;
        this.clockIncrement = clockIncrement;
        this.FEN = FEN;
        this.waitMinsBeforeTourney = waitMinsBeforeTourney;
        this.durationInMins = durationInMins;
        this.isRated = isRated;
        this.MIN_RATING_RULE = MIN_RATING_RULE;
        this.MAX_RATING_RULE = MAX_RATING_RULE;
        this.NUMBER_OF_GAMES_RATED = NUMBER_OF_GAMES_RATED;
        this.tournamentType = type;
    }

    public Tournament() {
        this.clockIncrement = 0;
        this.clockTime = 0;

    }

    public ClientAuth getClient() {
        return client;
    }


    public Type getTournamentType() {
        return tournamentType;
    }


    public void setTournamentType(Type tournamentType) {
        this.tournamentType = tournamentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getClockTime() {
        return clockTime;
    }

    public void setClockTime(int clockTime) {
        this.clockTime = clockTime;
    }

    public int getClockIncrement() {
        return clockIncrement;
    }

    public void setClockIncrement(int clockIncrement) {
        this.clockIncrement = clockIncrement;
    }

    public String getFEN() {
        return FEN;
    }

    public void setFEN(String FEN) {
        this.FEN = FEN;
    }

    public int getWaitMinsBeforeTourney() {
        return waitMinsBeforeTourney;
    }

    public void setWaitMinsBeforeTourney(int waitMinsBeforeTourney) {
        this.waitMinsBeforeTourney = waitMinsBeforeTourney;
    }

    public int getDurationInMins() {
        return durationInMins;
    }

    public void setDurationInMins(int durationInMins) {
        this.durationInMins = durationInMins;
    }

    public boolean isRated() {
        return isRated;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }

    public int getMIN_RATING_RULE() {
        return MIN_RATING_RULE;
    }

    public void setMIN_RATING_RULE(int MIN_RATING_RULE) {
        this.MIN_RATING_RULE = MIN_RATING_RULE;
    }

    public int getMAX_RATING_RULE() {
        return MAX_RATING_RULE;
    }

    public void setMAX_RATING_RULE(int MAX_RATING_RULE) {
        this.MAX_RATING_RULE = MAX_RATING_RULE;
    }

    public int getNUMBER_OF_GAMES_RATED() {
        return NUMBER_OF_GAMES_RATED;
    }

    public void setNUMBER_OF_GAMES_RATED(int NUMBER_OF_GAMES_RATED) {
        this.NUMBER_OF_GAMES_RATED = NUMBER_OF_GAMES_RATED;
    }


















}
