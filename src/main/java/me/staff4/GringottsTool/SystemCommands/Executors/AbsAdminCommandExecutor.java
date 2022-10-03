package me.staff4.GringottsTool.SystemCommands.Executors;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.IncomingMessageType;

public abstract class AbsAdminCommandExecutor {
    final void sendToAdminChat(final SystemCommandExecutorResponder responder, final String text) {
        var action = IncomingMessage.builder().
                type(IncomingMessageType.SYSTEM_COMMAND).
                chatId(Constants.ADMIN_CHAT_ID).
                text(text).
                build();

        responder.put(action);
    }
}
