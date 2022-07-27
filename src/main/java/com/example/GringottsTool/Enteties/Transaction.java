package com.example.GringottsTool.Enteties;


import com.example.GringottsTool.Constants;

public class Transaction {
    private final String date;
    private final int value;

    public Transaction(String date, int value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public int getValue() {
        return value;
    }
    @Override
    public String toString() {
        return String.format(Constants.TRANSACTION, date, value).replace(',', ' ');
    }
}
