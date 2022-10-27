package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.Enteties.Partner;
import me.staff4.GringottsTool.Enteties.Transaction;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.io.IOException;
import java.util.List;

@Component
public final class CreditHistory extends AbsGetCommand implements PrivateMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.CREDITHISTORY;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws NoDataFound, IOException, InvalidDataException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        String chatId = incomingMessage.getChatId();
        long tgId = incomingMessage.getUserTgId();
        boolean full = incomingMessage.getText().equals("/credithistoryfull");
        getLog().info("\n getCreditHistory STARTED from chatId: " + chatId + "\n From user with tgId: " + tgId
                + "\n Is full CreditHistory?: " + full);
        Partner partner = getRepository().getPartnerByTgId(String.valueOf(tgId));
        List<Transaction> transactions;
        if (partner.getTableId() == 0) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        } else {
            transactions = getRepository().getTransactions(partner);
        }
        if (transactions.size() == 0) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        me.staff4.GringottsTool.Enteties.CreditHistory creditHistory = new me.staff4.GringottsTool
                .Enteties.CreditHistory(transactions);
        String creditString;
        if (full) {
            creditString = creditHistory.fullString(true);
        } else {
            creditString = creditHistory.partialString(true);
        }
        String message = String.format(Constants.ABOUT_CREDIT_HISTORY_MESSAGE, partner.getName(), creditString);
        OutgoingMessage sendMessage = getOutMessage(incomingMessage);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        responder.put(sendMessage);
    }
}
