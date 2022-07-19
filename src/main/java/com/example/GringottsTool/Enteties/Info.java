package com.example.GringottsTool.Enteties;

public class Info {
    String capital;
    String borrowedMoney;
    String overdue;
    String reserve;
    String active;

    public Info(String capital, String borrowedMoney, String overdue, String reserve, String active) {
        this.capital = capital;
        this.borrowedMoney = borrowedMoney;
        this.overdue = overdue;
        this.reserve = reserve;
        this.active = active;
    }
    @Override
    public String toString() {
        String result = String.format("\n`%-12s%12s\n%-12s%12s\n%-12s%12s\n%-12s%12s\n%-12s%12s`",
                "Капитал", this.capital,
                "Занято", this.borrowedMoney,
                "Просрочено", this.overdue,
                "Запас", this.reserve,
                "Актив", this.active);
        return result;
    }
}
