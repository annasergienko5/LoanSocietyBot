package com.example.GringottsTool;

import com.example.GringottsTool.CRUD.Service;
import com.example.GringottsTool.Enteties.Cards;
import com.example.GringottsTool.Enteties.Contributions;
import com.example.GringottsTool.Enteties.Partner;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
public class MessageHandler {

    Logger log =  LogManager.getLogger();
    @Autowired
    Service service;

    public BotApiMethod<?> answerMessage(Message message) throws GeneralSecurityException, IOException, ParseException {
        String chatId = message.getChatId().toString();
        String tgId = message.getChat().getUserName();
        log.info(tgId);
        if (tgId == null){
            String[] inputText = message.getText().split("@", 2);
            switch (inputText[0]){
                case "/status":
                    return getStatus(chatId);
                case "/debts":
                    return getDebts(chatId);
                case "/cards":
                    return getCards(chatId);
                case "/rules":
                    return getRules(chatId);
                default:
                    return new SendMessage(chatId, Constants.UKNOWN_COMMAND);
            }
        }else {
            String[] inputText = message.getText().split(" ", 2);
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
                    return getAboutme(chatId, tgId);
                case "/aboutmypayment":
                    return getAboutMyPayment(chatId, tgId);
                case "/ducklist":
                    return getDucklist(chatId);
                default:
                    return new SendMessage(chatId, Constants.UKNOWN_COMMAND);
            }
        }
    }

    private BotApiMethod<?> getRules(String chatId) {
        return new SendMessage(chatId, Constants.RULE);
    }

    private BotApiMethod<?> getDucklist(String chatId) {
        return null;
    }

    private BotApiMethod<?> getAboutMyPayment(String chatId, String tgId) throws IOException {
        String expected = service.findPartner(tgId).get(0).getName();
        Contributions contributions = service.findContribution(expected);
        return new SendMessage(chatId, contributions.toString());
    }

    private BotApiMethod<?> getAboutme(String chatId, String tgId) throws IOException {
        ArrayList<Partner> resultList = service.findPartner(tgId);
        SendMessage sendMessage;
        if (resultList.size() == 0) {
            return new SendMessage(chatId, Constants.NOT_FOUND_DATA);
        }
        if (resultList.size() > 1) {
            StringBuffer res = new StringBuffer();
            res.append(Constants.FIND_MORE_RESULT);
            for (Partner partner : resultList) {
                res.append("\n" + partner.getTgId());
            }
            sendMessage = new SendMessage(chatId, res.toString());
            return sendMessage;
        }
        sendMessage = new SendMessage(chatId, resultList.get(0).toString());
        return sendMessage;
    }

    private BotApiMethod<?> getCards(String chatId) throws IOException {
        StringBuffer res = new StringBuffer();
        ArrayList<Cards> cards = service.findCards();
        for (Cards card : cards){
            res.append("\n").append(card.toString());
        }
        return new SendMessage(chatId, res.toString());
    }

    private BotApiMethod<?> getDebts(String chatId) throws IOException, ParseException {
        StringBuffer result = new StringBuffer();
        HashMap<Boolean, List<Partner>> debts = service.findDebt();
        if (debts.size() == 0) {
            return new SendMessage(chatId, Constants.NO_DEBTS);
        }
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
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(result.toString());
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
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