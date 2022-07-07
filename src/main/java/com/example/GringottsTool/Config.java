package com.example.GringottsTool;

import com.example.GringottsTool.CRUD.Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class Config {

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(Constants.WEBHOOK_PATH).build();
    }

    @Bean
    public Bot springWebhookBot(SetWebhook setWebhook, MessageHandler messageHandler) {
        Bot bot = new Bot(setWebhook, messageHandler);

        bot.setBotPath(Constants.WEBHOOK_PATH);
        bot.setBotUserName(Constants.BOT_USERNAME);
        bot.setBotToken(Constants.BOT_TOKEN);

        return bot;
    }
}
