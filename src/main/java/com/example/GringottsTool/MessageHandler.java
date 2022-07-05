package com.example.GringottsTool;

import com.example.GringottsTool.CRUD.Service;
import com.example.GringottsTool.Enteties.Cards;
import com.example.GringottsTool.Enteties.Partner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;


@Component
public class MessageHandler {

    Logger log =  LogManager.getLogger();
    @Autowired
    Service service;

    public BotApiMethod<?> answerMessage(Message message) throws GeneralSecurityException, IOException {
        String chatId = message.getChatId().toString();

        String[] inputText = message.getText().split(" ");
        log.info(inputText);

        switch (inputText[0]){
            case "/start":
                return getStartMessage(chatId);
            case "/search":
                if (inputText.length < 2){
                    return new SendMessage(chatId, Constants.NOT_PARAMETERS);
                }
                return getSearch(chatId, inputText[1]);
            case "/status":
                return getStatus(chatId);
            case "/debts":
                return getDebts(chatId);
            case "/cards":
                return getCards(chatId);
            case "/rules":
                return getRules(chatId);
            case "/aboutme":
                return getAboutme(chatId);
            case "/aboutmypayment":
                return getAboutMyPayment(chatId);
            case "/ducklist":
                return getDucklist(chatId);
            default:
                return new SendMessage(chatId, Constants.UKNOWN_COMMAND);
        }
    }

    private BotApiMethod<?> getRules(String chatId) {
        return null;
    }

    private BotApiMethod<?> getDucklist(String chatId) {
        return null;
    }

    private BotApiMethod<?> getAboutMyPayment(String chatId) {
        return null;
    }

    private BotApiMethod<?> getAboutme(String chatId) {
        return null;
    }

    private BotApiMethod<?> getCards(String chatId) throws IOException {
        StringBuffer res = new StringBuffer();
        ArrayList<Cards> cards = service.findCards();
        for (Cards card : cards){
            res.append("\n").append(card.toString());
        }
        return new SendMessage(chatId, res.toString());
    }

    private BotApiMethod<?> getDebts(String chatId) throws IOException {
        StringBuffer result = new StringBuffer();
        ArrayList<Partner> debts = service.findDebt();
        if (debts.size() == 0){
            return new SendMessage(chatId, Constants.NO_DEBTS);
        }
        for (Partner partner : debts){
            result.append("\n")
                    .append(partner.getName())
                    .append(" занял ").append(partner.getDebt())
                    .append(" и обещал вернуть до ").append(partner.getReturnDate());
        }
        return new SendMessage(chatId, result.toString());
    }

    private BotApiMethod<?> getStatus(String chatId) throws IOException {
        String result = service.findInfo().toString();
        return new SendMessage(chatId, result);
    }

    private BotApiMethod<?> getSearch(String chatId, String expected) throws IOException {
        ArrayList<Partner> resultList = service.findPartner(expected);
        SendMessage sendMessage;
        if (resultList.size() == 0){
            return new SendMessage(chatId, Constants.NOT_FOUND_DATA);
        }
        if (resultList.size()>1){
            StringBuffer res = new StringBuffer();
            res.append(Constants.FIND_MORE_RESULT);
            for (Partner partner : resultList){
                res.append("\n" + partner.getTgId());
            }
            sendMessage = new SendMessage(chatId, res.toString());
            return sendMessage;
        }
        sendMessage = new SendMessage(chatId, resultList.get(0).toString());
        return sendMessage;
    }

    private SendMessage getStartMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Привет");
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }
}