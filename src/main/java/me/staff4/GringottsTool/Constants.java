package me.staff4.GringottsTool;

public interface Constants {
    String START_MESSAGE = "Добрый день! Бот работает и готов принимать запросы.";
    double MAXIMUM_LOAN_COEFFICIENT = 0.125;
    double MINIMUM_LOAN_COEFFICIENT = 0.6;
    String TOKEN_BOT = System.getenv("TOKEN_BOT");
    String BOT_USERNAME = System.getenv("BOT_USERNAME");
    String WEBHOOK_PATH = System.getenv("WEBHOOK_PATH");
    String APPLICATION_NAME = System.getenv("APPLICATION_NAME");
    String CREDENTIALS_FILE_PATH = System.getenv("CREDENTIALS_FILE_PATH");
    String SHEET_ID = System.getenv("SHEET_ID");
    String FIND_MORE_RESULT = "Много таких, уточни";
    String NOT_FOUND_DATA = "Данные не найдены";
    String INVALID_DATA_IN_CELLS = "В базе данных сохранены неверные данные.\n Обратитесь к Администратору.";
    String INVALID_DATA_IN_CELLS_TO_ADMIN = "В базе данных сохранены неверные данные.\n";
    String ERROR_IN_SOME_FUNCTION = """
            ВНИМАНИЕ: Ошибка в работе программы в команде: %s.
            Обратитесь к разработчику.
            Функция вызвана из чата chatID:\t%s,
            Участником с userTgId:\t%s
            """;
    String INVALID_DATA_EXCEPTION =  """
            Ошибка в диапазоне таблицы:\t%s
            Столбец: \t%s
            Найденное значение:\t%s
            Ожидаемое значение:\t%s
            """;
    String NUMERIC_DECIMAL_EXPECTED_VALUE = "Числовое значение без разделительных знаков, арабскими цифрами."
            + " Пример: \"2000\"";
    String NOT_PARAMETERS = "Нет параметров поиска. Укажи через пробел после /search";
    String NOT_MONEY = "Нет суммы займа или тг. Укажи через пробел после /newloan";
    String NO_AMOUNT_OF_MONEY = "Нет запрашиваемой суммы. Укажи через пробел после /fast";
    String LOAN_DENIED = "В займе отказано: сумма превышает доступные тебе 0.6";
    String LOAN_APPROVED = "Одобрен займ в пределах 0.6 на сумму ";
    String FAST_MESSAGE_TO_ADMINS = "[Пользователь](tg://user?id=%d) запросил быстрый займ на сумму %d";
    String INCORRECT_AMOUNT_OF_MONEY = "Неверная запрашиваемая сумма";
    String INCORRECT_MONEY_TYPE = "Неверная запрашиваемая сумма. Укажи целое число";
    String HELP_PUBLIC_CHAT = """
            /id - получить id текущего чата
            /status - баланс кассы
            /debts - список должников
            /cards - список держателей
            /rules - правила кассы
            /fast - попросить быстрый займ""";
    String HELP_ADMIN_CHAT = """
            /id - получить id текущего чата
            /version - получить версию бота
            /search - поиск участника
            /status - баланс кассы
            /debts - список должников
            /cards - список держателей
            /rules - правила кассы
            /aboutme - статистика по мне
            /aboutmypayment - выписка по платежам
            /proxy - ссылка на наш прокси для телеги
            /ducklist - список премиальных участников
            /newloan - добавить займ в очередь
            /queue - показать очередь""";
    String HELP_PRIVAT_CHAT = """
            /id - получить id текущего чата
            /version - получить версию бота
            /search - поиск участника
            /status - баланс кассы
            /debts - список должников
            /cards - список держателей
            /rules - правила кассы
            /aboutme - статистика по мне
            /aboutmypayment - выписка по платежам
            /proxy - ссылка на наш прокси для телеги
            /ducklist - список премиальных участников
            /fast - попросить быстрый займ
            /queue - показать очередь
            /credithistory - история займов краткая
            /credithistoryfull - история займов с транзакциями""";
    String RULE = System.getenv("RULE");
    String PUBLIC_CHAT_ID = System.getenv("PUBLIC_CHAT_ID");
    String ADMIN_CHAT_ID = System.getenv("ADMIN_CHAT_ID");
    String CRON_TIMEZONE = System.getenv("CRON_TIMEZONE");
    String ADDED_IN_QUEUE = "Добавлен в очередь";
    String ALREADY_ADDED_IN_QUEUE = "Кабанчик уже был добавлен в очередь. Теперь перезаписан";
    String NOT_PARTNERS = "Нет такого кабанчика в базе";
    String ERROR_SEND_MESSAGE_TG = "Ошибка отправки сообщения в тг";
    String NOT_PARTNER_FROM_ID = "Неправославный id. Не записываю";
    String ERROR_OUT_WRITE_IN_BOT = "Ошибка записи в очередь со стороны бота";
    String ERROR_OUT_WRITE_IN_MESSAGEHANDLER = "Ошибка записи в очередь со стороны обработки сообщений";
    String ERROR_TAKING_IN_BOT = "Ошибка чтения из очереди со стороны бота";
    String ERROR_TAKING_IN_MESSAGEHANDLER = "Ошибка чтения из очереди со стороны обработки сообщений";
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
    String TODAY_DEBTS_MESSAGE = """
            <strong>Сегодня ожидаем погашения задолженности следующих Участников:</strong>
                                
            %s
                                
            """;
    String SIMPLE_DEBTS = """
            Участник:\t<strong>%s</strong>
            Текущий долг:\t<strong>%s</strong>₽
            Вернуть до:\t<strong>%s</strong>

            """;
    String OVERDUE_DEBTORS = """
            \t*%s*
            \t%s₽
            до:\t%s

            """;
    String NOT_OVERDUE_DEBTORS = """
            \t*%s*
            \t%s₽
            до:\t%s
            последний платёж:\t%s

            """;
    String NOT_PARTNER = "Для использования функций бота необходимо быть участником";
    String VERSION = "dev";
}
