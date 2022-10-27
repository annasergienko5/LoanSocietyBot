package me.staff4.GringottsTool.MessageHadler;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.IncomingMessageType;
import org.springframework.stereotype.Component;

@Component
public class DefSelectTypeChat implements SelectTypeChat {

    @Override
    public final TypeChat selectTypeChat(final IncomingMessage incomingMessage) {
        String chatId = incomingMessage.getChatId();
        long userTgId = incomingMessage.getUserTgId();
        if (userTgId == 0 || incomingMessage.getType() == IncomingMessageType.POLL) {
            return TypeChat.SYSTEM_MESSAGE;
        } else if (chatId.equals(Constants.PUBLIC_CHAT_ID)) {
            return TypeChat.PUBLIC_CHAT;
        } else if (Long.parseLong(chatId) == userTgId) {
            return TypeChat.PRIVAT_CHAT;
        } else if (chatId.equals(Constants.ADMIN_CHAT_ID)) {
            return TypeChat.ADMIN_CHAT;
        } else {
            return TypeChat.ALL_AVAILABLE_CHAT;
        }
    }
}
