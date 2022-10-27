package me.staff4.GringottsTool.MessageHadler;

import me.staff4.GringottsTool.DTO.IncomingMessage;

public interface IncomingMessageHandlerManager {
    void handle(IncomingMessage incomingMessage);
}
