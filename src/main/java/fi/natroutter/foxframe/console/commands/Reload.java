package fi.natroutter.foxframe.console.commands;

import fi.natroutter.foxframe.console.ConsoleCommand;
import fi.natroutter.foxframe.console.ConsoleData;
import fi.natroutter.foxframe.bot.DiscordBot;

public class Reload extends ConsoleCommand {


    public Reload() {
        super("reload","Reloads all bot commands", "reload");
    }

    @Override
    public ConsoleData execute(DiscordBot discordBot, ConsoleData data, String[] args) {
        if (discordBot.isRunning()) {
            println("Reloading...");
            discordBot.reloadCommands();
            println("Reload Done.");
        } else {
            println("Bot is not connected!");
        }
        return data;
    }
}
