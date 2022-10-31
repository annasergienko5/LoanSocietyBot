package me.staff4.GringottsTool.Entities;


import me.staff4.GringottsTool.Constants;
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
        return String.format("%,d", debt).replace(",", " ");
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.name);
        sb.append("\nВсего взносов: ").append(this.contributions);
        sb.append("\nНа сумму: ").append(this.sumContributions);
        sb.append("\nМакс. займ: ").append(maxMyLoan);
        sb.append("\nx0.6: ").append((int) Math.floor(this.sumContributions * Constants.MINIMUM_LOAN_COEFFICIENT));
        sb.append("\nВсего займов: ").append(this.loan);
        if (this.returnDate != null && !this.returnDate.equals("")) {
            sb.append("\nСейчас должен: ").append(this.debt);
            sb.append("\nДата возврата: ").append(this.returnDate);
        }
        if (this.amountOfEarlyRepayment != 0) {
            sb.append("\nДосрочных погашений: ").append(this.amountOfEarlyRepayment);
        } else {
            sb.append("\nДосрочных погашений: нет");
        }
        if (this.amountOfOverdueRepayment != 0) {
            sb.append("\nПросрочек: ").append(this.amountOfOverdueRepayment);
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
