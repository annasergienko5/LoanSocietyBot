package me.staff4.GringottsTool;

import me.staff4.GringottsTool.Templates.TemplateEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateEngineTest {
    @Test
    void errorInSomeFunction() {
        String expected = """
                ВНИМАНИЕ: Ошибка в работе программы в команде: /command.
                Обратитесь к разработчику.
                Функция вызвана из чата chatID: 123456789,
                Участником с userTgId:  987654321
                """;
        String actual = TemplateEngine.errorInSomeFunction("/command", "123456789", "987654321");
        assertEquals(expected, actual);
    }

    @Test
    void invalidDataException() {
        String expected =  """
            Ошибка в диапазоне таблицы: Участники!A2:M
            Столбец:    C
            Строка с именем:    Иванов Иван
            Найденное значение: abc123
            Ожидаемое значение: Дата в формате "dd.MM.yyyy\"""";
        String actual = TemplateEngine.invalidDataException("Участники!A2:M", "C",
                "Иванов Иван", "abc123", "Дата в формате \"dd.MM.yyyy\"");
        assertEquals(expected, actual);
    }
}
