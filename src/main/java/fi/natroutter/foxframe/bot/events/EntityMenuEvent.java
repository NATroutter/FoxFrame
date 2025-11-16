package fi.natroutter.foxframe.bot.events;

import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.List;

public interface EntityMenuEvent {

    List<EntitySelectMenu> entityMenus();
    void onEntityMenuSelect(EntitySelectInteractionEvent event);

}
