package me.staff4.GringottsTool.SystemCommands.Executors;

import me.staff4.GringottsTool.SystemCommands.Command;

public interface SystemCommandExecutorCommand extends SystemCommandExecutor {
    Command command();
}
