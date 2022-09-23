package me.staff4.GringottsTool.Exeptions;

import lombok.AllArgsConstructor;
import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.Repository.A1NotationParser;
@AllArgsConstructor
public final class InvalidDataException extends Exception {
    private final String sheetRange;
    private final String cellValue;
    private final String expectedValue;
    private final String columnA1Notation;
    private final String name;

    public InvalidDataException(final String msg,
                                final String sheetRange,
                                final int columnNumber,
                                final String cellValue,
                                final String expectedValue,
                                final String name) {
        super(msg);
        this.cellValue = cellValue;
        this.expectedValue = expectedValue;
        this.columnA1Notation = new A1NotationParser().toA1Notation(columnNumber);
        this.sheetRange = sheetRange;
        this.name = name;
    }

    public String toMessage() {
        return String.format(Constants.INVALID_DATA_EXCEPTION,
                sheetRange,
                columnA1Notation,
                name,
                cellValue,
                expectedValue);
    }
}
