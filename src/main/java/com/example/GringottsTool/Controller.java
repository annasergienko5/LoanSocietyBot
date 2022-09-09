package com.example.GringottsTool;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class Controller {
    private final Bot bot;

    public Controller(final Bot bot) {
        this.bot = bot;
    }

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody final Update update) {
        return bot.onWebhookUpdateReceived(update);
    }

    @GetMapping("/live")
    public ResponseEntity isAlive() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
