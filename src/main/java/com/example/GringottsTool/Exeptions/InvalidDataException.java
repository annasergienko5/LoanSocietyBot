package com.example.GringottsTool.Exeptions;

import com.example.GringottsTool.Constants;
import com.example.GringottsTool.Repository.A1NotationParser;

public final class InvalidDataException extends Exception {
    private final String sheetRange;
    private final String cellValue;
    private final String expectedValue;
    private final String columnA1Notation;

    public InvalidDataException(final String msg, final String sheetRange, final int columnNumber,
                                final String cellValue,
                                final String expectedValue) {
        super(msg);
        this.cellValue = cellValue;
        this.expectedValue = expectedValue;
        this.columnA1Notation = new A1NotationParser().toA1Notation(columnNumber);
        this.sheetRange = sheetRange;
    }

    public String toMessage() {
        return String.format(Constants.INVALID_DATA_EXCEPTION, sheetRange, columnA1Notation, cellValue, expectedValue);
    }
}
