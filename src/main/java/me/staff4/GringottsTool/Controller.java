package me.staff4.GringottsTool;

import me.staff4.GringottsTool.DTO.TriggerRequest;
import me.staff4.GringottsTool.DTO.TriggerResponse;
import me.staff4.GringottsTool.SystemCommands.SystemCommandManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public final class Controller {
    private final Bot bot;
    private HealthCheckerResponse healthCheckerResponse;
    private SystemCommandManager systemCommandManager;

    public Controller(final Bot bot,
                      final HealthCheckerResponse healthCheckerResponse,
                      final SystemCommandManager systemCommandManager) {
        this.bot = bot;
        this.healthCheckerResponse = healthCheckerResponse;
        this.systemCommandManager = systemCommandManager;
    }

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody final Update update) {
        return bot.onWebhookUpdateReceived(update);
    }

    @GetMapping("/live")
    public ResponseEntity isAlive() {
        return healthCheckerResponse.isAlive();
    }

    @RequestMapping(path = "/trigger", method = RequestMethod.POST, produces = {"application/json"})
    public ResponseEntity<TriggerResponse> trigger(@RequestBody final TriggerRequest request) {
        var response = TriggerResponse.builder();

        if (request.isValid()) {
            systemCommandManager.trigger(request.command());
            response = response.success(true);
        } else {
            response = response.success(false).
                    message("invalid request");
        }

        return new ResponseEntity<>(response.build(), HttpStatus.OK);
    }
}
