package fi.natroutter.foxframe.interfaces;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;

public interface HandlerFrame {

    String getBotName();
    String getVersion();
    String getAuthor();
    boolean isConnected();
    JDA getJDA();

}

