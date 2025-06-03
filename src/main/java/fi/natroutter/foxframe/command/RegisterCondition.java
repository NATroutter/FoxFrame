package fi.natroutter.foxframe.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

@FunctionalInterface
public interface RegisterCondition {
    boolean test(User bot, Guild guild);
}
