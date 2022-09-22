package me.staff4.GringottsTool.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OutgoingMessage {
    @With
    private String chatId;
    private String userTgId;
    private String text;
    private int replyToMessageId;
    private String parseMode;
    @Builder.Default
    private boolean enableMarkdown = false;
    @Builder.Default
    private boolean hasDocument = false;
    private String documentFilePath;
    private String documentFileName;
    private List<String> options;
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
    public final void setDocumentFileName(final String documentFileName) {
        if (documentFileName != null && !documentFileName.isEmpty()) {
            this.documentFileName = documentFileName;
        }
    }

    public final Optional<String> getMessageMeta() {
        if (this.messageMeta == null || this.messageMeta.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.messageMeta);
    }
}
