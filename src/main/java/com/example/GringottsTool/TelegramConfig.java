package com.example.GringottsTool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TelegramConfig {
    @Value("${telegram.webhook-path}")
    String webhookPath;
    @Value("${telegram.bot-name}")
    String botName;
    @Value("${telegram.bot-token}")
    String botToken;

    public String getWebhookPath() {
        return webhookPath;
    }

    public String getBotName() {
        return botName;
    }

    public String getBotToken() {
        return botToken;
    }
}