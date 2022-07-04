package com.example.GringottsTool;

import com.example.GringottsTool.CRUD.Service;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.security.GeneralSecurityException;


@Component
public class MessageHandler {

    Logger log =  LogManager.getLogger();
    @Autowired
    Service service;

    public BotApiMethod<?> answerMessage(Message message) throws GeneralSecurityException, IOException {
        String chatId = message.getChatId().toString();

        String inputText = message.getText();
        log.info(inputText);

        switch (inputText){
            case "/start":
                return getStartMessage(chatId);
            case "/search":
                return getSearch(chatId);
//            case "/search":
//                return getSearch(chatId);
            default:
                return new SendMessage(chatId, "Неизвестная команда");
        }
    }

//    private BotApiMethod<?> getSearch(String chatId) {
//        String range = "Участники!A1:A20";
//        return
//    }

    private BotApiMethod<?> getSearch(String chatId) throws IOException {
        String range = "Участники!A2:M";
        String result = service.findByPartner(range, "АБ").toString();
        SendMessage sendMessage = new SendMessage(chatId, result);
        return sendMessage;
    }

    private SendMessage getStartMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Привет");
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }
}