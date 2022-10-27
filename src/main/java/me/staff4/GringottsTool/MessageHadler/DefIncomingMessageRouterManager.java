package me.staff4.GringottsTool.MessageHadler;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessage;
import me.staff4.GringottsTool.DTO.OutgoingMessageType;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.Commands.Interfaces.*;
import me.staff4.GringottsTool.Repository.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class DefIncomingMessageRouterManager implements IncomingMessageHandlerManager {
    private final Map<MessageCommand, PublicMessageCommandExecutor> publicExecutors;
    private final Map<MessageCommand, PrivateMessageCommandExecutor> privatExecutors;
    private final Map<MessageCommand, AdminMessageCommandExecutor> adminExecutors;
    private final Map<MessageCommand, AllAvailableMessageCommandExecutor> allAvailableExecutors;
    private final Map<MessageCommand, SystemMessageCommandExecutor> systemMessageExecutors;
    private final BlockingQueue<OutgoingMessage> outQueue;
    private final Logger log = LogManager.getLogger();
    @Autowired
    private SelectCommand selectCommand;
    @Autowired
    private SelectTypeChat selectTypeChat;
    @Autowired
    private Repository repository;

    public DefIncomingMessageRouterManager(final List<PublicMessageCommandExecutor> publicCommands,
                                           final List<PrivateMessageCommandExecutor> privatCommands,
                                           final List<AdminMessageCommandExecutor> adminCommands,
                                           final List<AllAvailableMessageCommandExecutor> allAvailableCommands,
                                           final List<SystemMessageCommandExecutor> systemMessageExecutors,
                                           final BlockingQueue<OutgoingMessage> outQueue) {
        this.outQueue = outQueue;
        this.privatExecutors = getMap(privatCommands);
        this.publicExecutors = getMap(publicCommands);
        this.adminExecutors = getMap(adminCommands);
        this.allAvailableExecutors = getMap(allAvailableCommands);
        this.systemMessageExecutors = getMap(systemMessageExecutors);
    }
    private <T extends MessageCommandExecutor> Map<MessageCommand, T> getMap(final List<T> list) {
        Map<MessageCommand, T> result = new HashMap<>();
        for (var executor : list) {
            result.put(executor.command(), executor);
        }
        return result;
    }

    @Override
    public final void handle(final IncomingMessage incomingMessage) {
        Optional<MessageCommand> optionalMessageCommand = selectCommand.getCommand(incomingMessage);
        MessageCommand messageCommand;
        if (optionalMessageCommand.isPresent()) {
            messageCommand = optionalMessageCommand.get();
        } else {
            return;
        }
        TypeChat typeChat = selectTypeChat.selectTypeChat(incomingMessage);
        String chatId = incomingMessage.getChatId();
        String errorMessage = String.format(Constants.ERROR_IN_SOME_FUNCTION, incomingMessage.getText(),
                chatId, incomingMessage.getUserTgId());
        Optional<MessageCommandExecutor> commandExecuter = safeGet(typeChat, messageCommand);
        try {
            if (commandExecuter.isPresent()) {
                commandExecuter.get().execute((this::safePut), incomingMessage);
            } else {
                log.info("unknown command " + messageCommand);
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error(errorMessage, e);
            safePut(new OutgoingMessage(OutgoingMessageType.ERROR, Constants.ADMIN_CHAT_ID,
                    errorMessage + e.getMessage()));
        } catch (NoDataFound e) {
            log.info(e.getMessage(), e);
            safePut(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, e.getMessage(),
                    incomingMessage.getMessageId()));
        } catch (InvalidDataException e) {
            log.info(e.getMessage(), e);
            safePut(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, Constants.INVALID_DATA_IN_CELLS,
                    incomingMessage.getMessageId()));
            safePut(new OutgoingMessage(OutgoingMessageType.ERROR, Constants.ADMIN_CHAT_ID, errorMessage
                    + Constants.INVALID_DATA_IN_CELLS_TO_ADMIN + e.toMessage()));
        } catch (NumberFormatException | ParseException e) {
            log.info(e.getMessage(), e);
            safePut(new OutgoingMessage(OutgoingMessageType.ERROR, chatId, Constants.INVALID_DATA_IN_CELLS,
                    incomingMessage.getMessageId()));
            safePut(new OutgoingMessage(OutgoingMessageType.ERROR, Constants.ADMIN_CHAT_ID, errorMessage
                    + Constants.INVALID_DATA_IN_CELLS_TO_ADMIN));
        }
    }

    private void safePut(final OutgoingMessage outgoingMessage) {
        try {
            outQueue.put(outgoingMessage);
        } catch (InterruptedException e) {
            log.info(Constants.ERROR_OUT_WRITE_IN_BOT);
        }
    }

    private Optional<MessageCommandExecutor> safeGet(
            final TypeChat typeChat,
            final MessageCommand commandName) {
        MessageCommandExecutor executor = switch (typeChat) {
            case ADMIN_CHAT -> adminExecutors.get(commandName);
            case PUBLIC_CHAT -> publicExecutors.get(commandName);
            case PRIVAT_CHAT -> privatExecutors.get(commandName);
            case ALL_AVAILABLE_CHAT -> allAvailableExecutors.get(commandName);
            case SYSTEM_MESSAGE -> systemMessageExecutors.get(commandName);
        };
        return executor == null ? Optional.empty() : Optional.of(executor);
    }
}
