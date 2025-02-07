package fi.natroutter.foxframe.console;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ConsoleData {

    private Guild selectedGuild = null;
    private TextChannel selectedChannel = null;
    private List<Guild> guilds = new ArrayList<>();
    private List<TextChannel> channels = new ArrayList<>();

}
