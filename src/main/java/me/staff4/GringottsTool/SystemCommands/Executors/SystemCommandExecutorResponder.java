package me.staff4.GringottsTool.SystemCommands.Executors;

import me.staff4.GringottsTool.DTO.IncomingMessage;

public interface SystemCommandExecutorResponder {
    void put(IncomingMessage message);
}
