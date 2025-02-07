package fi.natroutter.foxframe.console.commands;

import fi.natroutter.foxframe.console.ConsoleCommand;
import fi.natroutter.foxframe.console.ConsoleData;
import fi.natroutter.foxframe.interfaces.HandlerFrame;

public class Select extends ConsoleCommand {


    public Select() {
        super("select","Changes selected channel or guild", "select <guild/channel> <num>");
    }

    @Override
    public ConsoleData execute(HandlerFrame handler, ConsoleData data, String[] args) {
        if (args.length < 2) {
            println(this.getUsage());
            return null;
        }
        String type = args[0];
        int num = Integer.parseInt(args[1]);

        if (type.equalsIgnoreCase("guild")) {
            if (!data.getGuilds().isEmpty()) {
                if (num < data.getGuilds().size()) {
                    data.setSelectedGuild(data.getGuilds().get(num));
                    println("Selected guild: " + data.getSelectedGuild().getName() + " (" + data.getSelectedGuild().getId() + ")");
                } else {
                    println("Invalid guild number!");
                }
            } else {
                println("No guilds found!");
            }
        } else if (type.equalsIgnoreCase("channel")) {
            if (data.getSelectedGuild() == null) {
                println("No guild selected!");
                return null;
            }
            if (!data.getChannels().isEmpty()) {
                if (num < data.getChannels().size()) {
                    data.setSelectedChannel(data.getChannels().get(num));
                    println("Selected channel: " + data.getSelectedChannel().getName() + " (" + data.getSelectedChannel().getId() + ")");
                } else {
                    println("Invalid channel number!");
                }
            } else {
                println("No channels found!");
            }
        } else {
            println("Unknown type: " + type);
        }
        return data;
    }
}
