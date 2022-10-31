package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.Entities.Partner;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.AdminMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.io.IOException;
import java.util.List;

@Component
public final class DuckList extends AbsGetCommand
        implements AdminMessageCommandExecutor, PrivateMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.DUCKLIST;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws InvalidDataException, NoDataFound, IOException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        List<Partner> elitePartners = getRepository().getDuckList();
        StringBuffer result = new StringBuffer();
        if (elitePartners.size() == 0) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        result.append("Уважаемые люди, которые делали взносы за последние 3 месяца:\n");
        for (Partner partner : elitePartners) {
            result.append("<strong>" + partner.getName() + "</strong>\n");
        }
        OutgoingMessage sendMessage = getOutMessage(incomingMessage);
        sendMessage.setText(result.toString());
        sendMessage.setParseMode(ParseMode.HTML);
        responder.put(sendMessage);
    }
}
