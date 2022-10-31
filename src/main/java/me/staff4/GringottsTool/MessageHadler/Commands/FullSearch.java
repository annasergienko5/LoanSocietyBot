package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.Converters.ConverterTxt;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.Entities.CreditHistoryEntity;
import me.staff4.GringottsTool.Entities.LoanEntity;
import me.staff4.GringottsTool.Entities.Partner;
import me.staff4.GringottsTool.Entities.Transaction;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.AdminMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import me.staff4.GringottsTool.Templates.TemplateEngine;
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
            CreditHistoryEntity creditHistoryEntity = new CreditHistoryEntity(transactions);

            String textWithCreditHistory = TemplateEngine.aboutCreditHistoryMessageParsemodeOff(partner.getName(),
                    fullCreditHistoryString(creditHistoryEntity, false));
            txtFilePath = new ConverterTxt().saveToTxtFile(textWithCreditHistory);
            aboutTransactions = Constants.TRANSACTIONS_BY_FILE;
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate nowDate = LocalDate.now(ZoneId.of(Constants.CRON_TIMEZONE));
            nowDate.format(dateTimeFormatter);
            fileName = TemplateEngine.fullSearchFilenameAboutFullcredit(partner.getName(), nowDate.toString());
        }
        outgoingMessage = new OutgoingMessage(OutgoingMessageType.TEXT, chatId,
                TemplateEngine.fullSearch(partner.toString(), aboutTransactions));
        outgoingMessage.setParseMode(ParseMode.HTML);
        outgoingMessage.setDocumentFilePath(txtFilePath);
        outgoingMessage.setDocumentFileName(fileName);
        return outgoingMessage;
    }

    private String fullCreditHistoryString(final CreditHistoryEntity creditHistoryEntity,
                                           final boolean isHtmlParseModeOn) {
        StringBuilder loansString = new StringBuilder();
        for (LoanEntity loan : creditHistoryEntity.getLoans()) {
            String text = getLoanString(loan, isHtmlParseModeOn, true);
            loansString.append(text);
        }
        return loansString.toString();
    }

    private String getLoanString(final LoanEntity loan, final boolean isHtmlParseModeOn,
                                 final boolean addTransactions) {
        if (loan.getDateEnd() == null) {
            loan.setDateEnd("Займ не закрыт");
        }
        String textTemplate;
        if (addTransactions) {
            textTemplate = getLoanStringWithTransactions(loan, isHtmlParseModeOn);
        } else {
            textTemplate = getLoanStringWithoutTransactions(loan, isHtmlParseModeOn);
        }
        return textTemplate;
    }

    private String getLoanStringWithTransactions(final LoanEntity loan, final boolean isHtmlParseModeOn) {
        String textTemplate;
        StringBuilder transactionsString = new StringBuilder();
        for (Transaction transaction : loan.getTransactions()) {
            String text = getTransactionString(transaction, isHtmlParseModeOn);
            transactionsString.append(text);
        }
        String dateStart = loan.getDateStart();
        String dateEnd = loan.getDateEnd();
        float value = loan.getValue();
        int loanId = loan.getLoanId();
        if (isHtmlParseModeOn) {
            textTemplate = TemplateEngine.loanWithTransactions(loanId, dateStart, dateEnd, value,
                    transactionsString.toString());
        } else {
            textTemplate = TemplateEngine.loanWithTransactionsParsemodeOff(loanId, dateStart, dateEnd, value,
                    transactionsString.toString());
        }
        return textTemplate;
    }

    private String getLoanStringWithoutTransactions(final LoanEntity loan, final boolean isHtmlParseModeOn) {
        String textTemplate;
        String dateStart = loan.getDateStart();
        String dateEnd = loan.getDateEnd();
        float value = loan.getValue();
        int loanId = loan.getLoanId();
        if (isHtmlParseModeOn) {
            textTemplate = TemplateEngine.loanWithoutTransactions(loanId, dateStart, dateEnd, value);
        } else {
            textTemplate = TemplateEngine.loanWithoutTransactionsParsemodeOff(loanId, dateStart, dateEnd, value);
        }
        return textTemplate;
    }

    private String getTransactionString(final Transaction transaction, final boolean isHtmlParseModeOn) {
        String template;
        String date = transaction.getDate();
        float value = transaction.getValue();
        if (isHtmlParseModeOn) {
            template = TemplateEngine.transaction(date, value);
        } else {
            template = TemplateEngine.transactionParsemodeOff(date, value);
        }
        return template;
    }
}
