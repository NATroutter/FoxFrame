package fi.natroutter.foxframe.bot.listeners;


import fi.natroutter.foxframe.FoxFrame;
import fi.natroutter.foxframe.bot.DiscordBot;
import fi.natroutter.foxframe.bot.command.DiscordCommand;
import fi.natroutter.foxframe.data.logs.*;
import fi.natroutter.foxframe.permissions.INode;
import fi.natroutter.foxframe.permissions.IPermissionHandler;
import fi.natroutter.foxlib.logger.FoxLogger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class SlashCommandListener extends ListenerAdapter {

    private DiscordBot bot;
    private IPermissionHandler permissionHandler;
    private INode bypassCooldownPermission;
    private FoxLogger logger = FoxFrame.getLogger();

    public SlashCommandListener(DiscordBot bot, IPermissionHandler permissionHandler, INode bypassCooldownPermission) {
        this.bot = bot;
        this.permissionHandler = permissionHandler;
        this.bypassCooldownPermission = bypassCooldownPermission;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (bot.commands() == null || bot.commands().isEmpty()) return;

        if (event.getGuild() == null) return;
        Member member = event.getMember();
        if (member == null) return;

        for (DiscordCommand cmd : bot.commands()) {
            if (event.getName().equalsIgnoreCase(cmd.getName())) {

                if (cmd.hasCooldown(member)) {
                    event.replyEmbeds(FoxFrame.error("This command is on cooldown for " + cmd.getCooldown(member) + " seconds.").build()).setEphemeral(true).queue();
                    logger.warn("Command usage failed because of cooldown!",
                        new LogMember(member),
                        new LogChannel(event.getChannel()),
                        new LogGuild(event.getGuild()),
                        new LogCommand(cmd, event.getOptions()),
                        new LogCooldown<>(cmd.getCooldown(), member.getId())
                    );
                    return;
                }
                if (cmd.getCooldownTime() > 0) {
                    permissionHandler.has(member, event.getGuild(), bypassCooldownPermission, ()->{
                        cmd.setCooldown(member);
                    });
                }

                logger.info("Member used command!",
                        new LogCommand(cmd, event.getOptions()),
                        new LogMember(member),
                        new LogChannel(event.getChannel()),
                        new LogGuild(event.getGuild())
                );

                if (cmd.getPermission() == null) {
                    cmd.onCommand(event);
                    return;
                }

                permissionHandler.has(member,event.getGuild(), cmd.getPermission(), ()->{
                    cmd.onCommand(event);
                }, ()-> {
                    event.replyEmbeds(FoxFrame.error("You don't have permissions to use this command!").build()).setEphemeral(true).queue();
                    logger.warn("Permission denied for command usage!",
                        new LogMember(member),
                        new LogChannel(event.getChannel()),
                        new LogGuild(event.getGuild()),
                        new LogCommand(cmd, event.getOptions())
                    );
                });

                if (cmd.getDeleteDelay() > 0) {
                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            try {
                                if (!event.getHook().isExpired()) {
                                    event.getHook().deleteOriginal().complete();
                                }
                            } catch (Exception ignore) {}
                        }
                    }, cmd.getDeleteDelay() * 1000L);
                }

            }
        }
    }
}
