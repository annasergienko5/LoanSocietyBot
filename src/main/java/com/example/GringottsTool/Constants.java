package com.example.GringottsTool;

public interface Constants {
    double MAXIMUM_LOAN_COEFFICIENT = 0.125;
    String BOT_TOKEN=System.getenv("TOKEN_BOT");
    String BOT_USERNAME=System.getenv("BOT_USERNAME");
    String WEBHOOK_PATH=System.getenv("WEBHOOK_PATH");
    String APPLICATION_NAME=System.getenv("APPLICATION_NAME");
    String CREDENTIALS_FILE_PATH=System.getenv("CREDENTIALS_FILE_PATH");
    String SHEET_ID=System.getenv("SHEET_ID");
    String FIND_MORE_RESULT = "Много таких, уточни";
    String NOT_FOUND_DATA = "Данные не найдены";
    String NOT_PARAMETERS = "Нет параметров поиска. Укажи через пробел после /search";
    String NO_DEBTS = "Должников нет";
    String HELP_OUR = """
            /id - получить id текущего чата
            /status - баланс кассы
            /debts - список должников
            /cards - список держателей
            /rules - правила кассы""";
    String HELP = """
            /id - получить id текущего чата
            /search - поиск участника
            /status - баланс кассы
            /debts - список должников
            /cards - список держателей
            /rules - правила кассы
            /aboutme - статистика по мне
            /aboutmypayment - выписка по платежам
            /proxy - ссылка на наш прокси для телеги
            /ducklist - список премиальных участников
            /credithistory - история займов краткая
            /credithistoryfull - история займов с транзакциями""";
    String RULE = System.getenv("RULE");
    String PROXY=System.getenv("PROXY");
    String SCHEDULED_NO_DEBTS = "На сегодня должников нет";
    String PUBLIC_CHAT_ID = System.getenv("PUBLIC_CHAT_ID");

    String ADMIN_CHAT_ID = System.getenv("ADMIN_CHAT_ID");
    String CRON_TIMEZONE = System.getenv("CRON_TIMEZONE");
    String DEBT_REMINDER_TIME = System.getenv("DEBT_REMINDER_TIME");
    String TODAY_PAYERS_REMINDER_TIME = System.getenv("TODAY_PAYERS_REMINDER_TIME");
    String SCHEDULED_NO_TODAY_PAYS ="Сегодня платежей в срочном порядке не ожидается.";
    String ABOUT_CREDIT_HISTORY_MESSAGE = """
            История займов Участника -
            <strong>%s</strong>:
            %s
            """;
    String TRANSACTION = """
            Дата:\t<strong>%s</strong>
            Сумма транзакций:\t<strong>%,+d</strong> ₽
            """;
    String LOAN_WITH_TRANSACTIONS = """
                                
            <strong>Займ №:\t%s</strong>
            Дата открытия:\t<strong>%s</strong>
            Дата закрытия:\t<strong>%s</strong>
            Сумма займа:\t<strong>%,+d</strong> ₽
                                
            <em>Список транзакций по займу:</em>
            %s
            <strong>___</strong>
            """;
    String LOAN_WITHOUT_TRANSACTIONS = """
                                
            <strong>Займ №:\t%s</strong>
            Дата открытия:\t<strong>%s</strong>
            Дата закрытия:\t<strong>%s</strong>
            Сумма займа:\t<strong>%,+d</strong> ₽
            <strong>___</strong>
            """;
}
