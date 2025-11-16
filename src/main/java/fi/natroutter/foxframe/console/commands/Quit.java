package fi.natroutter.foxframe.console.commands;

import fi.natroutter.foxframe.console.ConsoleCommand;
import fi.natroutter.foxframe.console.ConsoleData;
import fi.natroutter.foxframe.bot.DiscordBot;

public class Quit extends ConsoleCommand {


    public Quit() {
        super("quit","Disconnects the bot and shutdowns the application", "quit");
    }

    @Override
    public ConsoleData execute(DiscordBot discordBot, ConsoleData data, String[] args) {
        if (discordBot.isRunning()) {
            discordBot.disconnect();
            println("Bot has been stopped!");
        } else {
            println("Bot is not connected!");
        }
        System.exit(0);
        return data;
    }
}
