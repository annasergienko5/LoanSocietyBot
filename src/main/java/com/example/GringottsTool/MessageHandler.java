package com.example.GringottsTool;

import com.example.GringottsTool.Enteties.Cards;
import com.example.GringottsTool.Enteties.Contributions;
import com.example.GringottsTool.Enteties.Partner;
import com.example.GringottsTool.Enteties.QueueItem;
import com.example.GringottsTool.Enteties.*;
import com.example.GringottsTool.Exeptions.InvalidDataException;
import com.example.GringottsTool.Exeptions.NoDataFound;
import com.example.GringottsTool.Repository.Repository;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


@Component
public class MessageHandler {

    Logger log = LogManager.getLogger();
    @Autowired
    Repository repository;

    public BotApiMethod<?> answerMessage(Message message) throws GeneralSecurityException, IOException, ParseException, NoDataFound, NumberFormatException, InvalidDataException {
        String chatId = message.getChatId().toString();
        long userTgId = message.getFrom().getId();
        log.info("\nReceived message. Chat ID: " + chatId +"\nTelegramm-user ID: " + userTgId );
        String[] inputTextWithoutName = message.getText().split("@", 2);
        String[] inputText = inputTextWithoutName[0].split(" ", 2);
        if (chatId.equals(Constants.PUBLIC_CHAT_ID)) {
            return publicChat(inputText[0], chatId, inputText,userTgId);
        } else if (chatId.equals(Constants.ADMIN_CHAT_ID)) {
            return adminChat(inputText, chatId, userTgId);
        }else if (repository.isPartner(chatId)){
            return privateChat(inputText, chatId, userTgId);
        }
        return null;
    }

    private BotApiMethod<?> privateChat(String[] inputText, String chatId, long userTgId) throws NoDataFound, IOException, ParseException, NumberFormatException, InvalidDataException {
        switch (inputText[0]) {
            case "/start":
                return getStartMessage(chatId);
            case "/id":
                return getId(chatId);
            case "/help":
                return getHelp(chatId,Constants.HELP_PRIVAT_CHAT);
            case "/search":
                if (inputText.length < 2) {
                    return new SendMessage(chatId, Constants.NOT_PARAMETERS);
                }
                return getSearch(chatId, inputText[1]);
            case "/status":
                return getStatus(chatId);
            case "/debts":
                return getDebtors(chatId);
            case "/cards":
                return getCards(chatId);
            case "/rules":
                return getRules(chatId);
            case "/aboutme":
                return getAboutme(chatId, userTgId);
            case "/aboutmypayment":
                return getAboutMyPayment(chatId, userTgId);
            case "/ducklist":
                return getDucklist(chatId);
            case "/proxy":
                return getProxy(chatId);
            case "/queue":
                return getQueue(chatId);
            case "/credithistory":
                return getCreditHistory(chatId, userTgId, false);
            case "/credithistoryfull":
                return getCreditHistory(chatId, userTgId, true);
        }
        return null;
    }

    private BotApiMethod<?> adminChat(String[] inputText, String chatId, long userTgId) throws NoDataFound, IOException, ParseException, InvalidDataException, NumberFormatException{
        switch (inputText[0]) {
            case "/start":
                return getStartMessage(chatId);
            case "/id":
                return getId(chatId);
            case "/help":
                return getHelp(chatId,Constants.HELP_ADMIN_CHAT);
            case "/search":
                if (inputText.length < 2) {
                    return new SendMessage(chatId, Constants.NOT_PARAMETERS);
                }
                return getSearch(chatId, inputText[1]);
            case "/status":
                return getStatus(chatId);
            case "/debts":
                return getDebtors(chatId);
            case "/cards":
                return getCards(chatId);
            case "/rules":
                return getRules(chatId);
            case "/aboutme":
                return getAboutme(chatId, userTgId);
            case "/aboutmypayment":
                return getAboutMyPayment(chatId, userTgId);
            case "/ducklist":
                return getDucklist(chatId);
            case "/proxy":
                return getProxy(chatId);
            case "/newloan":
                if (inputText.length < 3 ) {
                    return new SendMessage(chatId, Constants.NOT_MONEY);
                }
                return addNewLoan(chatId ,inputText[1], inputText[2]);
            case "/queue":
                return getQueue(chatId);
        }
        return null;
    }

