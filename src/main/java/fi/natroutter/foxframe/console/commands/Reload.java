package fi.natroutter.foxframe.console.commands;

import fi.natroutter.foxframe.console.ConsoleCommand;
import fi.natroutter.foxframe.console.ConsoleData;
import fi.natroutter.foxframe.bot.BotHandler;

public class Reload extends ConsoleCommand {


    public Reload() {
        super("reload","Reloads all bot commands", "reload");
    }

    @Override
    public ConsoleData execute(BotHandler handler, ConsoleData data, String[] args) {
        if (handler.isConnected()) {
            println("Reloading...");
            handler.reloadCommands();
            println("Reload Done.");
        } else {
            println("Bot is not connected!");
        }
        return data;
    }
}
