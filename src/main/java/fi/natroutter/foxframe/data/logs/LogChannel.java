package fi.natroutter.foxframe.data.logs;

import fi.natroutter.foxlib.logger.types.ILogData;
import net.dv8tion.jda.api.entities.channel.Channel;


public class LogChannel implements ILogData {

    private String channel = "Unknown";
    private String id = "0";

    public LogChannel(Channel channel) {
        if (channel != null) {
            this.channel = channel.getName();
            this.id = channel.getId();
        }
    }

    @Override
    public String key() {
        return "Channel";
    }

    @Override
    public Object data() {
        return "#"+ channel + " (" + id + ")";
    }
}
