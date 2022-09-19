package me.staff4.GringottsTool.DTO;

import lombok.Builder;
import lombok.Getter;

import java.util.Optional;


@Builder
public final class IncomingMessage {
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

    public Optional<String> getMessageMeta() {
        if (this.messageMeta == null || this.messageMeta.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.messageMeta);
    }
}
