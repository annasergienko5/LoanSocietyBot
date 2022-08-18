package com.example.GringottsTool.Repository;

public class A1NotationParser {
    public String toA1Notation(int columnNumber) {
        String columnA1Notation = "";
        int Base = 26;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int TempNumber = columnNumber;
        while (TempNumber > 0) {
            int position = TempNumber % Base;
            if (position == 0){
                columnA1Notation = 'Z' + columnA1Notation;
            } else {
                if (position > 0) {
                    position = position - 1;
                } else {
                    position = 0;
                }
                columnA1Notation = chars.charAt(position) + columnA1Notation;
            }
            TempNumber = (TempNumber - 1) / Base;
        }
        return columnA1Notation;
    }
}
