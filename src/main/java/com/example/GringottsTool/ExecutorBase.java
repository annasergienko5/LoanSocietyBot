package com.example.GringottsTool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public final class ExecutorBase {
    private final Logger log = LogManager.getLogger();
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationContext applicationContext;

    private Boolean debug = true;

    @EventListener(ApplicationReadyEvent.class)
    public void atStartup() {
        Bot bot = applicationContext.getBean(Bot.class);
        taskExecutor.execute(bot);

        MessageHandler messageHandler = applicationContext.getBean(MessageHandler.class);
        taskExecutor.execute(messageHandler);
        if (debug) {
            log.info("###### Startup ok");
        }
    }
}
