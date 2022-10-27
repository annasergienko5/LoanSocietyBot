package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.AdminMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PublicMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
@Component
public class Status extends AbsGetCommand implements PublicMessageCommandExecutor,
        AdminMessageCommandExecutor, PrivateMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.STATUS;
    }


    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws InvalidDataException, NoDataFound, IOException, ParseException, GeneralSecurityException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        String result = getRepository().getInfo().toString();
        OutgoingMessage sendMessage = new OutgoingMessage(OutgoingMessageType.TEXT, incomingMessage.getChatId(),
                result);
        sendMessage.setEnableMarkdown(true);
        responder.put(sendMessage);
    }
}
