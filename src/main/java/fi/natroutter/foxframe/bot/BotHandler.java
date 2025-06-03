package fi.natroutter.foxframe.bot;

import fi.natroutter.foxframe.FoxFrame;
import fi.natroutter.foxframe.bot.listener.GuildJoin;
import fi.natroutter.foxframe.command.BaseCommand;
import fi.natroutter.foxframe.command.CommandHandler;
import fi.natroutter.foxframe.permissions.INode;
import fi.natroutter.foxframe.permissions.IPermissionHandler;
import fi.natroutter.foxlib.logger.FoxLogger;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.List;
import java.util.function.Consumer;

public abstract class BotHandler {

    @Getter
    private boolean connected = false;

    @Getter
    private JDABuilder builder;

    private CommandHandler commandHandler;

    private FoxLogger logger = FoxFrame.getLogger();

    private JDA jda;
    public JDA getJDA() {return jda;}

    public abstract String getBotName();
    public abstract String getVersion();
    public abstract String getAuthor();
    public abstract INode getCooldownBypassPerm();
    public abstract String getToken();
    public abstract List<GatewayIntent> getIntents();
    public abstract IPermissionHandler getPermissionHandler();

    public BotHandler() {
        if (getToken().equalsIgnoreCase("TOKEN_HERE")) {
            logger.error("You need to add your token to config.yaml!");
            return;
        }
        if (!FoxFrame.isTokenValidFormat(getToken())) {
            logger.error("Bot token is not formated correctly!");
            return;
        }

        builder = JDABuilder.create(getToken(), getIntents());
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

    public void reloadCommands() {
        if (commandHandler == null) return;
        commandHandler.registerAll();
        logger.info("All commands has been reloaded!");
    }

    public void registerCommand(BaseCommand command) {
        if (commandHandler == null) return;
        commandHandler.getCommands().add(command);
        logger.info("Registering command : " + command.getName());
    }

    public void connect(Consumer<JDA> consumer) {
        if (builder == null) return;
        logger.info("Connecting to discord...");

        try {
            jda = builder.build();
            jda.addEventListener(new GuildJoin(commandHandler));
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

