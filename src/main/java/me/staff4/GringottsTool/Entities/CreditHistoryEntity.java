package me.staff4.GringottsTool.Entities;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public final class CreditHistoryEntity {
    @Getter
    private ArrayList<LoanEntity> loans;

    public CreditHistoryEntity(final List<Transaction> transactions) {
        this.loans = new ArrayList<>();
        float previousBalance = 0;
        int loanId = 1;
        LoanEntity loanEntity = new LoanEntity();
        for (Transaction transaction : transactions) {
            float value = transaction.getValue();
            float currentBalance = value + previousBalance;
            if ((currentBalance) < 0) {
                loanEntity.setLoanId(loanId);
                if (value < 0) {
                    loanEntity.setValue(currentBalance);
                }
                if (loanEntity.getDateStart() == null) {
                    loanEntity.setDateStart(transaction.getDate());
                }
                loanEntity.appendTransaction(transaction);
            } else if (previousBalance <= 0) {
                loanEntity.appendTransaction(transaction);
                loanEntity.setDateEnd(transaction.getDate());
                this.loans.add(loanEntity);
                loanEntity = new LoanEntity();
                loanId++;
            }
            previousBalance = currentBalance;
        }
        if (previousBalance < 0) {
            this.loans.add(loanEntity);
        }
    }
}
