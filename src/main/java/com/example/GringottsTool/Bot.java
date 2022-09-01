package com.example.GringottsTool;


import com.example.GringottsTool.DTO.IncomingMessage;
import com.example.GringottsTool.DTO.OutgoingMessage;
import com.example.GringottsTool.Exeptions.HealthExeption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.concurrent.BlockingQueue;

public class Bot extends SpringWebhookBot implements Runnable, Healthcheckable {
    private final BlockingQueue<IncomingMessage> inQueue;
    private final BlockingQueue<OutgoingMessage> outQueue;
    Logger log = LogManager.getLogger();
    private String botPath;
    private String botUserName;
    private String botToken;
    private final String CRON_DEBT_SCHEDULE = "${cron.expression.debt}";
    private final String CRON_TODAY_PAYERS = "${cron.expression.todayPayers}";
    private final String CRON_ZONE = "${cron.expression.zone}";
    public Bot(SetWebhook setWebhook, BlockingQueue<IncomingMessage> inQueue, BlockingQueue<OutgoingMessage> outQueue) {
        super(setWebhook);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
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
    public void run() {
        while(true){
            try {
                OutgoingMessage outgoingMessage = outQueue.take();
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(outgoingMessage.getChatId());
                sendMessage.setText(outgoingMessage.getText());
                if (outgoingMessage.isEnableMarkdown()){
                    sendMessage.enableMarkdown(true);
                }else sendMessage.setParseMode(outgoingMessage.getParseMode());
                execute(sendMessage);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TelegramApiException e) {
                log.info(Constants.ERROR_SEND_MESSAGE_TG);
            }
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return null;
        }
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        long userTgId = message.getFrom().getId();

        if (message.getDate() < (System.currentTimeMillis() / 1000L)){
            return null;
        }

        if (message.getText() == null) {
            return null;
        }
        try {
            IncomingMessage incomingMessage = new IncomingMessage(chatId, userTgId, message.getText());
            inQueue.put(incomingMessage);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
            IncomingMessage incomingMessagePublic = new IncomingMessage(Constants.PUBLIC_CHAT_ID, "getDebtors");
            IncomingMessage incomingMessageAdmin = new IncomingMessage(Constants.ADMIN_CHAT_ID, "getDebtors");
            inQueue.put(incomingMessageAdmin);
            inQueue.put(incomingMessagePublic);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = CRON_TODAY_PAYERS, zone = CRON_ZONE)
    public void reportAboutTodayDebts()  {
        log.info("Making everyDay request about Debts...");
        try {
            IncomingMessage incomingMessagePublic = new IncomingMessage(Constants.PUBLIC_CHAT_ID, "getTodayDebtors");
            IncomingMessage incomingMessageAdmin = new IncomingMessage(Constants.ADMIN_CHAT_ID, "getTodayDebtors");
            inQueue.put(incomingMessageAdmin);
            inQueue.put(incomingMessagePublic);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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

    @Override
    public void isAlive() throws HealthExeption {
        try {
            Message sendMessage = execute(new SendMessage(Constants.ADMIN_CHAT_ID, "Проверка"));
            execute(new DeleteMessage(Constants.ADMIN_CHAT_ID, sendMessage.getMessageId()));
            log.info("Bot is healthy");
        } catch (TelegramApiException e) {
            throw new HealthExeption("There is a problem with Bot");
        }
    }
}
