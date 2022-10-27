package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.Enteties.Partner;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.SystemMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.io.IOException;
import java.util.List;
@Component
public final class TodayDebtors extends AbsGetCommand implements SystemMessageCommandExecutor {

    @Override
    public MessageCommand command() {
        return MessageCommand.TODAY_DEBTORS;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws InvalidDataException, NoDataFound, IOException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        List<Partner> persons = getRepository().getTodayDebtors();
        String text;
        if (persons.size() != 0) {
            text = String.format(Constants.TODAY_DEBTS_MESSAGE, getStringAboutTodayDebts(persons));
        } else {
            return;
        }
        OutgoingMessage outgoingMessage = getOutMessage(incomingMessage);
        outgoingMessage.setText(text);
        outgoingMessage.setParseMode(ParseMode.HTML);
        outgoingMessage.setEnableMarkdown(false);
        responder.put(outgoingMessage);
    }

    private String getStringAboutTodayDebts(final List<Partner> debts) {
        StringBuilder result = new StringBuilder();
        for (Partner partner : debts) {
            String text = String.format(Constants.SIMPLE_DEBTS, Long.parseLong(partner.getTgId()), partner.getName(),
                    partner.getDebt(), partner.getReturnDate());
            result.append(text);
        }
        return result.toString();
    }
}
