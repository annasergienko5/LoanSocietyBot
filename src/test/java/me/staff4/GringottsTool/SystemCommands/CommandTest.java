package me.staff4.GringottsTool.SystemCommands;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    @ParameterizedTest
    @CsvSource({
            "test, false",
            "DEBTORS, true",
            "TODAY_DEBTORS, true"
    })
    void is(String value, boolean expected) {
        assertEquals(Command.is(value), expected);
    }
}