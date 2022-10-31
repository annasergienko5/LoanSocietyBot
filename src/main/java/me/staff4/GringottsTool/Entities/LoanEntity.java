package me.staff4.GringottsTool.Entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public final class LoanEntity {
    private String dateStart;
    private String dateEnd;
    private float value;
    private int loanId;
    private List<Transaction> transactions;

    public LoanEntity() {
        this.transactions = new ArrayList<>();
    }

    public void appendTransaction(final Transaction transaction) {
        this.transactions.add(transaction);
    }
}
