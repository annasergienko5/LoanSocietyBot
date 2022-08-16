package com.example.GringottsTool.Exeptions;

import com.example.GringottsTool.Constants;
import com.example.GringottsTool.Repository.A1NotationParser;

public class InvalidDataException extends Exception {
    private String sheetRange;
    private String cellValue;
    private String expectedValue;
    private String columnA1Notation;

    public InvalidDataException (String msg, String sheetRange,int columnNumber, String cellValue, String expectedValue) {
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
