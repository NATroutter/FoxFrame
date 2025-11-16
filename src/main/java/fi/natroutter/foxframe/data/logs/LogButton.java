package fi.natroutter.foxframe.data.logs;

import fi.natroutter.foxlib.logger.types.ILogData;
import net.dv8tion.jda.api.components.buttons.Button;


public class LogButton implements ILogData {

    private String button = "Unknown";
    private String id = "0";

    public LogButton(Button button) {
        if (button != null) {
            this.button = button.getLabel();
            this.id = button.getCustomId();
        }
    }

    @Override
    public String key() {
        return "Button";
    }

    @Override
    public Object data() {
        return button + " (" + id + ")";
    }
}
