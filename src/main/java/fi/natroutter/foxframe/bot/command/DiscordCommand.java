package fi.natroutter.foxframe.bot.command;

import fi.natroutter.foxframe.FoxFrame;
import fi.natroutter.foxframe.permissions.INode;
import fi.natroutter.foxlib.cooldown.Cooldown;
import fi.natroutter.foxlib.logger.FoxLogger;
import fi.natroutter.foxlib.logger.types.LogData;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class DiscordCommand {

    private static final FoxLogger logger = FoxFrame.getLogger();

    private final Map<String, AutoComplete> completions = new HashMap<>();

    @Getter
    private String name;

    @Getter @Setter
    private String description = "new command!";

    @Getter @Setter
    private RegisterCondition registerCondition = (bot, guild)-> true;

    @Getter @Setter
    private int deleteDelay = 0;

    @Getter @Setter
    private int cooldownTime = 10; //30

    @Getter @Setter
    private int cooldownCleanInterval = 60; // 60

    @Getter
    private boolean allowCooldownBypass = true;

    @Getter @Setter
    private INode permission;

    @Getter
    private final Cooldown<String> cooldown = new Cooldown.Builder<String>()
            .setDefaultCooldown(cooldownTime)
            .setDefaultTimeUnit(TimeUnit.SECONDS)
            .onCooldownExpiry((userID,data)-> {
                long seconds = TimeUnit.SECONDS.convert(data.cooldown().time(), data.cooldown().timeUnit());
                logger.warn("Removed expired cooldown!",
                        new LogData("Command", name),
                        new LogData("UserID", userID),
                        new LogData("Duration", seconds + "s")
                );
            })
            .build();


    public boolean hasCooldown(Member member) {
        return cooldown.hasCooldown(member.getId());
    }
    public void setCooldown(Member member) {
        cooldown.setCooldown(member.getId());
    }
    public long getCooldown(Member member) {
        return cooldown.getCooldown(member.getId(), TimeUnit.SECONDS);
    }

    public abstract void onCommand(SlashCommandInteractionEvent event);

    public List<OptionData> options() {
        return Collections.emptyList();
    }

    public DiscordCommand(String name) {
        this.name = name;
    }

    /**
     * Whether the bypass cooldown permission is honoured for this command. Call it from the
     * command's constructor.
     *
     * <p>Defaults to true, so a member holding the node skips the cooldown as usual. Set it to false
     * for a command whose cooldown protects something the permission has no business overriding, and
     * the cooldown then applies to everyone, node or not.
     */
    protected void allowCooldownBypass(boolean allow) {
        this.allowCooldownBypass = allow;
    }


    /**
     * Registers where one option's suggestions come from. Call it from the command's constructor.
     */
    protected void autoComplete(String option, AutoComplete source) {
        completions.put(option.toLowerCase(Locale.ROOT), source);
    }

    /** The source registered for an option, or null when it has none. */
    public AutoComplete autoCompleteFor(String option) {
        return completions.get(option.toLowerCase(Locale.ROOT));
    }

    /**
     * The declared options with autocomplete switched on wherever a source is registered.
     *
     * <p>Discord only sends the autocomplete interaction for options registered with the flag, so
     * deriving it from the registry means the flag and the handler can never disagree — and an
     * option that nothing answers for can never be left spinning.
     */
    public final List<OptionData> buildOptions() {
        List<OptionData> options = options();
        for (OptionData option : options) {
            if (completions.containsKey(option.getName().toLowerCase(Locale.ROOT))
                    && option.getType().canSupportChoices()) {
                option.setAutoComplete(true);
            }
        }
        return options;
    }


    /*
    *
    * Reply handler utilities
    *
    */

    //Reply Text
    public void reply(SlashCommandInteractionEvent event, String text) {
        reply(event, text, true);
    }
    public void reply(SlashCommandInteractionEvent event, String text, boolean hidden) {
        event.reply(text).setEphemeral(hidden).queue();
    }

    //Reply EmbedMessage
    public void reply(SlashCommandInteractionEvent event, MessageEmbed embed) {
        reply(event, embed, true);
    }
    public void reply(SlashCommandInteractionEvent event, MessageEmbed embed, boolean hidden) {
        event.replyEmbeds(embed).setEphemeral(hidden).queue();
    }

    //Reply EmbedBuilder
    public void reply(SlashCommandInteractionEvent event, EmbedBuilder embed) {
        reply(event, embed, true);
    }
    public void reply(SlashCommandInteractionEvent event, EmbedBuilder embed, boolean hidden) {
        event.replyEmbeds(embed.build()).setEphemeral(hidden).queue();
    }

    //Error
    public void replyError(SlashCommandInteractionEvent event, String message) {
        replyError(event, null, message, true, true);
    }
    public void replyError(SlashCommandInteractionEvent event, String title, String message) {
        replyError(event, title, message, true, true);
    }
    public void replyError(SlashCommandInteractionEvent event, String title, String message, boolean hidden, boolean deleteAfter) {
        if (deleteAfter) {
            FoxFrame.replyError(event, title, message, hidden);
            return;
        }
        FoxFrame.replyError(event, title, message, hidden, 0, null);
    }

    //Warning
    public void replyWarn(SlashCommandInteractionEvent event, String message) {
        replyWarn(event, null, message, true, true);
    }
    public void replyWarn(SlashCommandInteractionEvent event, String title, String message) {
        replyWarn(event, title, message, true, true);
    }
    public void replyWarn(SlashCommandInteractionEvent event, String title, String message, boolean hidden, boolean deleteAfter) {
        if (deleteAfter) {
            FoxFrame.replyWarn(event, title, message, hidden);
            return;
        }
        FoxFrame.replyWarn(event, title, message, hidden, 0, null);
    }

    //Info
    public void replyInfo(SlashCommandInteractionEvent event, String message) {
        replyInfo(event, null, message, true, true);
    }
    public void replyInfo(SlashCommandInteractionEvent event, String title, String message) {
        replyInfo(event, title, message, true, true);
    }
    public void replyInfo(SlashCommandInteractionEvent event, String title, String message, boolean hidden, boolean deleteAfter) {
        if (deleteAfter) {
            FoxFrame.replyInfo(event, title, message, hidden);
            return;
        }
        FoxFrame.replyInfo(event, title, message, hidden, 0, null);
    }

    //Success
    public void replySuccess(SlashCommandInteractionEvent event, String message) {
        replySuccess(event, null, message, true, true);
    }
    public void replySuccess(SlashCommandInteractionEvent event, String title, String message) {
        replySuccess(event, title, message, true, true);
    }
    public void replySuccess(SlashCommandInteractionEvent event, String title, String message, boolean hidden, boolean deleteAfter) {
        if (deleteAfter) {
            FoxFrame.replySuccess(event, title, message, hidden);
            return;
        }
        FoxFrame.replySuccess(event, title, message, hidden, 0, null);
    }

    //Usage
    public void replyUsage(SlashCommandInteractionEvent event, String message) {
        replyUsage(event, null, message, true, true);
    }
    public void replyUsage(SlashCommandInteractionEvent event, String title, String message) {
        replyUsage(event, title, message, true, true);
    }
    public void replyUsage(SlashCommandInteractionEvent event, String title, String message, boolean hidden, boolean deleteAfter) {
        if (deleteAfter) {
            FoxFrame.replyUsage(event, title, message, hidden);
            return;
        }
        FoxFrame.replyUsage(event, title, message, hidden, 0, null);
    }

}
