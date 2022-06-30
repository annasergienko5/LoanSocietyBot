package com.example.GringottsTool;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;


@Component
public class MessageHandler {

    Logger log =  LogManager.getLogger();

    public BotApiMethod<?> answerMessage(Message message) {
        String chatId = message.getChatId().toString();

        String inputText = message.getText();
        log.info(inputText);

        if (inputText == null) {
            throw new IllegalArgumentException();
        } else if (inputText.equals("/start")) {
            return getStartMessage(chatId);
        } else {
            return new SendMessage(chatId, "404");
        }
    }

    private SendMessage getStartMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "200");
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }
}