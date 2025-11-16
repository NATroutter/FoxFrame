package fi.natroutter.foxframe.bot.listeners;

import fi.natroutter.foxframe.bot.DiscordBot;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildJoinListener extends ListenerAdapter {

    private DiscordBot discordBot;

    public GuildJoinListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        discordBot.registerGuildCommands(event.getGuild());
    }
}
