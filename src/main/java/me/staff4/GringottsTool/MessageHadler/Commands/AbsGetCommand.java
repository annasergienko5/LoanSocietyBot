package me.staff4.GringottsTool.MessageHadler.Commands;

import lombok.Getter;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.HelpFileReader;
import me.staff4.GringottsTool.Repository.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
public abstract class AbsGetCommand {
    private final Logger log = LogManager.getLogger();
    @Autowired
    private Repository repository;
    @Autowired
    private HelpFileReader helpFileReader;

    final OutgoingMessage getOutMessage(final IncomingMessage incomingMessage) {
        return OutgoingMessage.builder()
                .chatId(incomingMessage.getChatId())
                .replyToMessageId(incomingMessage.getMessageId())
                .type(OutgoingMessageType.TEXT)
                .build();
    }
}
