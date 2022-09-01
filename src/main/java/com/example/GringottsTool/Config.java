package com.example.GringottsTool;

import com.example.GringottsTool.DTO.IncomingMessage;
import com.example.GringottsTool.DTO.OutgoingMessage;
import com.example.GringottsTool.Exeptions.EnvironmentNullExeption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class Config {

    Logger log =  LogManager.getLogger();

    @Bean
    public void checkENV() throws EnvironmentNullExeption {
        Map<String, String> envMap = new HashMap<>();
        envMap.put("TOKEN_BOT", System.getenv("TOKEN_BOT"));
        envMap.put("BOT_USERNAME", System.getenv("BOT_USERNAME"));
        envMap.put("WEBHOOK_PATH", System.getenv("WEBHOOK_PATH"));
        envMap.put("APPLICATION_NAME", System.getenv("APPLICATION_NAME"));
        envMap.put("CREDENTIALS_FILE_PATH", System.getenv("CREDENTIALS_FILE_PATH"));
        envMap.put("SHEET_ID", System.getenv("SHEET_ID"));
        envMap.put("RULE", System.getenv("RULE"));
        envMap.put("PUBLIC_CHAT_ID", System.getenv("PUBLIC_CHAT_ID"));
        envMap.put("ADMIN_CHAT_ID", System.getenv("ADMIN_CHAT_ID"));
        envMap.put("CRON_TIMEZONE", System.getenv("CRON_TIMEZONE"));
        envMap.put("DEBT_REMINDER_TIME", System.getenv("DEBT_REMINDER_TIME"));
        envMap.put("TODAY_PAYERS_REMINDER_TIME", System.getenv("TODAY_PAYERS_REMINDER_TIME"));

        for (Map.Entry<String, String> env : envMap.entrySet()){
            if (env.getValue() == null || env.getValue().isEmpty()){
                throw new EnvironmentNullExeption(env.getKey() + " = null or empty");
            }
        }
    }

    @Bean
    @DependsOn({"checkENV", "registerTgBot", "setWebhookInstance", "springWebhookBot"})
    public HealthChecker healthChecker(List<Healthcheckable> programEntitiesList){
        HealthChecker healthChecker = new HealthChecker(programEntitiesList);
        healthChecker.areAllAlive();
        return healthChecker;
    }

    @Bean
    public BlockingQueue<IncomingMessage> inQueue(){
        return new LinkedBlockingQueue<IncomingMessage>();
    }

    @Bean
    public BlockingQueue<OutgoingMessage> outQueue(){
        return new LinkedBlockingQueue<OutgoingMessage>();
    }

    @Bean
    public  void registerTgBot() throws IOException {
        URL url = new URL("https://api.telegram.org/bot" + Constants.TOKEN_BOT + "/setWebhook?url=" + Constants.WEBHOOK_PATH);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            log.info(content);
        } catch (final Exception ex) {
            log.error(ex);
        }
    }

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(Constants.WEBHOOK_PATH).build();
    }

    @Bean
    public Bot springWebhookBot(SetWebhook setWebhook, BlockingQueue<IncomingMessage> inQueue, BlockingQueue<OutgoingMessage> outQueue) {
        Bot bot = new Bot(setWebhook, inQueue, outQueue);
        bot.setBotPath(Constants.WEBHOOK_PATH);
        bot.setBotUserName(Constants.BOT_USERNAME);
        bot.setBotToken(Constants.TOKEN_BOT);
        return bot;
    }

    @Bean
    @DependsOn("healthChecker")
    public int reportStartMessage(@Autowired Bot bot){
        bot.reportStartMessage();
        return 0;
    }
}
