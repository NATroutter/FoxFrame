package fi.natroutter.foxframe.bot.listeners;


import fi.natroutter.foxframe.FoxFrame;
import fi.natroutter.foxframe.bot.DiscordBot;
import fi.natroutter.foxframe.bot.command.DiscordCommand;
import fi.natroutter.foxframe.bot.events.ButtonPressEvent;
import fi.natroutter.foxframe.data.logs.LogButton;
import fi.natroutter.foxframe.data.logs.LogChannel;
import fi.natroutter.foxframe.data.logs.LogMember;
import fi.natroutter.foxlib.logger.FoxLogger;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class ButtonInteractListener extends ListenerAdapter {

    private DiscordBot bot;
    private FoxLogger logger = FoxFrame.getLogger();

    public ButtonInteractListener(DiscordBot bot) {
        this.bot = bot;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (bot.commands() == null || bot.commands().isEmpty()) return;
        for (DiscordCommand cmd : bot.commands()) {
            if (!(cmd instanceof ButtonPressEvent buttonPressEvent)) {
                return;
            }

            for (Button button : buttonPressEvent.buttons()) {
                if (event.getComponentId().equalsIgnoreCase(button.getCustomId())) {

                    logger.info("Button pressed!",
                        new LogButton(button),
                        new LogMember(event.getMember()),
                        new LogChannel(event.getChannel())
                    );
                    buttonPressEvent.onButtonPress(event);
                    return;
                }
            }
        }
    }

}
