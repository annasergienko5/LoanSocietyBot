package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.AdminMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PrivateMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public final class Proxy extends AbsGetCommand implements AdminMessageCommandExecutor, PrivateMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.PROXY;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws NoDataFound, IOException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        List<String> proxyList = getRepository().getProxy();
        StringBuilder result = new StringBuilder("Наши прокси-серверы:");
        int i = 1;
        for (String proxy : proxyList) {
            String inlineUrl = String.format("\n[сервер %d](%s)", i, proxy);
            result.append(inlineUrl);
            i++;
        }
        OutgoingMessage message = getOutMessage(incomingMessage);
        message.setText(result.toString());
        message.setEnableMarkdown(true);
        responder.put(message);
    }
}
