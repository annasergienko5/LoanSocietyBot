package com.example.GringottsTool.Enteties;

import com.example.GringottsTool.Constants;
import lombok.Getter;

@Getter
public class Info {
    int capital;
    int maxLoan;
    int borrowedMoney;
    int overdue;
    int reserve;
    int active;

    public Info(int capital, int borrowedMoney, int overdue, int reserve, int active) {
        this.capital = capital;
        this.borrowedMoney = borrowedMoney;
        this.overdue = overdue;
        this.reserve = reserve;
        this.active = active;
        this.maxLoan = (int) (capital * Constants.MAXIMUM_LOAN_COEFFICIENT);
    }

    @Override
    public String toString() {
        String result = String.format("\n`%-12s%,12d₽\n%-12s%,12d₽\n%-12s%,12d₽\n%-12s%,12d₽\n%-12s%,12d₽\n%-12s%,12d₽`",
                "Капитал", this.capital,
                "Макс. займ", this.maxLoan,
                "Занято", this.borrowedMoney,
                "Просрочено", this.overdue,
                "Запас", this.reserve,
                "Актив", this.active);
        return result;
    }
}
