package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;

@Component
public final class Start extends AbsGetCommand implements PrivateMessageCommandExecutor {

    @Override
    public MessageCommand command() {
        return MessageCommand.START;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage) {
        OutgoingMessage outgoingMessage = getOutMessage(incomingMessage);
        outgoingMessage.setText("Привет");
        outgoingMessage.setEnableMarkdown(true);
        responder.put(outgoingMessage);
    }
}
