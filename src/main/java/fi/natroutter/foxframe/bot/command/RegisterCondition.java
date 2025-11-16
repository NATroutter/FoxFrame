package fi.natroutter.foxframe.bot.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

@FunctionalInterface
public interface RegisterCondition {
    boolean test(User bot, Guild guild);
}
