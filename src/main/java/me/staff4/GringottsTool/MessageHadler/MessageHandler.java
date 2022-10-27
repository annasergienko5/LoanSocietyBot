package me.staff4.GringottsTool.MessageHadler;

import me.staff4.GringottsTool.DTO.IncomingMessage;

public interface MessageHandler {
    void handleIncomingMessage(IncomingMessage incomingMessage);
}
