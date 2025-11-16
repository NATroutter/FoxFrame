package fi.natroutter.foxframe;

import fi.natroutter.foxlib.logger.FoxLogger;
import fi.natroutter.foxlib.logger.types.LogData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.concurrent.TimeUnit;

public class Reply {

    private static FoxLogger logger = FoxFrame.getLogger();

    public static class ReplyBuilder {
        private IReplyCallback event;
        private boolean hidden = true;
        private int deleteAfter = 3;
        private TimeUnit timeUnit = TimeUnit.SECONDS;

        public ReplyBuilder(IReplyCallback event) {
            this.event = event;
        }

        public ReplyBuilder setHidden(boolean value) {
            this.hidden = value;
            return this;
        }
        public ReplyBuilder setDeleteAfter(int value) {
            this.deleteAfter = value;
            return this;
        }
        public ReplyBuilder setTimeUnit(TimeUnit value) {
            this.timeUnit = value;
            return this;
        }
        public void send(String title, String message) {
            reply(event, FoxFrame.usage(title,message), title, message, deleteAfter, timeUnit, hidden);
        }
    }

    //

    public static void error(IReplyCallback event, String title, String message) {

        new ReplyBuilder(event).setHidden(true).send("awd", "awd");

    //    Reply.error(event, "title", "message", settings)
    }


    public static void replyUsage(IReplyCallback event, String message) { replyUsage(event, null, message, null, null, true); }
    public static void replyUsage(IReplyCallback event, String title, String message) { replyUsage(event, title, message, null, null, true); }
    public static void replyUsage(IReplyCallback event, String title, String message, Integer deleteAfter) { replyUsage(event, title, message, deleteAfter, null, true); }
    public static void replyUsage(IReplyCallback event, String title, String message, Integer deleteAfter, TimeUnit timeUnit) { replyUsage(event, title, message, deleteAfter, timeUnit, true); }
    public static void replyUsage(IReplyCallback event, String title, String message, Integer deleteAfter, TimeUnit timeUnit, boolean hidden) {
        reply(event, FoxFrame.usage(title,message), title, message, deleteAfter, timeUnit, hidden);
    }

    private static void reply(IReplyCallback event, EmbedBuilder eb, String title, String message, Integer deleteAfter, TimeUnit timeUnit, boolean hidden) {
        int deleteIn = (deleteAfter == null ? FoxFrame.getDefaultDeleteMessageTime() : deleteAfter);
        TimeUnit unit = (timeUnit == null ? FoxFrame.getDefaultDeleteMessageTimeUnit() : timeUnit);

        if (deleteIn > 0) {
            eb.setFooter("This message will be deleted after " + deleteIn + " " + unit.name().toLowerCase());
        }
        event.replyEmbeds(eb.build()).setEphemeral(hidden).queue(msg-> {
            if (deleteIn > 0) {
                FoxFrame.delayedDelete(msg, deleteIn, unit);
            }
        }, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (err) -> {
            logger.error("Failed to send usage message!",err, new LogData("Title", title), new LogData("Message", message));
        }));
    }

}
