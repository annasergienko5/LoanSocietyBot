package me.staff4.GringottsTool.MessageHadler.Commands;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.MessageCommandExecutorResponder;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.PublicMessageCommandExecutor;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class HelpForPublic extends AbsGetCommand implements PublicMessageCommandExecutor {
    @Override
    public MessageCommand command() {
        return MessageCommand.HELP;
    }

    @Override
    public void execute(final MessageCommandExecutorResponder responder, final IncomingMessage incomingMessage)
            throws NoDataFound, IOException {
        if (!getRepository().isPartner(incomingMessage.getUserTgId())) {
            return;
        }
        OutgoingMessage outgoingMessage = getOutMessage(incomingMessage);
        outgoingMessage.setText(getHelpFileReader().read(Constants.HELP_PUBLIC_CHAT));
        responder.put(outgoingMessage);
    }
}
