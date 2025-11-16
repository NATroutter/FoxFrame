package fi.natroutter.foxframe.data.logs;

import fi.natroutter.foxlib.logger.types.ILogData;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.ArrayList;
import java.util.List;

public class LogModal implements ILogData {

    private String data;
    private String id;

    public LogModal(ModalInteraction modal) {
        this.id = modal.getId();

        List<ModalMapping> mappings = modal.getValues();
        if (mappings.isEmpty()) {
            data = id + " (NO DATA)";
        }
        List<String> entries = new ArrayList<>();
        for (ModalMapping arg : mappings) {
            entries.add(arg.getCustomId()+":"+arg.getAsString());
        }
        this.data = id + " (" + String.join(" ", entries) + ")";
    }

    @Override
    public String key() {
        return "Modal";
    }

    @Override
    public Object data() {
        return data;
    }
}
