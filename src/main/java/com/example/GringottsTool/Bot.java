package com.example.GringottsTool;


import com.example.GringottsTool.Enteties.Partner;
import com.example.GringottsTool.Repository.Repository;
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
import java.util.List;

public class Bot extends SpringWebhookBot {
    private String botPath;
    private String botUserName;
    private String botToken;

    private final MessageHandler messageHandler;


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
                } catch (GeneralSecurityException | IOException | ParseException e) {
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

    @Scheduled(cron = "${my.cron.expression}")
    public void reportAboutDebts() {
        HashMap<Boolean, List<Partner>> debts = null;
        String chatId = Constants.PUBLIC_CHAT_ID;
        StringBuilder result = new StringBuilder();
        try {
            debts = new Repository().findDebt();
        } catch (IOException | GeneralSecurityException | ParseException e) {
            e.printStackTrace();
        }
        if (debts.size() == 0) {
            try {
                execute(new SendMessage(chatId, Constants.NO_DEBTS));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        result.append("<strong>Список должников:</strong> \n\n");
        for (Partner partner : debts.get(true)) {
            result.append("Участник:\t<strong>" + partner.getName() + "</strong>\n")
                    .append("Текущий долг:\t<strong>" + partner.getDebt() + "</strong>₽\n")
                    .append("Вернуть до:\t<strong>" + partner.getReturnDate() + "</strong>")
                    .append(" - просрочил\n\n");
        }
        for (Partner partner : debts.get(false)) {
            result.append("Участник:\t<strong>" + partner.getName() + "</strong>\n")
                    .append("Текущий долг:\t<strong>" + partner.getDebt() + "</strong>₽\n")
                    .append("Вернуть до:\t<strong>" + partner.getReturnDate() + "</strong>\n\n");
        }
        try {
            SendMessage message = new SendMessage();
            message.setChatId(Constants.PUBLIC_CHAT_ID);
            message.setParseMode(ParseMode.HTML);
            message.setText(result.toString());
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
