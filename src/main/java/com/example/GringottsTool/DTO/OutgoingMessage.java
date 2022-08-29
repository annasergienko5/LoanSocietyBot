package com.example.GringottsTool.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutgoingMessage {
    String chatId;
    String text;
    String parseMode;
    boolean enableMarkdown = false;

    public OutgoingMessage(String chatId, String text) {
        this.chatId = chatId;
        this.text = text;
    }

}
