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
    @Builder.Default
    private boolean enableMarkdown = false;
    @Builder.Default
    private boolean hasDocument = false;
    private String documentFilePath;
    public OutgoingMessage(final String chatId, final String text) {
        this.chatId = chatId;
        this.text = text;
    }

    public OutgoingMessage(final String chatId, final String text, final int replyToMessageId) {
        this.chatId = chatId;
        this.text = text;
        this.replyToMessageId = replyToMessageId;
    }

    public final void setDocumentFilePath(final String documentFilePath) {
        if (documentFilePath != null && !documentFilePath.isEmpty()) {
            this.documentFilePath = documentFilePath;
            this.hasDocument = true;
        }
    }
}