    private BotApiMethod<?> publicChat(String s, String chatId, String[] inputText, long userTgId) throws NoDataFound, IOException, ParseException {
        return switch (s) {
            case "/id" -> getId(chatId);
            case "/help" -> getHelp(chatId, Constants.HELP_PUBLIC_CHAT);
            case "/status" -> getStatus(chatId);
            case "/debts" -> getDebtors(chatId);
            case "/cards" -> getCards(chatId);
            case "/rules" -> getRules(chatId);
            case "/fast" -> getFast(chatId, userTgId, inputText);
            default -> null;
        };
    }

    private BotApiMethod<?> getQueue(String chatId) throws NoDataFound, IOException, InvalidDataException, NumberFormatException {
        StringBuffer result = new StringBuffer();
        Queue<QueueItem> queue = repository.getQueue();
        int count = 1;
        for (QueueItem q : queue){
            String str = String.format("%d. %s - %d\n", count++, q.getName(), q.getSum());
            result.append(str);
        }
        return new SendMessage(chatId, result.toString());
    }

    private BotApiMethod<?> addNewLoan(String chatId, String tableId, String sumString) throws NoDataFound, IOException, InvalidDataException {
        int sum = Integer.parseInt(sumString);
            return new SendMessage(chatId, repository.addQueueItem(tableId, sum));
    }

    private BotApiMethod<?> getId(String chatId) {
        return new SendMessage(chatId, chatId);
    }

    private BotApiMethod<?> getHelp(String chatId, String help) {
        return new SendMessage(chatId, help);
    }

    private BotApiMethod<?> getRules(String chatId) {
        return new SendMessage(chatId, Constants.RULE);
    }

