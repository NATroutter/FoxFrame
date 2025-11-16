package fi.natroutter.foxframe.bot.listeners;


import fi.natroutter.foxframe.FoxFrame;
import fi.natroutter.foxframe.bot.DiscordBot;
import fi.natroutter.foxframe.bot.command.DiscordCommand;
import fi.natroutter.foxframe.bot.events.ModalSubmitEvent;
import fi.natroutter.foxframe.data.logs.LogChannel;
import fi.natroutter.foxframe.data.logs.LogMember;
import fi.natroutter.foxframe.data.logs.LogModal;
import fi.natroutter.foxlib.logger.FoxLogger;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;

public class ModalSubmitListener extends ListenerAdapter {

    private DiscordBot bot;
    private FoxLogger logger = FoxFrame.getLogger();

    public ModalSubmitListener(DiscordBot bot) {
        this.bot = bot;
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (bot.commands() == null || bot.commands().isEmpty()) return;

        for (DiscordCommand cmd : bot.commands()) {

            if (cmd instanceof ModalSubmitEvent modalEvent) {
                for (Modal modal : modalEvent.modals()) {
                    if (event.getModalId().equals(modal.getId())) {

                        logger.info("Modal submitted!",
                            new LogMember(event.getMember()),
                            new LogChannel(event.getChannel()),
                            new LogModal(event.getInteraction())
                        );

                        modalEvent.onModalSubmit(event);
                        return;
                    }
                }
            }

        }
    }

}
