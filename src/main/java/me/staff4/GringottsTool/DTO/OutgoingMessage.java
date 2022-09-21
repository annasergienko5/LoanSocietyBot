package me.staff4.GringottsTool.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

import java.util.Optional;

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
    @Builder.Default
    private OutgoingMessageType type = OutgoingMessageType.UNKNOWN;
    private String messageMeta;
    public OutgoingMessage(final OutgoingMessageType type, final String chatId, final String text) {
        this.type = type;
        this.chatId = chatId;
        this.text = text;
    }

    public OutgoingMessage(final OutgoingMessageType type, final String chatId, final String text,
                           final int replyToMessageId) {
        this.type = type;
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

    public Optional<String> getMessageMeta() {
        if (this.messageMeta == null || this.messageMeta.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.messageMeta);
    }
}
