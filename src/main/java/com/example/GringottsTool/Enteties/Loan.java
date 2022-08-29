package com.example.GringottsTool.Enteties;

import com.example.GringottsTool.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Loan {
    private String dateStart;
    private String dateEnd;
    private int value;
    private int loanId;
    private List<Transaction> transactions;

    public Loan() {
        this.transactions = new ArrayList<>();
    }
    public String fullString() {
        if (dateEnd == null) {
            dateEnd = "Займ не закрыт";
        }
        StringBuilder transactionsString = new StringBuilder();
        for (Transaction transaction : transactions) {
            String text = transaction.toString();
            transactionsString.append(text);
        }
        return String.format(Constants.LOAN_WITH_TRANSACTIONS, loanId, dateStart,
                dateEnd, value, transactionsString).replace(',', ' ');
    }

        public String partialString() {
        if (dateEnd == null) {
            dateEnd = "Займ не закрыт";
        }
        return String.format(Constants.LOAN_WITHOUT_TRANSACTIONS, loanId, dateStart,
                dateEnd, value).replace(',', ' ');
    }

    public void appendTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}