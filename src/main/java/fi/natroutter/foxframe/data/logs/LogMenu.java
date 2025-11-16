package fi.natroutter.foxframe.data.logs;

import fi.natroutter.foxlib.logger.types.ILogData;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.*;

import java.util.ArrayList;
import java.util.List;

public class LogMenu implements ILogData {

    private String data;
    private String id;

    public LogMenu(StringSelectInteraction menu) {
        this.id = menu.getId();

        List<SelectOption> options = menu.getSelectedOptions();
        if (options.isEmpty()) {
            this.data = "String("+id+") (NO DATA)";
            return;
        };
        List<String> entries = new ArrayList<>();

        options.forEach(opt-> entries.add(opt.getLabel().replace(" ", "_")+":"+opt.getValue()));

        this.data = "String("+id+") (" + String.join(" ", entries) + ")";
    }

    public LogMenu(EntitySelectInteraction menu) {
        this.id = menu.getId();

        String type = menu.getSelectMenu().getType().name();
        String typeCapitalized = type.substring(0, 1).toUpperCase() + type.substring(1);

        List<String> entries = new ArrayList<>();
        if (menu.getValues().isEmpty()) {
            this.data = typeCapitalized + "("+id+") (NO DATA)";
            return;
        }

        menu.getValues().forEach(entity -> {
            entries.add(entity.getId());
        });

        this.data = typeCapitalized + "("+id+") (" + String.join(" ", entries) + ")";
    }

    @Override
    public String key() {
        return "Menu";
    }

    @Override
    public Object data() {
        return data;
    }
}
