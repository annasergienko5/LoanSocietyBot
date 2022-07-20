package com.example.GringottsTool;

import com.example.GringottsTool.Exeptions.EnvironmentNullExeption;
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
    public void checkENV() throws EnvironmentNullExeption {
        String BOT_TOKEN=System.getenv("TOKEN_BOT");
        String BOT_USERNAME=System.getenv("BOT_USERNAME");
        String WEBHOOK_PATH=System.getenv("WEBHOOK_PATH");
        String APPLICATION_NAME=System.getenv("APPLICATION_NAME");
        String CREDENTIALS_FILE_PATH=System.getenv("CREDENTIALS_FILE_PATH");
        String SHEET_ID=System.getenv("SHEET_ID");
        String RULE = System.getenv("RULE");
        String PROXY=System.getenv("PROXY");
        String SCHEDULED_NO_DEBTS = System.getenv("SCHEDULED_NO_DEBTS");
        String PUBLIC_CHAT_ID = System.getenv("PUBLIC_CHAT_ID");
        String ADMIN_CHAT_ID = System.getenv("ADMIN_CHAT_ID");
        String CRON_TIMEZONE = System.getenv("CRON_TIMEZONE");
        String DEBT_REMINDER_TIME = System.getenv("DEBT_REMINDER_TIME");
        String TODAY_PAYERS_REMINDER_TIME = System.getenv("TODAY_PAYERS_REMINDER_TIME");
        String SCHEDULED_NO_TODAY_PAYS = System.getenv("SCHEDULED_NO_TODAY_PAYS");

        if (Constants.BOT_TOKEN == null){
            throw new EnvironmentNullExeption("BOT_TOKEN = null");
        }
        if (Constants.BOT_USERNAME == null){
            throw new EnvironmentNullExeption("BOT_USERNAME = null");
        }
        if (Constants.WEBHOOK_PATH == null){
            throw new EnvironmentNullExeption("WEBHOOK_PATH = null");
        }
        if (Constants.APPLICATION_NAME == null){
            throw new EnvironmentNullExeption("APPLICATION_NAME = null");
        }
        if (Constants.CREDENTIALS_FILE_PATH == null){
            throw new EnvironmentNullExeption("CREDENTIALS_FILE_PATH = null");
        }
        if (Constants.SHEET_ID == null){
            throw new EnvironmentNullExeption("SHEET_ID = null");
        }
        if (Constants.RULE == null){
            throw new EnvironmentNullExeption("RULE = null");
        }
        if (Constants.PROXY == null){
            throw new EnvironmentNullExeption("PROXY = null");
        }
        if (Constants.PUBLIC_CHAT_ID == null){
            throw new EnvironmentNullExeption("PUBLIC_CHAT_ID = null");
        }
        if (Constants.ADMIN_CHAT_ID == null){
            throw new EnvironmentNullExeption("ADMIN_CHAT_ID = null");
        }
        if (Constants.CRON_TIMEZONE == null){
            throw new EnvironmentNullExeption("CRON_TIMEZONE = null");
        }
        if (Constants.DEBT_REMINDER_TIME == null){
            throw new EnvironmentNullExeption("DEBT_REMINDER_TIME = null");
        }
        if (Constants.TODAY_PAYERS_REMINDER_TIME == null){
            throw new EnvironmentNullExeption("CRON_TIMEZONE = null");
        }

    }

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
