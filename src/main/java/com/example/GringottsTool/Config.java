package com.example.GringottsTool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Configuration
public class Config {

    Logger log =  LogManager.getLogger();

    @Bean
    public  void registerTgBot() throws IOException {
        URL url = new URL("https://api.telegram.org/bot" + Constants.BOT_TOKEN + "/setWebhook?url=" + Constants.WEBHOOK_PATH);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            log.info(content);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

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
