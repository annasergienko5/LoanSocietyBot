package me.staff4.GringottsTool.DTO;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class TriggerResponse {
    private boolean success;
    private String message;
}
