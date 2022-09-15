package me.staff4.GringottsTool.Enteties;

import java.util.ArrayList;
import java.util.List;

public final class CreditHistory {
    private ArrayList<Loan> loans;

    public CreditHistory(final List<Transaction> transactions) {
        this.loans = new ArrayList<>();
        int previousBalance = 0;
        int loanId = 1;
        Loan loan = new Loan();
        for (Transaction transaction : transactions) {
            int value = transaction.getValue();
            int currentBalance = value + previousBalance;
            if ((currentBalance) < 0) {
                loan.setLoanId(loanId);
                if (value < 0) {
                    loan.setValue(currentBalance);
                }
                if (loan.getDateStart() == null) {
                    loan.setDateStart(transaction.getDate());
                }
                loan.appendTransaction(transaction);
            } else if (previousBalance <= 0) {
                loan.appendTransaction(transaction);
                loan.setDateEnd(transaction.getDate());
                this.loans.add(loan);
                loan = new Loan();
                loanId++;
            }
            previousBalance = currentBalance;
        }
        if (previousBalance < 0) {
            this.loans.add(loan);
        }
    }

    public String fullString(final boolean isHtmlParseModeOn) {
        StringBuilder loansString = new StringBuilder();
        for (Loan loan : loans) {
            String text = loan.getString(isHtmlParseModeOn, true);
            loansString.append(text);
        }
        return loansString.toString();
    }

    public String partialString(final boolean isHtmlParseModeOn) {
        StringBuilder loansString = new StringBuilder();
        for (Loan loan : loans) {
            String text = loan.getString(isHtmlParseModeOn, false);
            loansString.append(text);
        }
        return loansString.toString();
    }
}
