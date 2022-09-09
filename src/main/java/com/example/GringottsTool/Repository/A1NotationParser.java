package com.example.GringottsTool.Repository;

import com.example.GringottsTool.Constants;

public final class A1NotationParser {
    public String toA1Notation(final int columnNumber) {
        String columnA1Notation = "";
        int base = Constants.COUNT_OF_ALPHABET;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int tempNumber = columnNumber;
        while (tempNumber > 0) {
            int position = tempNumber % base;
            if (position == 0) {
                columnA1Notation = 'Z' + columnA1Notation;
            } else {
                if (position > 0) {
                    position = position - 1;
                } else {
                    position = 0;
                }
                columnA1Notation = chars.charAt(position) + columnA1Notation;
            }
            tempNumber = (tempNumber - 1) / base;
        }
        return columnA1Notation;
    }
}
