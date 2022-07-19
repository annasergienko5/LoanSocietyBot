package com.example.GringottsTool;


import com.example.GringottsTool.Enteties.Partner;
import com.example.GringottsTool.Exeptions.NoDataFound;
import com.example.GringottsTool.Repository.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Bot extends SpringWebhookBot {
    private final MessageHandler messageHandler;
    Logger log = LogManager.getLogger();
    private String botPath;
    private String botUserName;
    private String botToken;
    private final String CRON_DEBT_SCHEDULE = "${cron.expression.debt}";
    private final String CRON_TODAY_PAYERS = "${cron.expression.todayPayers}";
    private final String CRON_ZONE = "${cron.expression.zone}";

    private final String ABOUT_DEBTS_MESSAGE = """
                    <strong>Список участников с просроченной задолженностью:</strong>
                                        
                    %s
                    <strong>Список участников с задолженностью:</strong>
                                        
                    %s
                    """;
    private final String TODAY_DEBTS_MESSAGE ="""
                    <strong>Сегодня ожидаем погашения задолженности следующих Участников:</strong>
                                        
                    %s
                                        
                    """;
    private final String ARREARS_DEBTS = """
                    Участник:\t<strong>%s</strong>
                    Текущий долг:\t<strong>%s</strong>₽
                    Вернуть до:\t<strong>%s</strong>
                    <strong>ВНИМАНИЕ:\tПРОСРОЧКА</strong>

                    """;
    private final String SIMPLE_DEBTS = """
                    Участник:\t<strong>%s</strong>
                    Текущий долг:\t<strong>%s</strong>₽
                    Вернуть до:\t<strong>%s</strong>

                    """;


    public Bot(SetWebhook setWebhook, MessageHandler messageHandler) {
        super(setWebhook);
        this.messageHandler = messageHandler;
    }


    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.getText() != null) {
                try {
                    return messageHandler.answerMessage(update.getMessage());
                } catch (GeneralSecurityException | IOException | ParseException | NoDataFound e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    public void setBotPath(String botPath) {
        this.botPath = botPath;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    @Scheduled(cron = CRON_DEBT_SCHEDULE, zone = CRON_ZONE)
    public void reportAboutDebts() {
        log.info("Отправляется запрос о должниках в таблицу.");
        List<List<Partner>> debts = getResponseAboutDebts();
        String text;
        if (debts.size() != 0) {
            text = String.format(ABOUT_DEBTS_MESSAGE, getStringAboutArrearsDebts(debts), getStringAboutSimpleDebts(debts));
        } else {
            text = Constants.SCHEDULED_NO_DEBTS;
        }
        executeMessage(text, Constants.PUBLIC_CHAT_ID);
        executeMessage(text, Constants.ADMIN_CHAT_ID);
    }

    @Scheduled(cron = CRON_TODAY_PAYERS, zone = CRON_ZONE)
    public void reportAboutTodayDebts() throws GeneralSecurityException, IOException, NoDataFound {
        log.info("Отправляется запрос о Сегодняшних должниках в таблицу.");
        LinkedList<Partner> persons = new Repository().getTodayPayPersons();
        String text;
        if (persons.size() != 0) {
            text = String.format(TODAY_DEBTS_MESSAGE, getStringAboutTodayDebts(persons));
        } else {
            text = Constants.SCHEDULED_NO_TODAY_PAYS;
        }
        executeMessage(text, Constants.PUBLIC_CHAT_ID);
        executeMessage(text, Constants.ADMIN_CHAT_ID);
    }

    private List<List<Partner>> getResponseAboutDebts() {
        List<List<Partner>> debts = null;
        String chatId = Constants.PUBLIC_CHAT_ID;
        StringBuilder result = new StringBuilder();
        try {
            debts = new Repository().findDebt();
        } catch (IOException | GeneralSecurityException | ParseException | NoDataFound e) {
            e.printStackTrace();
        }
        return debts;
    }

    private String getStringAboutArrearsDebts(List<List<Partner>> debts) {
        StringBuilder result = new StringBuilder();
        for (Partner partner : debts.get(0)) {
            String text = String.format(ARREARS_DEBTS, partner.getName(), partner.getDebt(), partner.getReturnDate());
            result.append(text);
        }
        return result.toString();
    }

    private String getStringAboutSimpleDebts(List<List<Partner>> debts) {
        StringBuilder result = new StringBuilder();
        for (Partner partner : debts.get(1)) {
            String text = String.format(SIMPLE_DEBTS, partner.getName(), partner.getDebt(), partner.getReturnDate());
            result.append(text);
        }
        return result.toString();
    }

    private String getStringAboutTodayDebts(LinkedList<Partner> debts) {
        StringBuilder result = new StringBuilder();
        for (Partner partner : debts) {
            String text = String.format(SIMPLE_DEBTS, partner.getName(), partner.getDebt(), partner.getReturnDate());
            result.append(text);
        }
        return result.toString();
    }

    private void executeMessage(String text, String chatID) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatID);
            message.setParseMode(ParseMode.HTML);
            message.setText(text);
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
