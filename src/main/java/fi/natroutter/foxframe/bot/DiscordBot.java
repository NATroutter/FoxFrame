package fi.natroutter.foxframe.bot;

import fi.natroutter.foxframe.FoxFrame;
import fi.natroutter.foxframe.bot.listeners.*;
import fi.natroutter.foxframe.bot.command.DiscordCommand;
import fi.natroutter.foxframe.data.ImageComparion;
import fi.natroutter.foxframe.permissions.IPermissionHandler;
import fi.natroutter.foxframe.permissions.PermissionHolder;
import fi.natroutter.foxlib.logger.FoxLogger;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AccountManager;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class DiscordBot {

    @Getter
    private JDABuilder builder;

    private FoxLogger logger = FoxFrame.getLogger();

    private JDA jda;
    private Consumer<JDA> connectedEvent = (guild)-> {};

    public JDA getJDA() {return jda;}
    public OnlineStatus status() {return OnlineStatus.ONLINE;}
    public Activity activity() {return null;}
    public List<GatewayIntent> intents() {
        return List.of(
                GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EXPRESSIONS,
                GatewayIntent.SCHEDULED_EVENTS, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_WEBHOOKS,
                GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.MESSAGE_CONTENT
        );
    }
    public List<ListenerAdapter> listener() { return Collections.emptyList(); }
    public List<DiscordCommand> commands() { return Collections.emptyList(); }


    public abstract String name();
    public abstract String version();
    public abstract String author();
    public abstract String token();
    public abstract IPermissionHandler permissionHandler(); //TODO remove abstract and set default to use sqlite default system
    public abstract PermissionHolder permissionHolder();

    public void whenConnected(Consumer<JDA> event) {
        connectedEvent = event;
    }

    public Icon iconFromUrl(String url) {
        try (InputStream avatarStream = URI.create(url).toURL().openStream()) {
            return Icon.from(avatarStream);
        } catch (Exception e) {
            logger.error("Failed to convert url to icon! : " + e.getMessage());
        }
        return null;
    }

    public DiscordBot() {
        if (token().equalsIgnoreCase("TOKEN_HERE")) {
            logger.error("You need to add your token to config.yaml!");
            return;
        }
        if (!FoxFrame.isTokenValidFormat(token())) {
            logger.error("Bot token is not formated correctly!");
            return;
        }

        builder = (intents().isEmpty() || intents() == null) ? JDABuilder.createDefault(token()) : JDABuilder.create(token(), intents());;
        builder.setStatus(status());
        if (activity() != null) {
            builder.setActivity(activity());
        }

        builder.setEnableShutdownHook(true);
        builder.setEventPassthrough(true);

        builder.addEventListeners(
                new SlashCommandListener(this, permissionHandler(), permissionHolder().bypassCommandCooldown()),
                new ButtonInteractListener(this),
                new EntityMenuListener(this),
                new StringMenuListener(this),
                new GuildJoinListener(this),
                new ModalSubmitListener(this)
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (isRunning()) {
                jda.shutdownNow();
            }
        }));
    }

    public void reloadCommands() {
        for (Guild guild : jda.getGuilds()) {
            registerGuildCommands(guild);
        }
        logger.info("All commands has been reloaded!");
    }

    public void registerGuildCommands(Guild guild) {
        List<SlashCommandData> list = new ArrayList<>();
        if (commands() == null || commands().isEmpty()) return;
        for (DiscordCommand command : commands()) {
            boolean regCondition = command.getRegisterCondition().test(jda.getSelfUser(), guild);
            if (regCondition) {
                SlashCommandData data = Commands.slash(command.getName().toLowerCase(), command.getDescription());
                if (!command.options().isEmpty()) {
                    data.addOptions(command.options());
                }
                list.add(data);
            }
        }
        guild.updateCommands().addCommands(list).queue();
    }

    public void connect() {
        if (builder == null) return;
        logger.info("Connecting to discord...");

        if (commands() != null && !commands().isEmpty()) {
            logger.info("Registering command : " + commands().stream().map(DiscordCommand::getName).collect(Collectors.joining(", ")));
        } else {
            logger.info("There are not commands registered!");
        }

        try {
            jda = builder.build();
            jda.awaitReady();

            //Register commands to bot
            for (Guild guild : jda.getGuilds()) {
                registerGuildCommands(guild);
            }

            //Register event handlers to bot
            if (listener() != null && !listener().isEmpty()) {
                listener().forEach(e-> {
                    jda.addEventListener(e);
                });
            }

            setupProfile();

            connectedEvent.accept(jda);

            logger.info("Bot connected successfully!");
        } catch (InterruptedException e) {
            logger.error("Interrupted : " + e.getMessage());
        }
    }

    private void setupProfile() {
        SelfUser bot = jda.getSelfUser();
        AccountManager botManager = bot.getManager();
        if (name() != null) {
            if (!bot.getName().equals(name())) {
                botManager.setName(name()).queue();
            }
        }
    }

    public void disconnect() {
        if (jda == null) return;
        jda.shutdown();
    }

    public boolean isRunning() {
        return jda != null && jda.getStatus() == JDA.Status.CONNECTED;
    }

}

