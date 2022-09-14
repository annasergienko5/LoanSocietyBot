package me.staff4.GringottsTool.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OutgoingMessage {
    @With
    private String chatId;
    private String text;
    private int replyToMessageId;
    private String parseMode;
    private boolean enableMarkdown = false;

    public OutgoingMessage(final String chatId, final String text) {
        this.chatId = chatId;
        this.text = text;
    }
}
