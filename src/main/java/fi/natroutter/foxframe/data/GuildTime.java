package fi.natroutter.foxframe.data;

public record GuildTime(String joined, long daysInGuild) {
    public GuildTime(){this("Unknown", -1L);}
}