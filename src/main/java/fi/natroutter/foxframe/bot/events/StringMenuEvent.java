package fi.natroutter.foxframe.bot.events;

import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.List;

public interface StringMenuEvent {

    List<StringSelectMenu> StringMenus();
    void onStringMenuSelect(StringSelectInteractionEvent event);

}
