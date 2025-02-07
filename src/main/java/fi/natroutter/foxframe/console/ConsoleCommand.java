package fi.natroutter.foxframe.console;

import fi.natroutter.foxframe.interfaces.HandlerFrame;
import fi.natroutter.foxlib.FoxLib;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class ConsoleCommand {

    private String name;
    private String description;
    private String usage;

    public abstract ConsoleData execute(HandlerFrame handler, ConsoleData data, String[] args);

    public void print(String msg) {
        FoxLib.print(msg);
    }
    public void println(String msg) {
        FoxLib.println(msg);
    }


}
