package me.staff4.GringottsTool.MessageHadler.Commands.Interfaces;

import me.staff4.GringottsTool.DTO.OutgoingMessage;

public interface MessageCommandExecutorResponder {
    void put(OutgoingMessage outgoingMessage);
}
