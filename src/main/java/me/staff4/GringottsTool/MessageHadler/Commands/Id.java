package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.*;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;
@Component
public final class Id extends AbsGetCommand implements AllAvailableMessageCommandExecutor,
        PublicMessageCommandExecutor, AdminMessageCommandExecutor, PrivateMessageCommandExecutor {

    @Override
    public MessageCommand command() {
        return MessageCommand.ID;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage) {
        OutgoingMessage outgoingMessage = getOutMessage(incomingMessage);
        outgoingMessage.setText(outgoingMessage.getChatId());
        responder.put(outgoingMessage);
    }
}
