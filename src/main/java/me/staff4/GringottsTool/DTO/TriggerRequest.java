package me.staff4.GringottsTool.DTO;

import lombok.Setter;
import lombok.ToString;
import me.staff4.GringottsTool.SystemCommands.Command;

@ToString
@Setter
public class TriggerRequest {
    private String command;

    /*
     * throws IllegalArgumentException if not checked for validity
     * */
    public final Command command() throws IllegalArgumentException {
        return Command.valueOf(command);
    }

    public final boolean isValid() {
        if (command == null) {
            return false;
        }

        return Command.is(command);
    }
}
