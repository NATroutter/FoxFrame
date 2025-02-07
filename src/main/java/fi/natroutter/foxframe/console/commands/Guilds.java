package fi.natroutter.foxframe.console.commands;

import fi.natroutter.foxframe.console.ConsoleCommand;
import fi.natroutter.foxframe.console.ConsoleData;
import fi.natroutter.foxframe.interfaces.HandlerFrame;

public class Guilds extends ConsoleCommand {


    public Guilds() {
        super("guilds","Shows list of guilds", "guilds");
    }

    @Override
    public ConsoleData execute(HandlerFrame handler, ConsoleData data, String[] args) {
        if (!data.getGuilds().isEmpty()) {
            println("Guilds:");
            for (int i = 0; i < data.getGuilds().size(); i++) {
                println("  " + i + ": " + data.getGuilds().get(i).getName());
            }
        } else {
            println("No guilds found!");
        }
        return data;
    }
}
