package com.example.GringottsTool;


import com.example.GringottsTool.Exeptions.InvalidDataException;
import com.example.GringottsTool.Exeptions.NoDataFound;
import com.google.api.client.auth.oauth2.TokenResponseException;
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

public class Bot extends SpringWebhookBot {
    private final MessageHandler messageHandler;
    Logger log = LogManager.getLogger();
    private String botPath;
    private String botUserName;
    private String botToken;
    private final String CRON_DEBT_SCHEDULE = "${cron.expression.debt}";
    private final String CRON_TODAY_PAYERS = "${cron.expression.todayPayers}";
    private final String CRON_ZONE = "${cron.expression.zone}";
    public Bot(SetWebhook setWebhook, MessageHandler messageHandler) {
        super(setWebhook);
        this.messageHandler = messageHandler;
    }
    public void reportStartMessage() {
        executeMessage(Constants.START_MESSAGE, Constants.ADMIN_CHAT_ID);
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
            String chatId = message.getChatId().toString();
            long userTgId = message.getFrom().getId();
            String userName = message.getChat().getUserName();
            if (message.getText() != null) {
                String[] inputText = message.getText().split("@", 2);
                String usedFunction = inputText[0];
                String errorMessage = String.format(Constants.ERROR_IN_SOME_FUNCTION, usedFunction, chatId, userTgId, userName);
                    try {
                        return messageHandler.answerMessage(update.getMessage());
                    } catch (TokenResponseException e) {
                        log.error(errorMessage, e);
                        executeMessage(Constants.TOKEN_RESPONSE_EXCEPTION, Constants.ADMIN_CHAT_ID);
                        System.exit(-1);
                    } catch (GeneralSecurityException | IOException e) {
                        log.error(errorMessage, e);
                        executeMessage(errorMessage, Constants.ADMIN_CHAT_ID);
                    } catch (NoDataFound e) {
                        log.info(e.getMessage(), e);
                        executeMessage(e.getMessage(), chatId);
                    } catch (InvalidDataException e) {
                        executeMessage(Constants.INVALID_DATA_IN_CELLS, chatId);
                        executeMessage(errorMessage + Constants.INVALID_DATA_IN_CELLS_TO_ADMIN + e.toMessage(), Constants.ADMIN_CHAT_ID);
                    } catch (NumberFormatException | ParseException e) {
                        log.info(e.getMessage(), e);
                        executeMessage(Constants.INVALID_DATA_IN_CELLS, chatId);
                        executeMessage(errorMessage + Constants.INVALID_DATA_IN_CELLS_TO_ADMIN, Constants.ADMIN_CHAT_ID);
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
        log.info("Making everyMonth request about Debts...");
        try {
            String text = messageHandler.getDebtors();
            if (text != null) {
                executeMessage(text, Constants.PUBLIC_CHAT_ID);
                executeMessage(text, Constants.ADMIN_CHAT_ID);
            }
        } catch (GeneralSecurityException | IOException | ParseException e) {
            log.error("error: ", e);
            executeMessage(Constants.ERROR_NOTIFICATION, Constants.ADMIN_CHAT_ID);
        } catch (NoDataFound ignored) {
        }
    }

    @Scheduled(cron = CRON_TODAY_PAYERS, zone = CRON_ZONE)
    public void reportAboutTodayDebts()  {
        log.info("Making everyDay request about Debts...");
        try {
            String text = messageHandler.getTodayDebtors();
            if (text != null) {
                executeMessage(text, Constants.PUBLIC_CHAT_ID);
                executeMessage(text, Constants.ADMIN_CHAT_ID);
            }
        } catch (IOException e) {
            log.error("error: ", e);
            executeMessage(Constants.ERROR_NOTIFICATION, Constants.ADMIN_CHAT_ID);
        } catch (NoDataFound ignored) {
        }
    }


    private void executeMessage(String text, String chatID) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatID);
            message.setParseMode(ParseMode.HTML);
            message.setText(text);
            execute(message);
        } catch (TelegramApiException e) {
            log.error("error: ", e);
        }
    }
}
