package fi.natroutter.foxframe.data.logs;

import fi.natroutter.foxframe.bot.command.DiscordCommand;
import fi.natroutter.foxlib.logger.types.ILogData;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.ArrayList;
import java.util.List;

public class LogCommand implements ILogData {

    private String commandLog;

    public LogCommand(DiscordCommand command, List<OptionMapping> options) {

        String cmdArgs = optionsAsString(options).trim();
        commandLog = command.getName().trim() + (!cmdArgs.isEmpty() ? " " + cmdArgs : "");

    }

    private String optionsAsString(List<OptionMapping> args) {
        List<String> entries = new ArrayList<>();
        for (OptionMapping arg : args) {
        String name = arg.getName();
            switch (arg.getType()) {
                case UNKNOWN, NUMBER, STRING, SUB_COMMAND, SUB_COMMAND_GROUP -> entries.add(name+":"+arg.getAsString());
                case INTEGER -> entries.add(name+":"+arg.getAsInt());
                case BOOLEAN -> entries.add(name+":"+(arg.getAsBoolean()?"true":"false"));
                case USER -> entries.add(name+":@"+arg.getAsUser().getName() + " ("+arg.getAsUser().getId()+")");
                case CHANNEL -> entries.add(name+":#"+arg.getAsChannel().getName() + " ("+arg.getAsChannel().getId()+")");
                case ROLE -> entries.add(name+":@"+arg.getAsRole().getName() + " ("+arg.getAsRole().getId()+")");
                case MENTIONABLE -> entries.add(name+":"+arg.getAsMentionable().getId());
                case ATTACHMENT -> entries.add(name+":"+arg.getAsAttachment().getUrl());
            }
        }
        return String.join(" ", entries);
    }

    @Override
    public String key() {
        return "Command";
    }

    @Override
    public Object data() {
        return commandLog;
    }
}
