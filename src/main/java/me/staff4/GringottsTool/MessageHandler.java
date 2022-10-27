package me.staff4.GringottsTool;

import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.Exeptions.HealthExeption;
import me.staff4.GringottsTool.MessageHadler.IncomingMessageHandlerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Component
public class MessageHandler implements Runnable, Healthcheckable {
    private Logger log = LogManager.getLogger();
    private Thread current;
    private final BlockingQueue<IncomingMessage> inQueue;
    private final IncomingMessageHandlerManager manager;

    public MessageHandler(final BlockingQueue<IncomingMessage> inQueue,
                          final IncomingMessageHandlerManager manager) {
        this.inQueue = inQueue;
        this.manager = manager;
    }

    @Override
    public final void run() {
        current = Thread.currentThread();
        while (!Thread.currentThread().isInterrupted()) {
            IncomingMessage incomingMessage;
            try {
                incomingMessage = inQueue.take();
                manager.handle(incomingMessage);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info(Constants.ERROR_TAKING_IN_MESSAGEHANDLER);
            }
        }
    }

    @Override
    public void isAlive() throws HealthExeption {
        if (current != null && !current.isAlive()) {
            throw new HealthExeption("Handler thread stop");
        }
    }
}
