package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.Entities.Contributions;
import me.staff4.GringottsTool.Entities.Partner;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class AboutMyPayment extends AbsGetCommand implements PrivateMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.ABOUTMYPAYMENTS;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws NoDataFound, IOException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        Partner partner = getRepository().getPartnerByTgId(String.valueOf(incomingMessage.getUserTgId()));
        Contributions contributions = getRepository().getContributions().get(partner.getTableId() - 2);
        if (contributions != null) {
            OutgoingMessage outgoingMessage = getOutMessage(incomingMessage);
            outgoingMessage.setText(contributions.toString());
            responder.put(outgoingMessage);
            return;
        }
        throw new NoDataFound(Constants.NOT_FOUND_DATA);
    }
}
