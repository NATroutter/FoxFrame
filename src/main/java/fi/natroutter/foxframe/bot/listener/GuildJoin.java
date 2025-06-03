package fi.natroutter.foxframe.bot.listener;

import fi.natroutter.foxframe.command.CommandHandler;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildJoin extends ListenerAdapter {

    private CommandHandler commandHandler;

    public GuildJoin(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent e) {
        commandHandler.registerAll(e.getGuild());
    }
}
