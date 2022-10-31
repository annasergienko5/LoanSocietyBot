package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.Entities.Partner;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.*;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import me.staff4.GringottsTool.Templates.TemplateEngine;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public final class OverdueDebtors extends AbsGetCommand implements SystemMessageCommandExecutor,
        AdminMessageCommandExecutor, PublicMessageCommandExecutor, PrivateMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.OVERDUE_DEBTORS;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws InvalidDataException, NoDataFound, IOException, ParseException {
        boolean isScheduled = incomingMessage.getUserTgId() == 0;
        List<Partner> debts = getRepository().getDebtors();
        if (debts.size() == 0 && !isScheduled) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        if (debts.size() != 0) {
            String debtorsString = getStringAboutOverdueDebtors(debts);
            OutgoingMessage sendMessage = new OutgoingMessage(OutgoingMessageType.TEXT, incomingMessage.getChatId(),
                    debtorsString);
            sendMessage.setEnableMarkdown(true);
            responder.put(sendMessage);
        }
    }

    private String getStringAboutOverdueDebtors(final List<Partner> debts) throws ParseException {
        List<Partner> overdueDebtor = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        for (Partner partner : debts) {
            Date date = dateFormat.parse(partner.getReturnDate());
            if (date.getTime() <= dateNow.getTime()) {
                overdueDebtor.add(partner);
            }
        }
        result.append(Constants.OVERDUE);
        for (Partner partner : overdueDebtor) {
            result.append(TemplateEngine.overdueDebtors(partner.getName(), partner.getDebt(), partner.getReturnDate()));
        }
        return result.toString();
    }
}
