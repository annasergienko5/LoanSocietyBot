package me.staff4.GringottsTool;

public interface Constants {
    String START_MESSAGE = "Штатно! Боеготово!";
    double MAXIMUM_LOAN_COEFFICIENT = 0.125;
    double MINIMUM_LOAN_COEFFICIENT = 0.6;
    String TOKEN_BOT = System.getenv("TOKEN_BOT");
    String BOT_USERNAME = System.getenv("BOT_USERNAME");
    String WEBHOOK_PATH = System.getenv("WEBHOOK_PATH");
    String APPLICATION_NAME = System.getenv("APPLICATION_NAME");
    String CREDENTIALS_FILE_PATH = System.getenv("CREDENTIALS_FILE_PATH");
    String SHEET_ID = System.getenv("SHEET_ID");
    String RULE = System.getenv("RULE");
    String PUBLIC_CHAT_ID = System.getenv("PUBLIC_CHAT_ID");
    String ADMIN_CHAT_ID = System.getenv("ADMIN_CHAT_ID");
    String CRON_TIMEZONE = System.getenv("CRON_TIMEZONE");
    String FIND_MORE_RESULT = "Много таких, уточни.";
    String NOT_FOUND_DATA = "Данные не найдены";
    String INVALID_DATA_IN_CELLS = "В базе данных сохранены неверные данные.\n Обратитесь к Администратору.";
    String INVALID_DATA_IN_CELLS_TO_ADMIN = "В базе данных сохранены неверные данные.\n";
    String EXPECTED_CELL_VALUE_NUMERIC_DECIMAL = "Числовое значение без разделительных знаков, арабскими цифрами."
            + " Пример: \"2000\"";
    String EXPECTED_CELL_VALUE_FLOAT = "Числовое значение. Пример: \"250\" или \"3.42\"";
    String NOT_PARAMETERS = "Нет параметров поиска. Укажи через пробел после /search";
    String NOT_PARAMETERS_FULLSEARCH = "Нет параметров поиска. Укажи через пробел после /fullsearch";
    String NOT_MONEY = "Нет суммы займа или тг. Укажи через пробел после /newloan";
    String NO_AMOUNT_OF_MONEY = "Нет запрашиваемой суммы. Укажи через пробел после /fast";
    String NO_TEXT = "Нет текста сообщения. Укажи через пробел после /sendToAll";
    String NO_MONEY_LOAN = "Запрашиваемая сумма отсутствует или указана неверно. Укажи через пробел после /loan."
            + " Пример: 250 или 3.42";
    String NO_TARGET_LOAN = "Укажи срок возврата, график погашений и пару слов о причинах через пробел после суммы";
    String NO_MONEY_AND_TERGET_LOAN = "Отсутствует запрашиваемая сумма, срок возврата, график погашений"
            + " и пару слов о причинах. Укажи через пробел после /loan";
    String LOAN_DENIED = "В займе отказано: сумма превышает доступные тебе 0.6";
    String LOAN_APPROVED = "Одобрен займ в пределах 0.6 на сумму ";
    String INCORRECT_MONEY_TYPE = "Неверная запрашиваемая сумма. Пример: 250 или 3.42";
    String HELP_PUBLIC_CHAT = "HelpFile/Help_Public_Chat";
    String HELP_ADMIN_CHAT = "HelpFile/Help_Admin_Chat";
    String HELP_PRIVAT_CHAT = "HelpFile/Help_Privat_Chat";
    String ADDED_IN_QUEUE = "Добавлен в очередь";
    String ALREADY_ADDED_IN_QUEUE = "Кабанчик уже был добавлен в очередь. Теперь перезаписан";
    String NOT_PARTNERS = "Нет такого кабанчика в базе";
    String ERROR_SEND_MESSAGE_TG = "Ошибка отправки сообщения в тг";
    String NOT_PARTNER_FROM_ID = "Неправославный id. Не записываю";
    String ERROR_OUT_WRITE_IN_BOT = "Ошибка записи в очередь со стороны бота";
    String ERROR_OUT_WRITE_IN_MESSAGEHANDLER = "Ошибка записи в очередь со стороны обработки сообщений";
    String ERROR_TAKING_IN_BOT = "Остановка потока Bot";
    String ERROR_TAKING_IN_MESSAGEHANDLER = "Остановка потока MessageHandler";
    String OVERDUE = "*Просрочено:*\n\n";
    String DEBTORS = "*Заёмщики:*\n\n";
    String NOT_PARTNER = "Для использования функций бота необходимо быть участником";
    String VERSION = "dev";
    String NO_PERSON_FOUND = "Участник не найден.";
    String NO_TRANSACTIONS_FOUND = "Транзакции по займам отсутствуют.";
    String TRANSACTIONS_BY_FILE = "Транзакции по займам записаны в файл:";
    String ERROR_WRITING_TXT_FILE = "Ошибка записи txt-файла.";
    String ERROR_DELETING_TEMP_FILE = "Ошибка удаления временного файла.";
    String EXPECTED_CELL_VALUE_DATE = "Дата в формате \"dd.MM.yyyy\"";
    String EXPECTED_CELL_VALUE_LAST_3_MONTH = "От \"1\" и больше.";
    String ERROR_READ_FILE = "Ошибка чтения файла";
    String EXPECTED_CELL_VALUE_TG_ID = "Числовое значение без разделительных знаков, арабскими цифрами."
            + " Значение должно содержать tgId.  \nПример: \"5197856969\"";
}
