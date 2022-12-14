package me.staff4.GringottsTool.Repository;

import org.springframework.stereotype.Component;

@Component
public final class A1NotationParser {
    private static final int COUNT_OF_ALPHABET = 26;
    public String toA1Notation(final int columnNumber) {
        String columnA1Notation = "";
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int tempNumber = columnNumber + 1;
        while (tempNumber > 0) {
            int position = tempNumber % COUNT_OF_ALPHABET;
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
            tempNumber = (tempNumber - 1) / COUNT_OF_ALPHABET;
        }
        return columnA1Notation;
    }
}
