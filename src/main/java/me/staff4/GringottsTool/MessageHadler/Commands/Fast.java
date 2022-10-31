package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.Entities.Partner;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PublicMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import me.staff4.GringottsTool.Templates.TemplateEngine;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.List;

@Component
public final class Fast extends AbsGetCommand implements PublicMessageCommandExecutor, PrivateMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.FAST;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws InvalidDataException, NoDataFound, IOException, ParseException, GeneralSecurityException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        float sum;
        String chatId = incomingMessage.getChatId();
        long userTgId = incomingMessage.getUserTgId();
        String[] inputTextWithout = incomingMessage.getText().split("@", 2);
        String[] inputTextAll = inputTextWithout[0].split(" ", 2);
        if (inputTextAll.length < 2) {
            throw new NoDataFound(Constants.NO_AMOUNT_OF_MONEY);
        }
        String inputText = inputTextAll[1];
        try {
            sum = Float.parseFloat(inputText);
        } catch (NumberFormatException e) {
            responder.put(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, Constants.INCORRECT_MONEY_TYPE));
            return;
        }
        if (sum <= 0) {
            responder.put(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, Constants.INCORRECT_MONEY_TYPE));
            return;
        }
        List<Partner> resultList = getRepository().getPartners(String.valueOf(userTgId));
        if (resultList.isEmpty()) {
            responder.put(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, Constants.NOT_FOUND_DATA));
            return;
        }
        String info = getRepository().getInfo().toString();
        String decision;
        if ((double) sum > resultList.get(0).getSumContributions() * Constants.MINIMUM_LOAN_COEFFICIENT) {
            decision = Constants.LOAN_DENIED;
        } else {
            decision = Constants.LOAN_APPROVED + inputText;
            StringBuilder messageToAdmins = new StringBuilder();
            messageToAdmins.append(TemplateEngine.fastMessageToAdmins(String.valueOf(userTgId), sum));
            messageToAdmins.append("\n\n" + resultList.get(0).toString() + "\n" + info);
            OutgoingMessage sendMessageToAdmins = new OutgoingMessage(OutgoingMessageType.TEXT, Constants.ADMIN_CHAT_ID,
                    messageToAdmins.toString());
            sendMessageToAdmins.setEnableMarkdown(true);
            responder.put(sendMessageToAdmins);
        }
        StringBuilder answer = new StringBuilder(decision);
        answer.append("\n\n" + resultList.get(0).toString());
        answer.append("\n" + info);
        OutgoingMessage sendMessage = new OutgoingMessage(OutgoingMessageType.TEXT, chatId, answer.toString());
        sendMessage.setEnableMarkdown(true);
        responder.put(sendMessage);
    }
}
