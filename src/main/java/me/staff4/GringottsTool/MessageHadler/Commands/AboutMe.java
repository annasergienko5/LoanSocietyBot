package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.Enteties.Partner;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.List;

@Component
public final class AboutMe extends AbsGetCommand implements PrivateMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.ABOUTME;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws InvalidDataException, NoDataFound, IOException, ParseException, GeneralSecurityException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        List<Partner> resultList = getRepository().getPartners(String.valueOf(incomingMessage.getUserTgId()));
        if (resultList.isEmpty()) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        } else {
            OutgoingMessage outgoingMessage = getOutMessage(incomingMessage);
            outgoingMessage.setText(resultList.get(0).toString());
            responder.put(outgoingMessage);
        }
    }
}
