package fi.natroutter.foxframe.bot.listeners;


import fi.natroutter.foxframe.FoxFrame;
import fi.natroutter.foxframe.bot.DiscordBot;
import fi.natroutter.foxframe.bot.command.DiscordCommand;
import fi.natroutter.foxframe.bot.events.StringMenuEvent;
import fi.natroutter.foxframe.data.logs.LogChannel;
import fi.natroutter.foxframe.data.logs.LogMember;
import fi.natroutter.foxframe.data.logs.LogMenu;
import fi.natroutter.foxlib.logger.FoxLogger;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class StringMenuListener extends ListenerAdapter {

    private DiscordBot bot;
    private FoxLogger logger = FoxFrame.getLogger();

    public StringMenuListener(DiscordBot bot) {
        this.bot = bot;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (bot.commands() == null || bot.commands().isEmpty()) return;

        for (DiscordCommand cmd : bot.commands()) {

            if (cmd instanceof StringMenuEvent stringMenuEvent) {
                if (stringMenuEvent.StringMenus().size() > 5) {
                    event.replyEmbeds(FoxFrame.error("You can only use up to 5 menus in a single message.").build()).setEphemeral(true).queue();
                    logger.error("Message contains more than 5 selection menus!");
                    return;
                }

                for (StringSelectMenu menu : stringMenuEvent.StringMenus()) {
                    if (event.getComponentId().equals(menu.getCustomId())) {

                        logger.info("String menu has been interacted!",
                            new LogMember(event.getMember()),
                            new LogChannel(event.getChannel()),
                            new LogMenu(event.getInteraction())
                        );

                        stringMenuEvent.onStringMenuSelect(event);
                        return;
                    }
                }
            }
        }
    }
}
