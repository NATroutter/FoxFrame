package fi.natroutter.foxframe.data.logs;

import fi.natroutter.foxlib.logger.types.ILogData;
import net.dv8tion.jda.api.entities.Guild;

public class LogGuild implements ILogData {

    private String guild = "Unknown";
    private String id = "0";

    public LogGuild(Guild guild) {
        if (guild != null) {
            this.guild = guild.getName();
            this.id = guild.getId();
        }
    }

    @Override
    public String key() {
        return "Guild";
    }

    @Override
    public Object data() {
        return "#"+ guild + " (" + id + ")";
    }
}
