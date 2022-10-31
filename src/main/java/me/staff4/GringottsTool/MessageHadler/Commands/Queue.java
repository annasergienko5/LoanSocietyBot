package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.Entities.QueueItem;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.AdminMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Queue extends AbsGetCommand implements AdminMessageCommandExecutor, PrivateMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.QUEUE;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws InvalidDataException, NoDataFound, IOException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        StringBuffer result = new StringBuffer();
        java.util.Queue<QueueItem> queue = getRepository().getQueue();
        int count = 1;
        for (QueueItem q : queue) {
            String str = String.format("%d. %s - %d\n", count++, q.getName(), q.getSum());
            result.append(str);
        }
        OutgoingMessage message = getOutMessage(incomingMessage);
        message.setText(result.toString());
        responder.put(message);
    }
}
