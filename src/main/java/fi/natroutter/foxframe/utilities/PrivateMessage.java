package fi.natroutter.foxframe.utilities;

import fi.natroutter.foxframe.FoxFrame;
import fi.natroutter.foxframe.data.logs.LogUser;
import fi.natroutter.foxlib.logger.FoxLogger;
import fi.natroutter.foxlib.logger.types.LogData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PrivateMessage {

    private static FoxLogger logger = FoxFrame.getLogger();
    private static ConcurrentHashMap<String, Long> privateMessages = new ConcurrentHashMap<>();

    public static void send(User user, EmbedBuilder eb, String contentKey) {
        send(user, eb.build(), contentKey, (e)->{});
    }
    public static void send(User user, MessageEmbed eb, String contentKey) {
        send(user, eb, contentKey, (e)->{});
    }
    public static void send(User user, MessageEmbed eb, String contentKey, Consumer<Boolean> result) {
        user.openPrivateChannel().flatMap(pm -> pm.sendMessageEmbeds(eb)).queue((mm)->{
            logger.info("Sent private message!",
                    new LogUser(user),
                    new LogData("ContentKey", contentKey)
            );
            privateMessages.put(contentKey+"-"+user.getId(), mm.getIdLong());
            result.accept(true);
        }, new ErrorHandler() .handle(ErrorResponse.CANNOT_SEND_TO_USER, (mm) -> {
            logger.info("Failed to send private message!",
                    new LogUser(user),
                    new LogData("ContentKey", contentKey)
            );
            result.accept(false);
        }));
    }

    //----------------------------------------------------------
    public static void edit(User user, EmbedBuilder embed, String contentKey) {
        edit(user,embed.build(),contentKey,(e)->{});
    }
    public static void edit(User user, MessageEmbed embed, String contentKey) {
        edit(user,embed,contentKey,(e)->{});
    }
    public static void edit(User user, MessageEmbed embed, String contentKey, Consumer<Boolean> result) {
        Long messageID = privateMessages.getOrDefault(contentKey+"-"+user.getId(),null);
        if (messageID == null) {
            logger.info("Failed to send private message! (Invalid history)",
                    new LogUser(user),
                    new LogData("ContentKey", contentKey)
            );
            result.accept(false);
            return;
        }
        user.openPrivateChannel().flatMap(pm -> pm.editMessageEmbedsById(messageID, embed)).queue((mm)->{
            logger.info("Sent private message!",
                    new LogUser(user),
                    new LogData("ContentKey", contentKey)
            );
            result.accept(true);
            privateMessages.put(contentKey+"-"+user.getId(), mm.getIdLong());
        }, new ErrorHandler() .handle(ErrorResponse.CANNOT_SEND_TO_USER, (mm) -> {
            logger.info("Failed to send private message!",
                    new LogUser(user),
                    new LogData("ContentKey", contentKey)
            );
        }));
        result.accept(false);
    }

    
    //----------------------------------------------------------


    public static void send(User user, String text, String contentKey) {
        send(user, text, contentKey, (e)->{});
    }
    public static void send(User user, String text, String contentKey, Consumer<Boolean> result) {
        user.openPrivateChannel().flatMap(pm -> pm.sendMessage(text)).queue((mm)->{
            logger.info("Sent private message!",
                    new LogUser(user),
                    new LogData("ContentKey", contentKey)
            );
            result.accept(true);
            privateMessages.put(contentKey+"-"+user.getId(), mm.getIdLong());
        }, new ErrorHandler() .handle(ErrorResponse.CANNOT_SEND_TO_USER, (mm) -> {
            logger.info("Failed to send private message!",
                    new LogUser(user),
                    new LogData("ContentKey", contentKey)
            );
            result.accept(false);
        }));
    }

    //----------------------------------------------------------
    public static void edit(User user, String text, String contentKey) {
        edit(user, text, contentKey, (e)->{});
    }
    public static void edit(User user, String text, String contentKey, Consumer<Boolean> result) {
        Long messageID = privateMessages.getOrDefault(contentKey+"-"+user.getId(),null);
        if (messageID == null) {
            logger.info("Failed to send private message! (Invalid history)",
                    new LogUser(user),
                    new LogData("ContentKey", contentKey)
            );
            result.accept(false);
            return;
        }
        user.openPrivateChannel().flatMap(pm -> pm.editMessageById(messageID, text)).queue((mm)->{
            logger.info("Sent private message!",
                    new LogUser(user),
                    new LogData("ContentKey", contentKey)
            );
            result.accept(true);
            privateMessages.put(contentKey+"-"+user.getId(), mm.getIdLong());
        }, new ErrorHandler() .handle(ErrorResponse.CANNOT_SEND_TO_USER, (mm) -> {
            logger.info("Failed to send private message!",
                    new LogUser(user),
                    new LogData("ContentKey", contentKey)
            );
        }));
        result.accept(false);
    }

}
