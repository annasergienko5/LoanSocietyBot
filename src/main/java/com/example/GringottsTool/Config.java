package com.example.GringottsTool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
public class Config {
    private final TelegramConfig telegramConfig;

    public Config(TelegramConfig telegramConfig) {
        this.telegramConfig = telegramConfig;
    }

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(telegramConfig.getWebhookPath()).build();
    }

    @Bean
    public Bot springWebhookBot(SetWebhook setWebhook, MessageHandler messageHandler) {
        Bot bot = new Bot(setWebhook, messageHandler);

        bot.setBotPath(telegramConfig.getWebhookPath());
        bot.setBotUserName(telegramConfig.getBotName());
        bot.setBotToken(telegramConfig.getBotToken());

        return bot;
    }
}
