package fi.natroutter.foxframe.console.commands;

import fi.natroutter.foxframe.console.ConsoleCommand;
import fi.natroutter.foxframe.console.ConsoleData;
import fi.natroutter.foxframe.bot.BotHandler;

public class Channels extends ConsoleCommand {


    public Channels() {
        super("channels","Shows list of channels", "channels");
    }

    @Override
    public ConsoleData execute(BotHandler handler, ConsoleData data, String[] args) {
        if (data.getSelectedGuild() == null) {
            println("No guild selected!");
            return null;
        }
        if (!data.getChannels().isEmpty()) {
            println("Channels:");
            for (int i = 0; i < data.getChannels().size(); i++) {
                println("  " + i + ": " + data.getChannels().get(i).getName());
            }
        } else {
            println("No channels found!");
        }
        return data;
    }
}
