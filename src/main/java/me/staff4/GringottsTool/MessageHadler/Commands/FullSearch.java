package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.Converters.ConverterTxt;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.Enteties.CreditHistory;
import me.staff4.GringottsTool.Enteties.Partner;
import me.staff4.GringottsTool.Enteties.Transaction;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.AdminMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public final class FullSearch extends AbsGetCommand implements AdminMessageCommandExecutor,
        PrivateMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.FULLSEARCH;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws NoDataFound, IOException, InvalidDataException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        String[] inputTextWithout = incomingMessage.getText().split("@", 2);
        String[] inputText = inputTextWithout[0].split(" ", 2);
        String chatId = incomingMessage.getChatId();
        long tgId = incomingMessage.getUserTgId();
        if (inputText.length < 2) {
            responder.put(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, Constants.NOT_PARAMETERS_FULLSEARCH));
            return;
        }
        String lookingForNameOrTgId = inputText[1];
        getLog().info("\n getFullSearch STARTED from chatId: " + chatId + "\n From user with tgId: " + tgId
                + "\n Looking for person: " + lookingForNameOrTgId);
        List<Partner> partners = getRepository().getPartners(lookingForNameOrTgId);
        if (partners.size() == 0) {
            responder.put(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, Constants.NO_PERSON_FOUND));
        } else if (partners.size() > 1) {
            responder.put(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, Constants.FIND_MORE_RESULT));
        } else {
            responder.put(getPersonHistoryMessage(partners.get(0), chatId));
        }
    }

    private OutgoingMessage getPersonHistoryMessage(final Partner partner, final String chatId)
            throws InvalidDataException, IOException {
        String aboutTransactions;
        String txtFilePath = null;
        OutgoingMessage outgoingMessage;
        List<Transaction> transactions = getRepository().getTransactions(partner);
        String fileName = null;
        if (transactions.size() == 0) {
            aboutTransactions = Constants.NO_TRANSACTIONS_FOUND;
        } else {
            me.staff4.GringottsTool.Enteties.CreditHistory creditHistory = new CreditHistory(transactions);
            String textWithCreditHistory = String.format(Constants.ABOUT_CREDIT_HISTORY_MESSAGE_PARSEMODE_OFF,
                    partner.getName(), creditHistory.fullString(false));
            txtFilePath = new ConverterTxt().saveToTxtFile(textWithCreditHistory);
            aboutTransactions = Constants.TRANSACTIONS_BY_FILE;
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate nowDate = LocalDate.now(ZoneId.of(Constants.CRON_TIMEZONE));
            nowDate.format(dateTimeFormatter);
            fileName = String.format(Constants.FULL_SEARCH_FILENAME_ABOUT_FULLCREDIT,
                    partner.getName(), nowDate);
        }
        outgoingMessage = new OutgoingMessage(OutgoingMessageType.TEXT, chatId,
                String.format(Constants.FULL_SEARCH_TEMPLATE, partner, aboutTransactions));
        outgoingMessage.setParseMode(ParseMode.HTML);
        outgoingMessage.setDocumentFilePath(txtFilePath);
        outgoingMessage.setDocumentFileName(fileName);
        return outgoingMessage;
    }
}
