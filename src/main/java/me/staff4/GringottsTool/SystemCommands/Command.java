package me.staff4.GringottsTool.SystemCommands;

public enum Command {
    DEBTORS, TODAY_DEBTORS;

    public static boolean is(final String c) {
        for (var command : Command.values()) {
            if (c.equals(command.name())) {
                return true;
            }
        }

        return false;
    }
}
