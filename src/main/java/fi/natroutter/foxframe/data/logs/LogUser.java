package fi.natroutter.foxframe.data.logs;

import fi.natroutter.foxlib.logger.types.ILogData;
import net.dv8tion.jda.api.entities.User;

public class LogUser implements ILogData {

    private String key = "User";
    private String user = "Unknown";
    private String id = "0";

    public LogUser(User user) {
        if (user != null) {
            this.user = user.getName();
            this.id = user.getId();
        }
    }
    public LogUser(String key, User user) {
        this.key = key;
        if (user != null) {
            this.user = user.getName();
            this.id = user.getId();
        }
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Object data() {
        return "@"+user + " (" + id + ")";
    }
}
