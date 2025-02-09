package fi.natroutter.foxframe.interfaces;

import fi.natroutter.foxframe.FoxFrame;
import fi.natroutter.foxframe.command.BaseCommand;
import fi.natroutter.foxframe.command.CommandHandler;
import fi.natroutter.foxframe.permissions.INode;
import fi.natroutter.foxframe.permissions.IPermissionHandler;
import fi.natroutter.foxlib.logger.FoxLogger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class HandlerFrame {

    @Getter
    private boolean connected = false;

    @Getter
    private JDABuilder builder;

    private CommandHandler commandHandler;

    private List<ListenerAdapter> listeners = new ArrayList<>();

    private FoxLogger logger = FoxFrame.getLogger();

    private JDA jda;
    public JDA getJDA() {return jda;}

    public abstract String getBotName();
    public abstract String getVersion();
    public abstract String getAuthor();
    public abstract INode getCooldownBypassPerm();
    public abstract String getToken();
    public abstract IPermissionHandler getPermissionHandler();

    public HandlerFrame() {
        if (getToken().equalsIgnoreCase("TOKEN_HERE")) {
            logger.error("You need to add your token to config.yaml!");
            return;
        }
        if (!FoxFrame.isTokenValidFormat(getToken())) {
            logger.error("Bot token is not formated correctly!");
            return;
        }

        builder = JDABuilder.create(getToken(),
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.GUILD_WEBHOOKS,
                GatewayIntent.GUILD_INVITES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGE_TYPING,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.SCHEDULED_EVENTS

        );
        builder.setActivity(Activity.watching("your behavior"));
        builder.setStatus(OnlineStatus.ONLINE);

        this.commandHandler = new CommandHandler(this,getPermissionHandler(),getCooldownBypassPerm());
        builder.setEnableShutdownHook(true);
        builder.setEventPassthrough(true);
        builder.addEventListeners(commandHandler);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (jda != null && connected) {
                jda.shutdownNow();
            }
        }));
    }

    public void registerCommand(BaseCommand command) {
        if (commandHandler == null) return;
        commandHandler.getCommands().add(command);
    }

    public void connect(Consumer<JDA> consumer) {
        if (builder == null) return;
        logger.info("Connecting to discord...");

        try {
            jda = builder.build();
            jda.awaitReady();
            connected = true;
            commandHandler.registerAll();
            logger.info("Bot connected successfully!");
            consumer.accept(jda);
        } catch (InterruptedException e) {
            logger.error("Interrupted : " + e.getMessage());
        }
    }

    public void shutdown() {
        if (jda == null) return;;
        jda.shutdown();
        connected = false;
    }

}

