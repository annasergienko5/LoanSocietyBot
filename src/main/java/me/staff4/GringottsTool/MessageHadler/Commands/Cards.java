package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
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
import java.util.List;
@Component
public final class Cards extends AbsGetCommand implements PublicMessageCommandExecutor,
        AdminMessageCommandExecutor, PrivateMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.CARDS;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws InvalidDataException, NoDataFound, IOException, ParseException, GeneralSecurityException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        StringBuffer res = new StringBuffer();
        List<me.staff4.GringottsTool.Enteties.Cards> cards = getRepository().getCards();
        if (cards.isEmpty()) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        for (me.staff4.GringottsTool.Enteties.Cards card : cards) {
            res.append("\n").append(card.toString());
        }
        OutgoingMessage outgoingMessage = getOutMessage(incomingMessage);
        outgoingMessage.setText(res.toString());
        outgoingMessage.setEnableMarkdown(true);
        responder.put(outgoingMessage);
    }
}
