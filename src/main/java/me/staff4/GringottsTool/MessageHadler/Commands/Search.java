package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.Enteties.Partner;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.AdminMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
@Component
public final class Search extends AbsGetCommand implements PrivateMessageCommandExecutor,
        AdminMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.SEARCH;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws NoDataFound, IOException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        String[] inputTextWithout = incomingMessage.getText().split("@", 2);
        String[] inputText = inputTextWithout[0].split(" ", 2);
        if (inputText.length < 2) {
            responder.put(new OutgoingMessage(OutgoingMessageType.ERROR, incomingMessage.getChatId(),
                    Constants.NOT_PARAMETERS));
            return;
        }
        List<Partner> resultList = getRepository().getPartners(inputText[1]);
        OutgoingMessage sendMessage;
        if (resultList.size() == 0) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        if (resultList.size() > 1) {
            sendMessage = new OutgoingMessage(OutgoingMessageType.ERROR, incomingMessage.getChatId(),
                    Constants.FIND_MORE_RESULT);
            responder.put(sendMessage);
        }
        sendMessage = new OutgoingMessage(OutgoingMessageType.TEXT, incomingMessage.getChatId(),
                resultList.get(0).toString());
        responder.put(sendMessage);
    }
}
