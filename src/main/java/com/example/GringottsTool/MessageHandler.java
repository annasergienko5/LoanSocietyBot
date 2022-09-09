package com.example.GringottsTool;

import com.example.GringottsTool.DTO.IncomingMessage;
import com.example.GringottsTool.DTO.OutgoingMessage;

import com.example.GringottsTool.Enteties.Cards;
import com.example.GringottsTool.Enteties.Contributions;
import com.example.GringottsTool.Enteties.CreditHistory;
import com.example.GringottsTool.Enteties.Partner;
import com.example.GringottsTool.Enteties.Transaction;
import com.example.GringottsTool.Enteties.QueueItem;
import com.example.GringottsTool.Exeptions.InvalidDataException;
import com.example.GringottsTool.Exeptions.NoDataFound;
import com.example.GringottsTool.Repository.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

@Component
public class MessageHandler implements Runnable {

    private Logger log = LogManager.getLogger();
    @Autowired
    private Repository repository;
    private final BlockingQueue<IncomingMessage> inQueue;
    private final BlockingQueue<OutgoingMessage> outQueue;
    private final int numberOfNewloanCommandParameters = 3;

    public MessageHandler(final BlockingQueue<IncomingMessage> inQueue, final BlockingQueue<OutgoingMessage> outQueue) {
        this.inQueue = inQueue;
        this.outQueue = outQueue;
    }

    private OutgoingMessage answerMessage(final IncomingMessage message)
            throws GeneralSecurityException, IOException, ParseException, NoDataFound, NumberFormatException,
            InvalidDataException {
        String chatId = message.getChatId();
        long userTgId = message.getUserTgId();
        log.info("\nReceived message. Chat ID: " + chatId + "\nTelegramm-user ID: " + userTgId);
        String[] inputTextWithout = message.getMessageText().split("@", 2);
        String[] inputText = inputTextWithout[0].split(" ", 2);
        if (inputText[0].equals("/id")) {
            return getId(chatId);
        }
        if (userTgId == 0) {
            systemMessage(inputText[0], chatId);
            return null;
        }
            if (repository.isPartner(userTgId)) {
                if (chatId.equals(Constants.PUBLIC_CHAT_ID)) {
                    return publicChat(inputText[0], chatId, inputText, userTgId);
                } else if (chatId.equals(Constants.ADMIN_CHAT_ID)) {
                    return adminChat(inputText, chatId, userTgId);
                } else if (Long.parseLong(chatId) == userTgId) {
                    return privateChat(inputText, chatId, userTgId);
                }
            } else {
                throw new NoDataFound(Constants.NOT_PARTNER);
            }
        return null;
    }

    private void systemMessage(final String inputText, final String chatId) throws NoDataFound, IOException,
            ParseException {
        OutgoingMessage message = null;
        if (inputText.equals("getTodayDebtors")) {
            message = getTodayDebtors(chatId);
        } else if (inputText.equals("getDebtors")) {
            message = getDebtors(chatId, true);
        } else {
            throw new IOException("Problems with scheduled notifications. "
                    + "systemMessage() received wrong inputText.");
        }
        if (message != null) {
            sendToChats(message);
        }
    }

    private void sendToChats(final OutgoingMessage message) {
        if (!message.isEnableMarkdown()) {
            message.setParseMode(ParseMode.HTML);
        }
        putToOutQueue(message);
        putToOutQueue(message.withChatId(Constants.PUBLIC_CHAT_ID));
    }

    private OutgoingMessage privateChat(final String[] inputText, final String chatId, final long userTgId)
            throws NoDataFound, IOException, ParseException, NumberFormatException, InvalidDataException {
        switch (inputText[0]) {
            case "/version":
                return getVersion(chatId);
            case "/start":
                return getStartMessage(chatId);
            case "/help":
                return getHelp(chatId, Constants.HELP_PRIVAT_CHAT);
            case "/search":
                if (inputText.length < 2) {
                    return new OutgoingMessage(chatId, Constants.NOT_PARAMETERS);
                }
                return getSearch(chatId, inputText[1]);
            case "/status":
                return getStatus(chatId);
            case "/debts":
                return getDebtors(chatId, false);
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
            default:
                return null;
        }
    }


