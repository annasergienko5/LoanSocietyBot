package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.Entities.Partner;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import me.staff4.GringottsTool.Templates.TemplateEngine;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public final class Loan extends AbsGetCommand implements PrivateMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.LOAN;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws NoDataFound, IOException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        String[] inputTextWithout = incomingMessage.getText().split("@", 2);
        String[] inputTextArray = inputTextWithout[0].split(" ", 2);
        String chatId = incomingMessage.getChatId();
        if (inputTextArray.length < 2) {
            responder.put(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, Constants.NO_MONEY_AND_TERGET_LOAN));
            return;
        }
        String inputText = inputTextArray[1];
        String[] text = inputText.split(" ", 2);
        float money;
        try {
            money = Float.parseFloat(text[0]);
        } catch (NumberFormatException e) {
            responder.put(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, Constants.NO_MONEY_LOAN));
            return;
        }
        int numberOfLoanCommandParameters = 2;
        if (text.length < numberOfLoanCommandParameters) {
            responder.put(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, Constants.NO_TARGET_LOAN));
            return;
        }
        List<String> answers = new ArrayList<>();
        answers.add("За");
        answers.add("Против");
        answers.add("Воздержусь");
        List<Partner> partnerList = getRepository().getPartners(chatId);
        if (partnerList.isEmpty()) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        Partner partner = partnerList.get(0);
        String aboutMe = partner.toString();
        String status = getRepository().getInfo().toString();
        String targetString = String.format("[%s](tg://user?id=%d): ", partner.getName(), Long.parseLong(chatId))
                + text[1];
        OutgoingMessage targetMessage = new OutgoingMessage(OutgoingMessageType.TEXT, Constants.PUBLIC_CHAT_ID,
                targetString);
        targetMessage.setEnableMarkdown(true);
        OutgoingMessage infoMessage = new OutgoingMessage(OutgoingMessageType.TEXT, Constants.PUBLIC_CHAT_ID,
                aboutMe + "\n\n" + status);
        infoMessage.setEnableMarkdown(true);
        responder.put(targetMessage);
        responder.put(infoMessage);
        String pollText = TemplateEngine.pollQuestion(partner.getName(), money);
        OutgoingMessage pollMessage = new OutgoingMessage(OutgoingMessageType.POLL, Constants.PUBLIC_CHAT_ID, pollText);
        pollMessage.setUserTgId(chatId);
        pollMessage.setOptions(answers);
        responder.put(pollMessage);
    }
}