    private BotApiMethod<?> getDucklist(String chatId) throws IOException, NoDataFound {
        List<Partner> elitePartners = repository.getDuckList();
        StringBuffer result = new StringBuffer();
        if (elitePartners.size() == 0) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        result.append("Уважаемые люди, которые делали взносы за последние 3 месяца:\n");
        for (Partner partner : elitePartners) {
            result.append("<strong>" + partner.getName() + "</strong>\n");
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(result.toString());
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    private BotApiMethod<?> getAboutMyPayment(String chatId, long userTgId) throws IOException, NoDataFound {
        Partner partner = repository.getPartnerByTgId(String.valueOf(userTgId));
        Contributions contributions = repository.getContribution(partner);
        if (contributions != null) {
                return new SendMessage(chatId, contributions.toString());
            }
        throw new NoDataFound(Constants.NOT_FOUND_DATA);
    }

    private BotApiMethod<?> getAboutme(String chatId, long userTgId) throws IOException, NoDataFound {
        List<Partner> resultList = repository.getPartners(String.valueOf(userTgId));
        if (resultList.isEmpty()) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        } else {
            return new SendMessage(chatId, resultList.get(0).toString());
        }
    }

    private BotApiMethod<?> getCards(String chatId) throws IOException, NoDataFound {
        StringBuffer res = new StringBuffer();
        List<Cards> cards = repository.getCards();
        if (cards.isEmpty()){
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        for (Cards card : cards) {
            res.append("\n").append(card.toString());
        }
        return new SendMessage(chatId, res.toString());
    }

    public BotApiMethod<?> getDebtors(String chatId) throws IOException, ParseException, NoDataFound {
        StringBuffer result = new StringBuffer();
        List<List<Partner>> debts = repository.getDebtors();
        if (debts.size() == 0) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        result.append("*Просрочено:*\n\n");
        for (Partner partner : debts.get(0)) {
            result.append("*" + partner.getName() + "*\n")
                    .append(partner.getDebt() + "₽\n")
                    .append("до: " + partner.getReturnDate() + "\n\n");
        }
        result.append("----------\n\n")
                .append("*Должники:*\n\n");
        for (Partner partner : debts.get(1)) {
            Contributions.Contribution lastContr = repository.getLastContribution(partner);
            result.append("*" + partner.getName() + "*\n")
                    .append(partner.getDebt() + "₽\n")
                    .append("до: " + partner.getReturnDate() + "\n")
                    .append("последний платёж: " + lastContr.getDate() + "\n\n");
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setText(result.toString());
        sendMessage.setChatId(chatId);

        return sendMessage;
    }

    private BotApiMethod<?> getStatus(String chatId) throws IOException, NoDataFound {
        String result = repository.getInfo().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(result);
        return sendMessage;
    }

    private BotApiMethod<?> getSearch(String chatId, String nameOrTgId) throws IOException, NoDataFound {
        List<Partner> resultList = repository.getPartners(nameOrTgId);
        SendMessage sendMessage;
        if (resultList.size() == 0) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        if (resultList.size() > 1) {
            sendMessage = new SendMessage(chatId, Constants.FIND_MORE_RESULT);
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

    private BotApiMethod<?> getFast(String chatId, long userTgId, String[] inputTextAll) throws NoDataFound, IOException {
        int sum;
        if (inputTextAll.length < 2) {
            throw new NoDataFound(Constants.NO_AMOUNT_OF_MONEY);
        }
        String inputText = inputTextAll[1];
        try {
            sum = Integer.parseInt(inputText);
        } catch (NumberFormatException e){
            return new SendMessage(chatId, Constants.INCORRECT_MONEY_TYPE);
        }
        if (sum <= 0){
            return new SendMessage(chatId, Constants.INCORRECT_AMOUNT_OF_MONEY);
        }
        List<Partner> resultList = repository.getPartners(String.valueOf(userTgId));
        if (resultList.isEmpty()) {
            return new SendMessage(chatId, Constants.NOT_FOUND_DATA);
        }
        String decision;
        if ((double) sum > resultList.get(0).getSumContributions()){
            decision = Constants.LOAN_DENIED;
        } else {
            decision = Constants.LOAN_APPROVED + inputText;
        }
        String info = repository.getInfo().toString();
        StringBuilder answer = new StringBuilder(decision);
        answer.append("\n\n" + resultList.get(0).toString());
        answer.append("\n" + info);
        SendMessage sendMessage = new SendMessage(chatId, answer.toString());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }

    private BotApiMethod<?> getProxy(String chatId) throws NoDataFound, IOException {
        List<String> proxyList = repository.getProxy();
        StringBuilder result = new StringBuilder("Наши прокси-серверы:");
        int i = 1;
        for (String proxy : proxyList){
            String inlineUrl = String.format("\n[сервер %d](%s)", i, proxy);
            result.append(inlineUrl);
            i++;
        }
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableMarkdown(true);
        message.setText(result.toString());
        return message;
    }

    private BotApiMethod<?> getCreditHistory(String chatId, long tgId, boolean full) throws NoDataFound, IOException, NumberFormatException, InvalidDataException {
        log.info("\n getCreditHistory STARTED from chatId: " + chatId + "\n From user with tgId: " + tgId + "\n Is full CreditHistory?: " + full);
        Partner partner = repository.getPartnerByTgId(String.valueOf(tgId));
        List<Transaction> transactions;
        if (partner.getTableId() == 0) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        } else {
            transactions = repository.getTransactions(partner);
        }
        if (transactions.size() == 0) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        CreditHistory creditHistory = new CreditHistory(transactions);
        String creditString;
        if (full) {
            creditString = creditHistory.fullString();
        } else {
            creditString = creditHistory.partialString();
        }
        String message = String.format(Constants.ABOUT_CREDIT_HISTORY_MESSAGE, partner.getName(), creditString);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        return sendMessage;
    }
    public String getTodayDebtors() throws NoDataFound, IOException {
        List<Partner> persons =  repository.getTodayDebtors();
        String text = null;
        if (persons.size() != 0) {
            text = String.format(Constants.TODAY_DEBTS_MESSAGE, getStringAboutTodayDebts(persons));
        } 
        return text;
    }
    private String getStringAboutTodayDebts(List<Partner> debts) {
        StringBuilder result = new StringBuilder();
        for (Partner partner : debts) {
            String text = String.format(Constants.SIMPLE_DEBTS, partner.getName(), partner.getDebt(), partner.getReturnDate());
            result.append(text);
        }
        return result.toString();
    }
    public String getDebtors() throws GeneralSecurityException, IOException, NoDataFound, ParseException {
        List<List<Partner>> debtors = repository.getDebtors();
        String text = null;
        if (debtors.size() != 0) {
            text = String.format(Constants.ABOUT_DEBTS_MESSAGE, getStringAboutArrearsDebts(debtors), getStringAboutSimpleDebts(debtors));
        } else {
            throw new NoDataFound("No debtors found.");
        }
        return text;
    }
    private String getStringAboutArrearsDebts(List<List<Partner>> debts) {
        StringBuilder result = new StringBuilder();
        for (Partner partner : debts.get(0)) {
            String text = String.format(Constants.ARREARS_DEBTS, partner.getName(), partner.getDebt(), partner.getReturnDate());
            result.append(text);
        }
        return result.toString();
    }
        private String getStringAboutSimpleDebts(List<List<Partner>> debts) {
        StringBuilder result = new StringBuilder();
        for (Partner partner : debts.get(1)) {
            String text = String.format(Constants.SIMPLE_DEBTS, partner.getName(), partner.getDebt(), partner.getReturnDate());
            result.append(text);
        }
        return result.toString();
    }
}