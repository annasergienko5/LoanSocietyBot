package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.SystemMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import me.staff4.GringottsTool.Templates.TemplateEngine;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public final class Poll extends AbsGetCommand implements SystemMessageCommandExecutor {
    private final int numberOfSupergroupIdentifierSymbols = 4;
    @Override
    public MessageCommand command() {
        return MessageCommand.POLL;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws NoDataFound, IOException {
        List<String> allPartners = getRepository().getAllPartners();
        String publicChat;
        if (Constants.PUBLIC_CHAT_ID.startsWith("-100")) {
            publicChat = Constants.PUBLIC_CHAT_ID.substring(numberOfSupergroupIdentifierSymbols);
        } else {
            publicChat = Constants.PUBLIC_CHAT_ID.substring(1);
        }
        String notificationText = TemplateEngine.pollNotification(publicChat,
                String.valueOf(incomingMessage.getMessageId()));
        for (String tgId : allPartners) {
            if (tgId.equals(String.valueOf(incomingMessage.getUserTgId())) || tgId.equals("")) {
                continue;
            }
            responder.put(new OutgoingMessage(OutgoingMessageType.TEXT, tgId, notificationText));
        }
    }
}
