package fi.natroutter.foxframe.records;

public record GuildTime(String joined, long daysInGuild) {
    public GuildTime(){this("Unknown", -1L);}
}