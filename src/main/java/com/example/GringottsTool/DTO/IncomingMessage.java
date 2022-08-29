package com.example.GringottsTool.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IncomingMessage {
    private String chatId;
    private long userTgId;
    private String messageText;

    public IncomingMessage(String chatId, String messageText) {
        this.chatId = chatId;
        this.messageText = messageText;
    }
}
