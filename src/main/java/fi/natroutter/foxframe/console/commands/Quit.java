package fi.natroutter.foxframe.console.commands;

import fi.natroutter.foxframe.console.ConsoleCommand;
import fi.natroutter.foxframe.console.ConsoleData;
import fi.natroutter.foxframe.interfaces.HandlerFrame;

public class Quit extends ConsoleCommand {


    public Quit() {
        super("quit","Disconnects the bot and shutdowns the application", "quit");
    }

    @Override
    public ConsoleData execute(HandlerFrame handler, ConsoleData data, String[] args) {
        if (handler.isConnected()) {
            handler.getJDA().shutdown();
            println("Bot has been stopped!");
        }
        System.exit(0);
        return data;
    }
}
