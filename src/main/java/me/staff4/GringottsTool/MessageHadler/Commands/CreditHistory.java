package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.Entities.CreditHistoryEntity;
import me.staff4.GringottsTool.Entities.LoanEntity;
import me.staff4.GringottsTool.Entities.Partner;
import me.staff4.GringottsTool.Entities.Transaction;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import me.staff4.GringottsTool.Templates.TemplateEngine;
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
        CreditHistoryEntity creditHistoryEntity = new CreditHistoryEntity(transactions);
        String creditString;
        if (full) {
            creditString = fullCreditHistoryString(creditHistoryEntity, true);
        } else {
            creditString = partialCreditHistoryString(creditHistoryEntity, true);
        }
        String message = TemplateEngine.aboutCreditHistoryMessage(partner.getName(), creditString);
        OutgoingMessage sendMessage = getOutMessage(incomingMessage);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        responder.put(sendMessage);
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

    private String partialCreditHistoryString(final CreditHistoryEntity creditHistoryEntity,
                                              final boolean isHtmlParseModeOn) {
        StringBuilder loansString = new StringBuilder();
        for (LoanEntity loan : creditHistoryEntity.getLoans()) {
            String text = getLoanString(loan, isHtmlParseModeOn, false);
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