    private OutgoingMessage adminChat(final String[] inputText, final String chatId, final long userTgId)
            throws NoDataFound, IOException, ParseException, InvalidDataException, NumberFormatException {
        switch (inputText[0]) {
            case "/version":
                return getVersion(chatId);
            case "/start":
                return getStartMessage(chatId);
            case "/help":
                return getHelp(chatId, Constants.HELP_ADMIN_CHAT);
            case "/search":
                if (inputText.length < 2) {
                    return new OutgoingMessage(chatId, Constants.NOT_PARAMETERS);
                }
                return getSearch(chatId, inputText[1]);
            case "/status":
                return getStatus(chatId);
            case "/debts":
                return getDebtors(chatId, false);
            case "/cards":
                return getCards(chatId);
            case "/rules":
                return getRules(chatId);
            case "/ducklist":
                return getDucklist(chatId);
            case "/proxy":
                return getProxy(chatId);
            case "/newloan":
                if (inputText.length < numberOfNewloanCommandParameters) {
                    return new OutgoingMessage(chatId, Constants.NOT_MONEY);
                }
                return addNewLoan(chatId, inputText[1], inputText[2]);
            case "/queue":
                return getQueue(chatId);
            default:
                return null;
        }
    }

    private OutgoingMessage publicChat(
            final String s, final String chatId, final String[] inputText, final long userTgId)
            throws NoDataFound, IOException, ParseException {
        return switch (s) {
            case "/help" -> getHelp(chatId, Constants.HELP_PUBLIC_CHAT);
            case "/status" -> getStatus(chatId);
            case "/debts" -> getDebtors(chatId, false);
            case "/cards" -> getCards(chatId);
            case "/rules" -> getRules(chatId);
            case "/fast" -> getFast(chatId, userTgId, inputText);
            default -> null;
        };
    }

    private OutgoingMessage getVersion(final String chatId) {
        return new OutgoingMessage(chatId, Constants.VERSION);
    }

    private OutgoingMessage getQueue(final String chatId)
            throws NoDataFound, IOException, InvalidDataException, NumberFormatException {
        StringBuffer result = new StringBuffer();
        Queue<QueueItem> queue = repository.getQueue();
        int count = 1;
        for (QueueItem q : queue) {
            String str = String.format("%d. %s - %d\n", count++, q.getName(), q.getSum());
            result.append(str);
        }
        return new OutgoingMessage(chatId, result.toString());
    }

    private OutgoingMessage addNewLoan(final String chatId, final String tableId, final String sumString)
            throws NoDataFound, IOException, InvalidDataException {
        int sum = Integer.parseInt(sumString);
            return new OutgoingMessage(chatId, repository.addQueueItem(tableId, sum));
    }

    private OutgoingMessage getId(final String chatId) {
        return new OutgoingMessage(chatId, chatId);
    }

    private OutgoingMessage getHelp(final String chatId, final String help) {
        return new OutgoingMessage(chatId, help);
    }

    private OutgoingMessage getRules(final String chatId) {
        return new OutgoingMessage(chatId, Constants.RULE);
    }

    private OutgoingMessage getDucklist(final String chatId) throws IOException, NoDataFound {
        List<Partner> elitePartners = repository.getDuckList();
        StringBuffer result = new StringBuffer();
        if (elitePartners.size() == 0) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        result.append("Уважаемые люди, которые делали взносы за последние 3 месяца:\n");
        for (Partner partner : elitePartners) {
            result.append("<strong>" + partner.getName() + "</strong>\n");
        }
        OutgoingMessage sendMessage = new OutgoingMessage(chatId, result.toString());
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    private OutgoingMessage getAboutMyPayment(final String chatId, final long userTgId)
            throws IOException, NoDataFound {
        Partner partner = repository.getPartnerByTgId(String.valueOf(userTgId));
        Contributions contributions = repository.getContributions().get(partner.getTableId() - 2);
        if (contributions != null) {
                return new OutgoingMessage(chatId, contributions.toString());
            }
        throw new NoDataFound(Constants.NOT_FOUND_DATA);
    }

    private OutgoingMessage getAboutme(final String chatId, final long userTgId) throws IOException, NoDataFound {
        List<Partner> resultList = repository.getPartners(String.valueOf(userTgId));
        if (resultList.isEmpty()) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        } else {
            return new OutgoingMessage(chatId, resultList.get(0).toString());
        }
    }

