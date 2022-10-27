package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.AdminMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class NewLoan extends AbsGetCommand implements AdminMessageCommandExecutor {
    private final int numberOfNewloanCommandParameters = 3;
    @Override
    public MessageCommand command() {
        return MessageCommand.NEW_LOAN;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws InvalidDataException, NoDataFound, IOException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        String[] inputTextWithout = incomingMessage.getText().split("@", 2);
        String[] inputTextArray = inputTextWithout[0].split(" ", 2);
        String chatId = incomingMessage.getChatId();
        if (inputTextArray.length < numberOfNewloanCommandParameters) {
            responder.put(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, Constants.NOT_MONEY));
            return;
        }
        int sum = Integer.parseInt(inputTextArray[2]);
        responder.put(new OutgoingMessage(OutgoingMessageType.TEXT, chatId,
                getRepository().addQueueItem(inputTextArray[1], sum)));
    }
}
