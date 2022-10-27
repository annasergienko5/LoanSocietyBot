package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.Enteties.Contributions;
import me.staff4.GringottsTool.Enteties.Partner;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.*;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Component
public final class Debtors extends AbsGetCommand implements PublicMessageCommandExecutor, AdminMessageCommandExecutor,
        PrivateMessageCommandExecutor, SystemMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.DEBTS;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws InvalidDataException, NoDataFound, IOException, ParseException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        boolean isScheduled = incomingMessage.getUserTgId() == 0;
        List<Partner> debts = getRepository().getDebtors();
        if (debts.size() == 0 && !isScheduled) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        } else if (debts.size() != 0) {
            String debtorsString = getStringAboutAllDebtors(debts);
            OutgoingMessage sendMessage = getOutMessage(incomingMessage);
            sendMessage.setText(debtorsString);
            sendMessage.setEnableMarkdown(true);
            responder.put(sendMessage);
        }
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
        result.append(Constants.OVERDUE);
        for (Partner partner : overdueDebtor) {
            result.append(String.format(Constants.OVERDUE_DEBTORS, partner.getName(), partner.getDebt(),
                    partner.getReturnDate()));
        }
        result.append("----------\n\n")
                .append(Constants.DEBTORS);
        List<Contributions> contributions = getRepository().getContributions();
        for (Partner partner : notOverdueDebtor) {
            Contributions.Contribution lastContr = contributions.get(partner.getTableId() - 2).getPays().get(0);
            result.append(String.format(Constants.NOT_OVERDUE_DEBTORS, partner.getName(), partner.getDebt(),
                    partner.getReturnDate(), lastContr.getDate()));
        }
        return result.toString();
    }
}
