package me.staff4.GringottsTool.MessageHadler;

import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.IncomingMessageType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefSelectCommand implements SelectCommand {
    @Override
    public final Optional<MessageCommand> getCommand(final IncomingMessage incomingMessage) {
        if (incomingMessage.getType() == IncomingMessageType.POLL) {
            return Optional.of(MessageCommand.POLL);
        }
        String[] inputTextWithout = incomingMessage.getText().split("@", 2);
        String[] inputTextCommand = inputTextWithout[0].split(" ", 2);
        MessageCommand result = switch (inputTextCommand[0]) {
            case "/version" -> MessageCommand.VERSION;
            case "/id" -> MessageCommand.ID;
            case "/start" -> MessageCommand.START;
            case "/help" -> MessageCommand.HELP;
            case "getTodayDebtors" -> MessageCommand.TODAY_DEBTORS;
            case "/search" -> MessageCommand.SEARCH;
            case "/status" -> MessageCommand.STATUS;
            case "/debts" -> MessageCommand.DEBTS;
            case "/cards" -> MessageCommand.CARDS;
            case "/rules" -> MessageCommand.RULES;
            case "/aboutme" -> MessageCommand.ABOUTME;
            case "/aboutmypayment" -> MessageCommand.ABOUTMYPAYMENTS;
            case "/ducklist" -> MessageCommand.DUCKLIST;
            case "/proxy" -> MessageCommand.PROXY;
            case "/fast" -> MessageCommand.FAST;
            case "/queue" -> MessageCommand.QUEUE;
            case "/credithistory", "/credithistoryfull" -> MessageCommand.CREDITHISTORY;
            case "/fullsearch" -> MessageCommand.FULLSEARCH;
            case "/loan" -> MessageCommand.LOAN;
            case "/overduedebts" -> MessageCommand.OVERDUE_DEBTORS;
            default -> null;
        };
        return Optional.ofNullable(result);
    }
}
