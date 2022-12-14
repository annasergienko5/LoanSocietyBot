package me.staff4.GringottsTool;


import lombok.extern.slf4j.Slf4j;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.IncomingMessageType;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.Exeptions.HealthExeption;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;

@Slf4j
public final class Bot extends SpringWebhookBot implements Runnable, Healthcheckable {

    private final BlockingQueue<IncomingMessage> inQueue;
    private final BlockingQueue<OutgoingMessage> outQueue;
    private String botPath;
    private String botUserName;
    private String botToken;

    private static final long TIME_DIVISOR = 1000L;
    private static final int MINUTE = 5;
    private static final long SECOND_IN_1_MINUTE = 60L;

    public Bot(final SetWebhook setWebhook, final BlockingQueue<IncomingMessage> inQueue,
               final BlockingQueue<OutgoingMessage> outQueue) {
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

    public void setBotToken(final String botToken) {
        this.botToken = botToken;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                OutgoingMessage outgoingMessage = outQueue.take();
                if (outgoingMessage.getType() == OutgoingMessageType.POLL) {
                    sendPollx(outgoingMessage);
                } else {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(outgoingMessage.getChatId());
                    sendMessage.setText(outgoingMessage.getText());
                    sendMessage.setReplyToMessageId(outgoingMessage.getReplyToMessageId());
                    if (outgoingMessage.isEnableMarkdown()) {
                        sendMessage.enableMarkdown(true);
                    } else {
                        sendMessage.setParseMode(outgoingMessage.getParseMode());
                    }
                    execute(sendMessage);
                }
                if (outgoingMessage.isHasDocument()) {
                    String filePath = outgoingMessage.getDocumentFilePath();
                    SendDocument sendDocument = new SendDocument(outgoingMessage.getChatId(),
                            new InputFile(new File(filePath)));
                    sendDocument.setCaption(outgoingMessage.getDocumentFileName());
                    execute(sendDocument);
                    Files.delete(Paths.get(filePath));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info(Constants.ERROR_TAKING_IN_BOT);
            } catch (TelegramApiException e) {
                log.info(Constants.ERROR_SEND_MESSAGE_TG, e);
            } catch (IOException e) {
                log.info(Constants.ERROR_DELETING_TEMP_FILE, e);
            }
        }
    }

    private void sendPollx(final OutgoingMessage outgoingMessage) throws TelegramApiException, InterruptedException {
        SendPoll sendPoll = new SendPoll(Constants.PUBLIC_CHAT_ID, outgoingMessage.getText(),
                outgoingMessage.getOptions());
        sendPoll.setIsAnonymous(false);
        Message pollMessage = execute(sendPoll);
        inQueue.put(IncomingMessage.builder().type(IncomingMessageType.POLL).
                userTgId(Long.parseLong(outgoingMessage.getUserTgId())).
                messageId(pollMessage.getMessageId()).
                build());
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(final Update update) {
        if (!update.hasMessage()) {
            return null;
        }
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        long userTgId = message.getFrom().getId();
        String text = message.getText();
        if (checkTimeMessage(message.getDate())) {
            return null;
        }
        if (text == null) {
            return null;
        }
        try {
            IncomingMessage incomingMessage = IncomingMessage.builder().
                    type(IncomingMessageType.COMMAND).
                    chatId(chatId).
                    userTgId(userTgId).
                    text(text).
                    messageId(message.getMessageId()).
                    build();
            inQueue.put(incomingMessage);
        } catch (InterruptedException e) {
            executeMessage(Constants.ERROR_OUT_WRITE_IN_BOT, Constants.ADMIN_CHAT_ID);
        }
        return null;
    }

    private boolean checkTimeMessage(final int messageDate) {
        return messageDate + MINUTE * SECOND_IN_1_MINUTE < (System.currentTimeMillis() / TIME_DIVISOR);
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    public void setBotPath(final String botPath) {
        this.botPath = botPath;
    }

    public void setBotUserName(final String botUserName) {
        this.botUserName = botUserName;
    }


    private void executeMessage(final String text, final String chatID) {
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
            Message sendMessage = execute(new SendMessage(Constants.ADMIN_CHAT_ID, "????????????????"));
            execute(new DeleteMessage(Constants.ADMIN_CHAT_ID, sendMessage.getMessageId()));
            log.info("Bot is healthy");
        } catch (TelegramApiException e) {
            throw new HealthExeption("There is a problem with Bot");
        }
    }
}
