package com.example.GringottsTool;

import com.example.GringottsTool.Enteties.Cards;
import com.example.GringottsTool.Enteties.Contributions;
import com.example.GringottsTool.Enteties.Partner;
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
import java.util.List;


@Component
public class MessageHandler {

    Logger log = LogManager.getLogger();
    @Autowired
    Repository repository;

    public BotApiMethod<?> answerMessage(Message message) throws GeneralSecurityException, IOException, ParseException, NoDataFound {
        String chatId = message.getChatId().toString();
        String userName = message.getChat().getUserName();
        long tgId = message.getChat().getId();
        log.info(userName);
        log.info(tgId);
        if (chatId.equals(Constants.PUBLIC_CHAT_ID)) {
            String[] inputText = message.getText().split("@", 2);
            switch (inputText[0]) {
                case "/id":
                    return getId(chatId);
                case "/help":
                    return getHelpOurChat(chatId);
                case "/status":
                    return getStatus(chatId);
                case "/debts":
                    return getDebts(chatId);
                case "/cards":
                    return getCards(chatId);
                case "/rules":
                    return getRules(chatId);
            }
        } else if (chatId.equals(Constants.ADMIN_CHAT_ID) || repository.isPartner(chatId)) {
            String[] inputText = message.getText().split(" ", 2);
            switch (inputText[0]) {
                case "/start":
                    return getStartMessage(chatId);
                case "/id":
                    return getId(chatId);
                case "/help":
                    return getHelp(chatId);
                case "/search":
                    if (inputText.length < 2) {
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
                case "/proxy":
                    return getProxy(chatId);

            }
        }
        return null;
    }

    private BotApiMethod<?> getId(String chatId) {
        return new SendMessage(chatId, chatId);
    }

    private BotApiMethod<?> getHelp(String chatId) {
        return new SendMessage(chatId, Constants.HELP);
    }

    private BotApiMethod<?> getHelpOurChat(String chatId) {
        return new SendMessage(chatId, Constants.HELP_OUR);
    }

    private BotApiMethod<?> getRules(String chatId) {
        return new SendMessage(chatId, Constants.RULE);
    }

    private BotApiMethod<?> getDucklist(String chatId) throws IOException, NoDataFound {
        ArrayList<Partner> elitePartners = repository.getDuckList();
        StringBuffer result = new StringBuffer();
        if (elitePartners.size() == 0) {
            return new SendMessage(chatId, Constants.NO_DEBTS);
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

    private BotApiMethod<?> getAboutMyPayment(String chatId) throws IOException, NoDataFound {
        String expected = repository.findPartners(chatId).get(0).getName();
        Contributions contributions = repository.findContribution(expected);
        return new SendMessage(chatId, contributions.toString());
    }

    private BotApiMethod<?> getAboutme(String chatId) throws IOException, NoDataFound {
        ArrayList<Partner> resultList = repository.findPartners(chatId);
        SendMessage sendMessage;
        if (resultList.size() == 0) {
            return new SendMessage(chatId, Constants.NOT_FOUND_DATA);
        }
        sendMessage = new SendMessage(chatId, resultList.get(0).toString());
        return sendMessage;
    }

    private BotApiMethod<?> getCards(String chatId) throws IOException, NoDataFound {
        StringBuffer res = new StringBuffer();
        ArrayList<Cards> cards = repository.findCards();
        for (Cards card : cards) {
            res.append("\n").append(card.toString());
        }
        return new SendMessage(chatId, res.toString());
    }

    public BotApiMethod<?> getDebts(String chatId) throws IOException, ParseException, NoDataFound {
        StringBuffer result = new StringBuffer();
        List<List<Partner>> debts = repository.findDebt();

        if (debts.size() == 0) {
            return new SendMessage(chatId, Constants.NO_DEBTS);
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
            Contributions.Contribution lastContr = repository.findLastContribution(partner.getName());
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
        String result = repository.findInfo().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(result);
        return sendMessage;
    }

    private BotApiMethod<?> getSearch(String chatId, String expected) throws IOException, NoDataFound {
        ArrayList<Partner> resultList = repository.findPartners(expected);
        SendMessage sendMessage;
        if (resultList.size() == 0) {
            return new SendMessage(chatId, Constants.NOT_FOUND_DATA);
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

    private BotApiMethod<?> getProxy(String chatId) throws NoDataFound, IOException {
        StringBuilder proxies = repository.findProxy();
        return new SendMessage(chatId, "Наши прокси-серверы:\n" + proxies);
    }
}