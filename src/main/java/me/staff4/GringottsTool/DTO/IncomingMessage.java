package me.staff4.GringottsTool.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IncomingMessage {
    private String chatId;
    private long userTgId;
    private String messageText;
    private int messageId;

    public IncomingMessage(final String chatId, final String messageText) {
        this.chatId = chatId;
        this.messageText = messageText;
    }
}
