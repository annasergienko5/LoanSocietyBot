package me.staff4.GringottsTool.Enteties;

import me.staff4.GringottsTool.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public final class Loan {
    private String dateStart;
    private String dateEnd;
    private float value;
    private int loanId;
    private List<Transaction> transactions;

    public Loan() {
        this.transactions = new ArrayList<>();
    }
    public String getString(final boolean isHtmlParseModeOn, final boolean addTransactions) {
        if (dateEnd == null) {
            dateEnd = "Займ не закрыт";
        }
        String textTemplate;
        if (addTransactions) {
            textTemplate = getStringWithTransactions(isHtmlParseModeOn);
        } else {
            textTemplate = getStringWithoutTransactions(isHtmlParseModeOn);
        }
        return textTemplate;
    }
    private String getStringWithTransactions(final boolean isHtmlParseModeOn) {
        String textTemplate;
        StringBuilder transactionsString = new StringBuilder();
        for (Transaction transaction : transactions) {
            String text = transaction.getString(isHtmlParseModeOn);
            transactionsString.append(text);
        }
        if (isHtmlParseModeOn) {
            textTemplate = Constants.LOAN_WITH_TRANSACTIONS;
        } else {
            textTemplate = Constants.LOAN_WITH_TRANSACTIONS_PARSEMODE_OFF;
        }
        return String.format(textTemplate, loanId, dateStart, dateEnd, value,
                transactionsString).replace(',', ' ');
    }
    private String getStringWithoutTransactions(final boolean isHtmlParseModeOn) {
        String textTemplate;
        if (isHtmlParseModeOn) {
            textTemplate = Constants.LOAN_WITHOUT_TRANSACTIONS;
        } else {
            textTemplate = Constants.LOAN_WITHOUT_TRANSACTIONS_PARSEMODE_OFF;
        }
        return String.format(textTemplate,
                loanId, dateStart,
                dateEnd, value).replace(',', ' ');
    }
    public void appendTransaction(final Transaction transaction) {
        this.transactions.add(transaction);
    }
}
