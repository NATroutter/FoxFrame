package fi.natroutter.foxframe.console.commands;

import fi.natroutter.foxframe.console.ConsoleCommand;
import fi.natroutter.foxframe.console.ConsoleData;
import fi.natroutter.foxframe.interfaces.HandlerFrame;

public class Say extends ConsoleCommand {


    public Say() {
        super("say","Sends message to currently selected channel in selected guild", "say <message>");
    }

    @Override
    public ConsoleData execute(HandlerFrame handler, ConsoleData data, String[] args) {
        if (args.length < 1) {
            println("Usage: say <message>");
            return null;
        }
        if (data.getSelectedGuild() == null) {
            println("No guild selected!");
            return null;
        }
        if (data.getSelectedChannel() == null) {
            println("No channel selected!");
            return null;
        }
        String msg = String.join(" ", args);
        data.getSelectedChannel().sendMessage(msg).queue();
        return data;
    }
}