    private OutgoingMessage getCards(final String chatId) throws IOException, NoDataFound {
        StringBuffer res = new StringBuffer();
        List<Cards> cards = repository.getCards();
        if (cards.isEmpty()) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        for (Cards card : cards) {
            res.append("\n").append(card.toString());
        }
        OutgoingMessage outgoingMessage = new OutgoingMessage(chatId, res.toString());
        outgoingMessage.setEnableMarkdown(true);
        return outgoingMessage;
    }

    private OutgoingMessage getDebtors(final String chatId, final boolean isScheduled)
            throws IOException, ParseException, NoDataFound {
        List<Partner> debts = repository.getDebtors();
        if (debts.size() == 0 && !isScheduled) {
                throw new NoDataFound(Constants.NOT_FOUND_DATA);
        } else if (debts.size() != 0) {
            String debtorsString = getStringAboutAllDebtors(debts);
            OutgoingMessage sendMessage = new OutgoingMessage(chatId, debtorsString);
            sendMessage.setEnableMarkdown(true);
            return sendMessage;
        }
        return null;
    }

    private String getStringAboutAllDebtors(final List<Partner> debts) throws ParseException, NoDataFound, IOException {
        List<Partner> overdueDebtor = new ArrayList<>();
        List<Partner> notOverdueDebtor = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        for (Partner partner : debts) {
            Date date = dateFormat.parse(partner.getReturnDate());
            if (date.getTime() <= dateNow.getTime()) {
                overdueDebtor.add(partner);
            }
            notOverdueDebtor.add(partner);
        }
        result.append("*Просрочено:*\n\n");
        for (Partner partner : overdueDebtor) {
            result.append(String.format(Constants.OVERDUE_DEBTORS, partner.getName(), partner.getDebt(),
                    partner.getReturnDate()));
        }
        result.append("----------\n\n")
                .append("*Должники:*\n\n");
        List<Contributions> contributions = repository.getContributions();
        for (Partner partner : notOverdueDebtor) {
            Contributions.Contribution lastContr = contributions.get(partner.getTableId() - 2).getPays().get(0);
            result.append(String.format(Constants.NOT_OVERDUE_DEBTORS, partner.getName(), partner.getDebt(),
                    partner.getReturnDate(), lastContr.getDate()));
        }
        return result.toString();
    }

    private OutgoingMessage getStatus(final String chatId) throws IOException, NoDataFound {
        String result = repository.getInfo().toString();
        OutgoingMessage sendMessage = new OutgoingMessage(chatId, result);
        sendMessage.setEnableMarkdown(true);
        return sendMessage;
    }

    private OutgoingMessage getSearch(final String chatId, final String nameOrTgId) throws IOException, NoDataFound {
        List<Partner> resultList = repository.getPartners(nameOrTgId);
        OutgoingMessage sendMessage;
        if (resultList.size() == 0) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        if (resultList.size() > 1) {
            sendMessage = new OutgoingMessage(chatId, Constants.FIND_MORE_RESULT);
            return sendMessage;
        }
        sendMessage = new OutgoingMessage(chatId, resultList.get(0).toString());
        return sendMessage;
    }

    private OutgoingMessage getStartMessage(final String chatId) {
        OutgoingMessage sendMessage = new OutgoingMessage(chatId, "Привет");
        sendMessage.setEnableMarkdown(true);
        return sendMessage;
    }

    private OutgoingMessage getFast(final String chatId, final long userTgId, final String[] inputTextAll)
            throws NoDataFound, IOException {
        int sum;
        if (inputTextAll.length < 2) {
            throw new NoDataFound(Constants.NO_AMOUNT_OF_MONEY);
        }
        String inputText = inputTextAll[1];
        try {
            sum = Integer.parseInt(inputText);
        } catch (NumberFormatException e) {
            return new OutgoingMessage(chatId, Constants.INCORRECT_MONEY_TYPE);
        }
        if (sum <= 0) {
            return new OutgoingMessage(chatId, Constants.INCORRECT_AMOUNT_OF_MONEY);
        }
        List<Partner> resultList = repository.getPartners(String.valueOf(userTgId));
        if (resultList.isEmpty()) {
            return new OutgoingMessage(chatId, Constants.NOT_FOUND_DATA);
        }
        String decision;
        if ((double) sum > resultList.get(0).getSumContributions()) {
            decision = Constants.LOAN_DENIED;
        } else {
            decision = Constants.LOAN_APPROVED + inputText;
        }
        String info = repository.getInfo().toString();
        StringBuilder answer = new StringBuilder(decision);
        answer.append("\n\n" + resultList.get(0).toString());
        answer.append("\n" + info);
        OutgoingMessage sendMessage = new OutgoingMessage(chatId, answer.toString());
        sendMessage.setEnableMarkdown(true);
        return sendMessage;
    }

