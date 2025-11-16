package fi.natroutter.foxframe.bot.events;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;

import java.util.List;

public interface ModalSubmitEvent {

    List<net.dv8tion.jda.api.modals.Modal> modals();
    void onModalSubmit(ModalInteractionEvent event);

}
