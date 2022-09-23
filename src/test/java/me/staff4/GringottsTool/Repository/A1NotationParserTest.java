package me.staff4.GringottsTool.Repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class A1NotationParserTest {

    @ParameterizedTest
    @CsvSource({
            "0, A",
            "25, Z",
            "26, AA"
    })
    void testToA1Notation(int value, String expected) {
        A1NotationParser a1NotationParser = new A1NotationParser();
        String actual = a1NotationParser.toA1Notation(value);
        assertEquals(expected, actual);
    }
}