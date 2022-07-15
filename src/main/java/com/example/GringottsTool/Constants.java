package com.example.GringottsTool;

public interface Constants {
    String BOT_TOKEN=System.getenv("TOKEN_BOT");
    String BOT_USERNAME=System.getenv("BOT_USERNAME");
    String WEBHOOK_PATH=System.getenv("WEBHOOK_PATH");
    String APPLICATION_NAME=System.getenv("APPLICATION_NAME");
    String CREDENTIALS_FILE_PATH=System.getenv("CREDENTIALS_FILE_PATH");
    String SHEET_ID=System.getenv("SHEET_ID");
    String FIND_MORE_RESULT = "Много таких, уточни:";
    String UKNOWN_COMMAND = "Не выдумывай, нет таких команд";
    String NOT_FOUND_DATA = "Данные не найдены";
    String NOT_PARAMETERS = "Нет параметров поиска. Укажи через пробел после /search";
    String NO_DEBTS = "Должников нет";
    String RULE = System.getenv("RULE");
    String PROXY=System.getenv("PROXY");
    String SCHEDULED_NO_DEBTS = System.getenv("SCHEDULED_NO_DEBTS");
    String PUBLIC_CHAT_ID = System.getenv("PUBLIC_CHAT_ID");

    String ADMIN_CHAT_ID = System.getenv("ADMIN_CHAT_ID");
    String CRON_TIMEZONE = System.getenv("CRON_TIMEZONE");
    String DEBT_REMINDER_TIME = System.getenv("DEBT_REMINDER_TIME");
    String TODAY_PAYERS_REMINDER_TIME = System.getenv("TODAY_PAYERS_REMINDER_TIME");
    String SCHEDULED_NO_TODAY_PAYS = System.getenv("SCHEDULED_NO_TODAY_PAYS");
}
