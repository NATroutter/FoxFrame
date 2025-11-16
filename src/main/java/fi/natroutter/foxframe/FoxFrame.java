package fi.natroutter.foxframe;

import fi.natroutter.foxframe.data.CustomEmoji;
import fi.natroutter.foxframe.data.GuildTime;
import fi.natroutter.foxframe.data.ImageComparion;
import fi.natroutter.foxframe.data.logs.LogUser;
import fi.natroutter.foxlib.logger.FoxLogger;
import fi.natroutter.foxlib.logger.types.LogData;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FoxFrame {

    //TODO add default permission system same as in FoxBot but change the mongoDB to sqlite?
    // - some kind of easy way that user can just implement like "PermissionHandlerSqlite"
    // - or something like that and thenn that just handles the permissions saving etc
    // - when user creates new instance of "DiscordBot" (Custom) user can just do somethging like
    // - "PermissionHandler = new sqlitePermissionHandler();"

    @Getter @Setter
    private static Color themeColor = new Color(215, 55, 45);

    @Getter @Setter
    private static Color SuccessColor = new Color(55, 215, 45);

    @Getter @Setter
    private static Color InfoColor = new Color(55, 45, 215);

    @Getter @Setter
    private static Color ErrorColor = new Color(255, 0, 0);

    @Getter @Setter
    private static Color WarnColor = new Color(215, 145, 45);

    @Getter @Setter
    private static CustomEmoji infoEmoji = new CustomEmoji("information_source", -1, false);

    @Getter @Setter
    private static CustomEmoji successEmoji = new CustomEmoji("white_check_mark", -1, false);

    @Getter @Setter
    private static CustomEmoji warnEmoji = new CustomEmoji("warning", -1, false);

    @Getter @Setter
    private static CustomEmoji errorEmoji = new CustomEmoji("no_entry_sign", -1, false);

    @Getter @Setter
    private static CustomEmoji usageEmoji = new CustomEmoji("books", -1, false);

    @Getter @Setter
    private static CustomEmoji helpEmoji = new CustomEmoji("scroll", -1, false);

    @Getter @Setter
    private static boolean useEmojis = true;

    @Getter @Setter
    private static int defaultDeleteMessageTime = 30;

    @Getter @Setter
    private static TimeUnit defaultDeleteMessageTimeUnit = TimeUnit.SECONDS;

    @Getter @Setter
    private static boolean printStackTrace = false;

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

    public static boolean isTokenValidFormat(String token) {
        String regex = "^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(token);
        return matcher.matches();
    }

    public static void replyError(IReplyCallback event, String message) { replyError(event, null, message, true, null, null); }
    public static void replyError(IReplyCallback event, String title, String message) { replyError(event, title, message, true, null, null); }
    public static void replyError(IReplyCallback event, String title, String message, boolean hidden) { replyError(event, title, message, hidden, null, null); }
    public static void replyError(IReplyCallback event, String title, String message, boolean hidden, Integer deleteAfter) { replyError(event, title, message, hidden, deleteAfter, null); }
    public static void replyError(IReplyCallback event, String title, String message, boolean hidden, Integer deleteAfter, TimeUnit timeUnit) {
        reply(event, error(title,message), title, message, hidden, deleteAfter, timeUnit);
    }

    public static void replyWarn(IReplyCallback event, String message) { replyWarn(event, null, message, true, null, null); }
    public static void replyWarn(IReplyCallback event, String title, String message) { replyWarn(event, title, message, true, null, null); }
    public static void replyWarn(IReplyCallback event, String title, String message, boolean hidden) { replyWarn(event, title, message, hidden, null, null); }
    public static void replyWarn(IReplyCallback event, String title, String message, boolean hidden, Integer deleteAfter) { replyWarn(event, title, message, hidden, deleteAfter, null); }
    public static void replyWarn(IReplyCallback event, String title, String message, boolean hidden, Integer deleteAfter, TimeUnit timeUnit) {
        reply(event, warn(title,message), title, message, hidden, deleteAfter, timeUnit);
    }

    public static void replyInfo(IReplyCallback event, String message) { replyInfo(event, null, message, true, null, null); }
    public static void replyInfo(IReplyCallback event, String title, String message) { replyInfo(event, title, message, true, null, null); }
    public static void replyInfo(IReplyCallback event, String title, String message, boolean hidden) { replyInfo(event, title, message, hidden, null, null); }
    public static void replyInfo(IReplyCallback event, String title, String message, boolean hidden, Integer deleteAfter) { replyInfo(event, title, message, hidden, deleteAfter, null); }
    public static void replyInfo(IReplyCallback event, String title, String message, boolean hidden, Integer deleteAfter, TimeUnit timeUnit) {
        reply(event, info(title,message), title, message, hidden, deleteAfter, timeUnit);
    }

    public static void replySuccess(IReplyCallback event, String message) { replySuccess(event, null, message, true, null, null); }
    public static void replySuccess(IReplyCallback event, String title, String message) { replySuccess(event, title, message, true, null, null); }
    public static void replySuccess(IReplyCallback event, String title, String message, boolean hidden) { replySuccess(event, title, message, hidden, null, null); }
    public static void replySuccess(IReplyCallback event, String title, String message, boolean hidden, Integer deleteAfter) { replySuccess(event, title, message, hidden, deleteAfter, null); }
    public static void replySuccess(IReplyCallback event, String title, String message, boolean hidden, Integer deleteAfter, TimeUnit timeUnit) {
        reply(event, success(title,message), title, message, hidden, deleteAfter, timeUnit);
    }

    public static void replyUsage(IReplyCallback event, String message) { replyUsage(event, null, message, true, null, null); }
    public static void replyUsage(IReplyCallback event, String title, String message) { replyUsage(event, title, message, true, null, null); }
    public static void replyUsage(IReplyCallback event, String title, String message, boolean hidden) { replyUsage(event, title, message, hidden, null, null); }
    public static void replyUsage(IReplyCallback event, String title, String message, boolean hidden, Integer deleteAfter) { replyUsage(event, title, message, hidden, deleteAfter, null); }
    public static void replyUsage(IReplyCallback event, String title, String message, boolean hidden, Integer deleteAfter, TimeUnit timeUnit) {
        reply(event, usage(title,message), title, message, hidden, deleteAfter, timeUnit);
    }

    private static void reply(IReplyCallback event, EmbedBuilder eb, String title, String message,boolean hidden, Integer deleteAfter, TimeUnit timeUnit) {
        int deleteIn = (deleteAfter == null ? defaultDeleteMessageTime : deleteAfter);
        TimeUnit unit = (timeUnit == null ? defaultDeleteMessageTimeUnit : timeUnit);

        if (deleteIn > 0) {
            eb.setFooter("This message will be deleted after " + deleteIn + " " + unit.name().toLowerCase());
        }
        event.replyEmbeds(eb.build()).setEphemeral(hidden).queue(msg-> {
            if (deleteIn > 0) {
                delayedDelete(msg, deleteIn, unit);
            }
        }, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (err) -> {
            logger.error("Failed to send usage message!",err, new LogData("Title", title), new LogData("Message", message));
        }));
    }


    public static EmbedBuilder error(String msg) {
        return error(null, msg);
    }
    public static EmbedBuilder error(String title, String msg) {
        EmbedBuilder eb =  embedTemplate();
        eb.setColor(ErrorColor);
        if (title == null) title = "Error!";
        String ebTitle = (useEmojis ? errorEmoji.asFormat():"")+" "+title;
        eb.setDescription("## "+ebTitle + "\n" +msg);
        return eb;
    }

    public static EmbedBuilder warn(String msg) {return warn(null,msg);}
    public static EmbedBuilder warn(String title, String msg) {
        EmbedBuilder eb =  embedTemplate();
        eb.setColor(WarnColor);
        if (title == null) title = "Warning!";
        String ebTitle = (useEmojis ? warnEmoji.asFormat():"")+" "+title;
        eb.setDescription("## "+ebTitle + "\n" +msg);
        return eb;
    }

    public static EmbedBuilder info(String msg) {return info(null,msg);}
    public static EmbedBuilder info(String title, String msg) {
        EmbedBuilder eb =  embedTemplate();
        eb.setColor(InfoColor);
        if (title == null) title = "Info!";
        String ebTitle = (useEmojis ? infoEmoji.asFormat():"")+" "+title;
        eb.setDescription("## "+ebTitle + "\n" +msg);
        return eb;
    }

    public static EmbedBuilder success(String msg) {return success(null,msg);}
    public static EmbedBuilder success(String title, String msg) {
        EmbedBuilder eb =  embedTemplate();
        eb.setColor(SuccessColor); // Success.

        if (title == null) title = "Success!";
        String ebTitle = (useEmojis ? successEmoji.asFormat():"")+" "+title;
        eb.setDescription("## "+ebTitle + "\n" +msg);
        return eb;
    }

    public static EmbedBuilder usage(String usage) { return usage(null,usage); }
    public static EmbedBuilder usage(String title, String usage) {
        EmbedBuilder eb =  embedTemplate();
        eb.setColor(InfoColor);
        if (title == null) title = "You didn't use that correctly!";
        String ebTitle = (useEmojis ? usageEmoji.asFormat():"")+" "+title;
        eb.setDescription("## "+ebTitle + "\n> **Usage:** *"+usage+"*");
        return eb;
    }



    public record HelpEntry(String command, String description) {}
    public static EmbedBuilder helpMessage(String title, String description, HelpEntry... helps) {
        return helpMessage(title,description,false,helps);
    }
    public static EmbedBuilder helpMessage(String title, String description,boolean spaced, HelpEntry... helps) {
        EmbedBuilder eb =  embedTemplate();
        eb.setColor(InfoColor);
        eb.setTitle((useEmojis ? helpEmoji.asFormat():"")+" " + title);

        String help = Arrays.stream(helps).map(e->
                "> ### **"+e.command()+"**\n> _" + e.description() + "_\n" + (spaced ? "\n" : "")
        ).collect(Collectors.joining());

        if (description != null) {
            eb.setDescription(description + "\n\n" + help);
        } else {
            eb.setDescription(help);
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
            logger.info("Sent private message!",
                new LogUser(user),
                new LogData("ContentName", contentName)
            );
        }, new ErrorHandler() .handle(ErrorResponse.CANNOT_SEND_TO_USER, (mm) -> {
            logger.info("Failed to send private message!",
                    new LogUser(user),
                    new LogData("ContentName", contentName)
            );
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
    public static GuildTime getGuildTime(Member member, String timeZoneName, String dateFormatPattern) {
        ZoneId helsinki = ZoneId.of(timeZoneName);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatPattern);
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

    public static ImageComparion compareImageDataFromURL(String oldImageURL, String newImageURL) {
        try{
            //Get current avatar and read bytes
            BufferedImage oldImgBuffer = ImageIO.read(URI.create(oldImageURL).toURL());
            ByteArrayOutputStream oldByteArray = new ByteArrayOutputStream();
            ImageIO.write(oldImgBuffer, "png", oldByteArray);
            byte[] oldImageBytes = oldByteArray.toByteArray();

            //Get new avatar and read bytes
            BufferedImage newImgBuffer = ImageIO.read(URI.create(newImageURL).toURL());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(newImgBuffer, "png", baos);
            byte[] newImageBytes = baos.toByteArray();

            if (Arrays.equals(oldImageBytes, newImageBytes)) {
                return ImageComparion.SAME_IMAGE;
            } else {
                return ImageComparion.NOT_SAME_IMAGE;
            }

        } catch (IOException error) {
            logger.error("Failed to compare images!",error,
                new LogData("OldImage", oldImageURL),
                new LogData("NewImage", newImageURL)
            );
            return ImageComparion.ERROR;
        }
    }

    public static void delayedDelete(InteractionHook message) {
        delayedDelete(message, defaultDeleteMessageTime, defaultDeleteMessageTimeUnit);
    }
    public static void delayedDelete(InteractionHook message, int seconds) {
        delayedDelete(message, seconds, defaultDeleteMessageTimeUnit);
    }
    public static void delayedDelete(InteractionHook message, int seconds, TimeUnit timeUnit) {
        // Try to fetch the original message before attempting to delete
        message.retrieveOriginal().queueAfter(seconds, timeUnit,
                original -> {
                    // If retrieval succeeds, schedule deletion
                    message.deleteOriginal().queue(
                            success -> {},
                            failure -> {
                                if (failure instanceof ErrorResponseException error) {
                                    if (error.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE || error.getErrorResponse() == ErrorResponse.UNKNOWN_CHANNEL) {
                                        return;
                                    }
                                }
                                if (printStackTrace) failure.printStackTrace();
                            }
                    );
                },
                fetchFailure -> {
                    // If the message does not exist, do nothing
                    if (fetchFailure instanceof ErrorResponseException error) {
                        if (error.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE || error.getErrorResponse() == ErrorResponse.UNKNOWN_CHANNEL) {
                            return;
                        }
                    }
                    if (printStackTrace) fetchFailure.printStackTrace();
                }
        );
    }
}