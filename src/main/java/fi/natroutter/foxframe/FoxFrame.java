package fi.natroutter.foxframe;

import fi.natroutter.foxframe.data.EmojiData;
import fi.natroutter.foxframe.interfaces.BaseCommand;
import fi.natroutter.foxframe.records.GuildTime;
import fi.natroutter.foxlib.FoxLib;
import fi.natroutter.foxlib.logger.FoxLogger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class FoxFrame {

    //TODO add permission system same as in FoxBot but change the mongoDB to sqlite?

    @Getter @Setter
    private static Color themeColor = Color.RED;

    @Getter @Setter
    private static EmojiData infoEmoji = new EmojiData("information_source", -1, false);

    @Getter @Setter
    private static EmojiData errorEmoji = new EmojiData("no_entry_sign", -1, false);

    @Getter @Setter
    private static EmojiData usageEmoji = new EmojiData("books", -1, false);

    @Getter @Setter
    private static FoxLogger logger = new FoxLogger.Builder()
            .setDebug(false)
            .setPruneOlderThanDays(35)
            .setSaveIntervalSeconds(300)
            .setParentFolder("FoxFrame")
            .build();

    private static MarkdownSanitizer sanitizer = new MarkdownSanitizer();

    public static EmbedBuilder embedTemplate() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(themeColor);
        return eb;
    }

    public static EmbedBuilder error(String msg) {
        return error(msg, null);
    }
    public static EmbedBuilder error(String title, String msg) {
        EmbedBuilder eb =  embedTemplate();
        eb.setColor(Color.decode("#ff0000"));
        eb.setTitle(title);
        if (msg != null) {
            eb.setDescription(msg);
        }
        return eb;
    }

    public static EmbedBuilder info(String msg) {
        EmbedBuilder eb =  embedTemplate();
        eb.setColor(Color.decode("#0073ff"));
        eb.setTitle(infoEmoji.asFormat()+" Info");
        eb.setDescription(msg);
        return eb;
    }

    public static EmbedBuilder usage(String usage) { return usage(null,usage); }
    public static EmbedBuilder usage(String leadingMessage, String usage) {
        EmbedBuilder eb =  embedTemplate();
        eb.setColor(Color.decode("#ff0000"));
        eb.setTitle(usageEmoji.asFormat()+" You didn't use that correctly!");
        if (leadingMessage != null) {
            eb.setDescription(leadingMessage + "\n\n> **Usage:** _" + usage + "_");
        } else {
            eb.setDescription("> **Usage:** _" + usage + "_");
        }
        return eb;
    }

    public static String stripMarkdown(String message) {
        if (message == null) {return "";}
        return sanitizer.withStrategy(MarkdownSanitizer.SanitizationStrategy.REMOVE).compute(message);
    }

    public static List<User> getUsersInVoice(Guild guild) {
        return guild.getVoiceChannels().stream()
                .flatMap(vc -> vc.getMembers().stream())
                .map(Member::getUser)
                .collect(Collectors.toList());
    }

    public static void sendPrivateMessage(User user, EmbedBuilder eb, String contentName) {
        user.openPrivateChannel().flatMap(pm -> pm.sendMessageEmbeds(eb.build())).queue((mm)->{
            logger.info("Sent private message to " + user.getGlobalName() + " ("+contentName+")");
        }, new ErrorHandler() .handle(ErrorResponse.CANNOT_SEND_TO_USER, (mm) -> {
            logger.info("Failed to send private message to " + user.getGlobalName() + " ("+contentName+")");
        }));
    }

    public static void removeMessages(MessageChannel channel, int amount) {
        List<Message> messages = channel.getHistory().retrievePast(amount).complete();
        for (Message message : messages) {
            if (message.isPinned()) {continue;}
            message.delete().complete();
        }
    }

    public static Instant unix() {
        return Instant.ofEpochMilli(System.currentTimeMillis());
    }
    public static Instant unix(long time) {
        return Instant.ofEpochMilli(time * 1000);
    }

    public static GuildTime getGuildTime(Member member) {
        ZoneId helsinki = ZoneId.of("Europe/Helsinki");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return getGuildTime(member, helsinki, formatter);
    }
    public static GuildTime getGuildTime(Member member, ZoneId timeZone, DateTimeFormatter formatter) {
        if (member != null) {
            formatter.withZone(timeZone);

            LocalDateTime ldt = LocalDateTime.ofInstant(member.getTimeJoined().toInstant(), timeZone);
            long daysInGuild = Duration.between(ldt, LocalDateTime.now()).toDays();
            String joined = member.getTimeJoined().format(formatter);
            return new GuildTime(joined, daysInGuild);
        }
        return new GuildTime("Unknown", -1L);
    }
}