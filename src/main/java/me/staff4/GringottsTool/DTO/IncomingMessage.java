package me.staff4.GringottsTool.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;


@AllArgsConstructor
@Builder
public class IncomingMessage {

    @Getter
    private String chatId;
    @Getter
    private long userTgId;
    @Getter
    private String text;
    @Getter
    private int messageId;

    private String messageMeta;
    @Builder.Default
    private IncomingMessageType type = IncomingMessageType.UNKNOWN;

    public IncomingMessage(final IncomingMessageType type, final String chatId, final String text) {
        this.type = type;
        this.chatId = chatId;
        this.text = text;
    }

    public IncomingMessage(final IncomingMessageType type, final String chatId, final long userTgId,
                           final String text, final int messageId) {
        this.type = type;
        this.chatId = chatId;
        this.text = text;
        this.userTgId = userTgId;
        this.messageId = messageId;
    }

    public final Optional<String> getMessageMeta() {
        if (this.messageMeta == null || this.messageMeta.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.messageMeta);
    }
}
