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
        StringBuffer result = new StringBuffer();
        result.append("\nКапитал " + this.capital);
        result.append("\nЗанято " + this.borrowedMoney);
        result.append("\nПросрочено " + this.overdue);
        result.append("\nЗапас " + this.reserve);
        result.append("\nАктив " + this.active);
        return result.toString();
    }
}
