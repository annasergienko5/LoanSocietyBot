package me.staff4.GringottsTool;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public final class Controller {
    private final Bot bot;
    private HealthCheckerResponse healthCheckerResponse;

    public Controller(final Bot bot, final HealthCheckerResponse healthCheckerResponse) {
        this.bot = bot;
        this.healthCheckerResponse = healthCheckerResponse;
    }

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody final Update update) {
        return bot.onWebhookUpdateReceived(update);
    }

    @GetMapping("/live")
    public ResponseEntity isAlive() {
        return healthCheckerResponse.isAlive();
    }
}
