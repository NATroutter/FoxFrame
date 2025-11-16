package fi.natroutter.foxframe.bot.events;

import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.List;

public interface ButtonPressEvent {

    List<Button> buttons();
    void onButtonPress(ButtonInteractionEvent event);
}
