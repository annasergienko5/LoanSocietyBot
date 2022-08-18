package com.example.GringottsTool.Enteties;

import com.example.GringottsTool.Constants;

import java.util.ArrayList;
import java.util.List;

public class Loan {
    private String dateStart;
    private String dateEnd;
    private int value;
    private int loanId;
    private List<Transaction> transactions;

    public Loan() {
        this.transactions = new ArrayList<>();
    }


    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
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