package fi.natroutter.foxframe.bot.listeners;


import fi.natroutter.foxframe.FoxFrame;
import fi.natroutter.foxframe.bot.DiscordBot;
import fi.natroutter.foxframe.bot.command.DiscordCommand;
import fi.natroutter.foxframe.bot.events.EntityMenuEvent;
import fi.natroutter.foxframe.data.logs.LogChannel;
import fi.natroutter.foxframe.data.logs.LogMember;
import fi.natroutter.foxframe.data.logs.LogMenu;
import fi.natroutter.foxlib.logger.FoxLogger;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EntityMenuListener extends ListenerAdapter {

    private DiscordBot bot;
    private FoxLogger logger = FoxFrame.getLogger();

    public EntityMenuListener(DiscordBot bot) {
        this.bot = bot;
    }

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        if (bot.commands() == null || bot.commands().isEmpty()) return;

        for (DiscordCommand cmd : bot.commands()) {

            if (cmd instanceof EntityMenuEvent entityMenuEvent) {
                if (entityMenuEvent.entityMenus().size() > 5) {
                    event.replyEmbeds(FoxFrame.error("You can only use up to 5 menus in a single message.").build()).setEphemeral(true).queue();
                    logger.error("You can only use up to 5 menus in a single message.");
                    return;
                }

                for (EntitySelectMenu menu : entityMenuEvent.entityMenus()) {
                    if (event.getComponentId().equals(menu.getCustomId())) {

                        logger.info("Entity menu has been interacted!",
                                new LogMember(event.getMember()),
                                new LogChannel(event.getChannel()),
                                new LogMenu(event.getInteraction())
                        );

                        entityMenuEvent.onEntityMenuSelect(event);
                        return;
                    }
                }
            }

        }
    }

}
