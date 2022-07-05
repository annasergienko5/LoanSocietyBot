package com.example.GringottsTool.Enteties;

public class Svodka {
    int capital;
    int borrowedMoney;
    int overdue;
    int reserve;
    int active;

    public Svodka(int capital, int borrowedMoney, int overdue, int reserve, int active) {
        this.capital = capital;
        this.borrowedMoney = borrowedMoney;
        this.overdue = overdue;
        this.reserve = reserve;
        this.active = active;
    }

    @Override
    public String toString() {

        return super.toString();
    }
}
