package com.example.GringottsTool.Enteties;


import com.example.GringottsTool.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class Partner {
    private int tableId;
    private String name;
    private String tgId;
    private String vk;
    private String city;
    private int maxMyLoan;
    private int contributions;
    private double sumContributions;
    private int loan;
    private int debt;
    private String returnDate;
    private int amountOfEarlyRepayment;
    private int amountOfOverdueRepayment;
    private boolean elite;
    private boolean payedInThisMonth;

    public Partner(final String names) {
        this.name = names;
    }

    public Partner(final String names, final int debts,
                   final String returnDates) {
        this.name = names;
        this.debt = debts;
        this.returnDate = returnDates;
    }
    public String getDebt() {
        String result = String.format("%,d", debt).replace(",", " ");
        return result;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.name);
        sb.append("\nВсего взносов: " + this.contributions);
        sb.append("\nНа сумму: " + this.sumContributions);
        sb.append("\nМакс. займ: " + maxMyLoan);
        sb.append("\nx0.6: " + (int) Math.floor(this.sumContributions * Constants.MINIMUM_LOAN_COEFFICIENT));
        sb.append("\nВсего займов: " + this.loan);
        if (this.returnDate != null && !this.returnDate.equals("")) {
            sb.append("\nСейчас должен: " + this.debt);
            sb.append("\nДата возврата: " + this.returnDate);
        }
        if (this.amountOfEarlyRepayment != 0) {
            sb.append("\nДосрочных погашений: " + this.amountOfEarlyRepayment);
        } else {
            sb.append("\nДосрочных погашений: нет");
        }
        if (this.amountOfOverdueRepayment != 0) {
            sb.append("\nПросрочек: " + this.amountOfOverdueRepayment);
        } else {
            sb.append("\nПросрочек: нет");
        }
        if (this.elite) {
            sb.append("\nВзносы за прошедшие 3 месяца: уплачено");
        } else {
            sb.append("\nВзносы за прошедшие 3 месяца: не уплачено");
        }
        if (this.payedInThisMonth) {
            sb.append("\nВзносы за текущий месяц: уплачено");
        } else {
            sb.append("\nВзносы за текущий месяц: не уплачено");
        }
        return sb.toString();
    }
}
