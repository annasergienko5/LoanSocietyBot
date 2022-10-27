package me.staff4.GringottsTool.MessageHadler;

import me.staff4.GringottsTool.DTO.IncomingMessage;

import java.util.Optional;

@FunctionalInterface
public interface SelectCommand {
    Optional<MessageCommand> getCommand(IncomingMessage incomingMessage);
}