    private OutgoingMessage getProxy(final String chatId) throws NoDataFound, IOException {
        List<String> proxyList = repository.getProxy();
        StringBuilder result = new StringBuilder("Наши прокси-серверы:");
        int i = 1;
        for (String proxy : proxyList) {
            String inlineUrl = String.format("\n[сервер %d](%s)", i, proxy);
            result.append(inlineUrl);
            i++;
        }
        OutgoingMessage message = new OutgoingMessage(chatId, result.toString());
        message.setEnableMarkdown(true);
        return message;
    }

    private OutgoingMessage getCreditHistory(final String chatId, final long tgId, final boolean full)
            throws NoDataFound, IOException, NumberFormatException, InvalidDataException {
        log.info("\n getCreditHistory STARTED from chatId: " + chatId + "\n From user with tgId: " + tgId
                + "\n Is full CreditHistory?: " + full);
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
        OutgoingMessage sendMessage = new OutgoingMessage(chatId, message);
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    @Override
    public final void run() {
        while (true) {
            String errorMessage = null;
            String chatId = null;
            try {
                IncomingMessage incomingMessage = inQueue.take();
                chatId = incomingMessage.getChatId();
                errorMessage = String.format(Constants.ERROR_IN_SOME_FUNCTION, incomingMessage.getMessageText(),
                        chatId, incomingMessage.getUserTgId());
                OutgoingMessage outgoingMessage = answerMessage(incomingMessage);
                if (outgoingMessage != null) {
                    putToOutQueue(outgoingMessage);
                }
            } catch (GeneralSecurityException | IOException e) {
                log.error(errorMessage, e);
                putToOutQueue(new OutgoingMessage(Constants.ADMIN_CHAT_ID, errorMessage));
            } catch (NoDataFound e) {
                log.info(e.getMessage(), e);
                putToOutQueue(new OutgoingMessage(chatId, e.getMessage()));
            } catch (InvalidDataException e) {
                putToOutQueue(new OutgoingMessage(chatId, Constants.INVALID_DATA_IN_CELLS));
                putToOutQueue(new OutgoingMessage(Constants.ADMIN_CHAT_ID, errorMessage
                        + Constants.INVALID_DATA_IN_CELLS_TO_ADMIN + e.toMessage()));
            } catch (NumberFormatException | ParseException e) {
                log.info(e.getMessage(), e);
                putToOutQueue(new OutgoingMessage(chatId, Constants.INVALID_DATA_IN_CELLS));
                putToOutQueue(new OutgoingMessage(Constants.ADMIN_CHAT_ID, errorMessage
                        + Constants.INVALID_DATA_IN_CELLS_TO_ADMIN));
            } catch (InterruptedException e) {
                putToOutQueue(new OutgoingMessage(Constants.ADMIN_CHAT_ID, Constants.ERROR_TAKING_IN_MESSAGEHANDLER));
            }
        }
    }
    private void putToOutQueue(final OutgoingMessage outgoingMessage) {
        try {
            outQueue.put(outgoingMessage);
        } catch (InterruptedException e) {
            log.info(Constants.ERROR_OUT_WRITE_IN_MESSAGEHANDLER);
        }

    }
    private OutgoingMessage getTodayDebtors(final String chatId) throws NoDataFound, IOException {
        List<Partner> persons =  repository.getTodayDebtors();
        String text;
        if (persons.size() != 0) {
            text = String.format(Constants.TODAY_DEBTS_MESSAGE, getStringAboutTodayDebts(persons));
        } else {
            return null;
        }
        OutgoingMessage outgoingMessage = new OutgoingMessage(chatId, text);
        outgoingMessage.setParseMode(ParseMode.HTML);
        outgoingMessage.setEnableMarkdown(false);
        return outgoingMessage;
    }

    private String getStringAboutTodayDebts(final List<Partner> debts) {
        StringBuilder result = new StringBuilder();
        for (Partner partner : debts) {
            String text = String.format(Constants.SIMPLE_DEBTS, partner.getName(), partner.getDebt(),
                    partner.getReturnDate());
            result.append(text);
        }
        return result.toString();
    }
}
