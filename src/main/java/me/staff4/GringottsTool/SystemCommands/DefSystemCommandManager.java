package me.staff4.GringottsTool.SystemCommands;

import lombok.extern.slf4j.Slf4j;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.SystemCommands.Executors.SystemCommandExecutor;
import me.staff4.GringottsTool.SystemCommands.Executors.SystemCommandExecutorCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class DefSystemCommandManager implements SystemCommandManager {
    private final BlockingQueue<IncomingMessage> inQueue;
    private final Map<Command, SystemCommandExecutor> executors;


    public DefSystemCommandManager(
            final BlockingQueue<IncomingMessage> inQueue,
            final List<SystemCommandExecutorCommand> executorCommands) {
        this.inQueue = inQueue;

        this.executors = new HashMap<>();
        for (var executor : executorCommands) {
            executors.put(executor.command(), executor);
        }
    }

    @Override
    public final void trigger(final Command commandName) {
        // todo: заменить эксепшен на сообщение об ошибке админам, но в теории таких команд не будет

        safeGet(commandName).
                orElseThrow(() -> new RuntimeException("unknown command" + commandName)).
                exec(this::safePut);
    }

    private void safePut(final IncomingMessage msg) {
        try {
            inQueue.put(msg);
        } catch (InterruptedException e) {
            log.error("cant put msg: " + e);
        }
    }

    private Optional<SystemCommandExecutor> safeGet(final Command commandName) {
        var executor = executors.get(commandName);
        return executor == null ? Optional.empty() : Optional.of(executor);
    }

}
