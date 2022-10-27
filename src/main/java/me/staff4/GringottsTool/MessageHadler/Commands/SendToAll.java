package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.AdminMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SendToAll extends AbsGetCommand implements AdminMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return null;
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
                    Constants.NO_TEXT));
            return;
        }
        List<String> allPartners = getRepository().getAllPartners();
        for (String tgId : allPartners) {
            if (tgId.equals(incomingMessage.getChatId()) || tgId.equals("")) {
                continue;
            }
            responder.put(new OutgoingMessage(OutgoingMessageType.TEXT, tgId, inputText[1]));
        }
    }
}
