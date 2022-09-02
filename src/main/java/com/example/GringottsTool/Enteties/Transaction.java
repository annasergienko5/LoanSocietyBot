package com.example.GringottsTool.Enteties;


import com.example.GringottsTool.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class Transaction {
    private final String date;
    private final int value;

    @Override
    public String toString() {
        return String.format(Constants.TRANSACTION, date, value)
                .replace(',', ' ');
    }
}
