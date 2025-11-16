package fi.natroutter.foxframe.data.logs;

import fi.natroutter.foxlib.logger.types.ILogData;
import net.dv8tion.jda.api.entities.Member;

public class LogMember implements ILogData {

    private String key = "Member";
    private String member = "Unknown";
    private String id = "0";

    public LogMember(Member member) {
        if (member != null) {
            this.member = member.getUser().getName();
            this.id = member.getId();
        }
    }
    public LogMember(String key, Member member) {
        this.key = key;
        if (member != null) {
            this.member = member.getUser().getName();
            this.id = member.getId();
        }
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Object data() {
        return "@"+ member + " (" + id + ")";
    }
}
