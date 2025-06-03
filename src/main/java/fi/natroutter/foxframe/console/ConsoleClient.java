package fi.natroutter.foxframe.console;

import fi.natroutter.foxframe.FoxFrame;
import fi.natroutter.foxframe.console.commands.*;
import fi.natroutter.foxframe.bot.BotHandler;
import fi.natroutter.foxlib.FoxLib;
import fi.natroutter.foxlib.logger.FoxLogger;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

import java.util.*;

@Slf4j
public class ConsoleClient {

    private Scanner scanner = new Scanner(System.in);
    private BotHandler handler;
    private FoxLogger logger = FoxFrame.getLogger();

    @Getter @Setter
    private boolean useDefaultHelpCommand = true;

    @Getter @Setter
    private boolean useDefaultCommands = true;

    private ConsoleData data = new ConsoleData();

    private Map<String, ConsoleCommand> commands = new HashMap<>();

    public ConsoleClient(BotHandler handler) {
        this.handler = handler;

        if (useDefaultCommands) {
            register(
                    new Channels(),
                    new Guilds(),
                    new Quit(),
                    new Reload(),
                    new Say(),
                    new Select()
            );
        }


        logger.info("Console client is running!");
        if (useDefaultHelpCommand) {
            logger.info("Get started by typing \"help\"");
        }

        loop();
    }

    public void register(ConsoleCommand... commands) {
        for(ConsoleCommand c : commands) {
            this.commands.put(c.getName(), c);
        }
    }

    public void loop() {
        if (commands.isEmpty()) {
            logger.warn("There is no commands registered for ConsoleClient. Exiting ConsoleClient");
            return;
        }
        print("> ");
        String input = scanner.nextLine();

        if (handler.isConnected()) {
            data.setGuilds(handler.getJDA().getGuilds());
            if (!data.getGuilds().isEmpty() && data.getSelectedGuild() != null) {
                Guild g = handler.getJDA().getGuildById(data.getSelectedGuild().getIdLong());
                if (g != null) {
                    data.setChannels(g.getTextChannels());
                }
            }
        }

        String[] args = input.split(" ");
        String command = args[0];
        if (args.length > 1) {
            args = Arrays.copyOfRange(args, 1, args.length);
        } else {
            args = new String[0];
        }

        if (useDefaultHelpCommand && command.equalsIgnoreCase("help")) {
            println("======= "+handler.getBotName()+" Console Client =======");
            println("Version: " + handler.getVersion());
            println("Author: " + handler.getAuthor());
            println(" ");
            println("Selected:");
            println("  Guild: " + (data.getSelectedGuild() != null ? data.getSelectedGuild().getName() + " (" + data.getSelectedGuild().getId() + ")" : "None"));
            println("  Channel: " + (data.getSelectedChannel() != null ? data.getSelectedChannel().getName() + " (" + data.getSelectedChannel().getId() + ")" : "None"));
            println(" ");
            println("Commands:");
            println("  help - Shows this message");
            for(ConsoleCommand c : commands.values()) {
                println("  "+c.getUsage()+" - " + c.getDescription());
            }
            loop();
        }

        if (!commands.containsKey(command.toLowerCase())) {
            println("Unknown command: " + input);
            loop();
        }

        ConsoleCommand cmd = commands.get(command.toLowerCase());
        ConsoleData newData = cmd.execute(handler, data, args);
        if (newData != null) {
            data=newData;
        }

        loop();
    }


    private void print(String msg) {
        FoxLib.print(msg);
    }
    private void println(String msg) {
        FoxLib.println(msg);
    }

}
