package me.staff4.GringottsTool.MessageHadler.Commands.Interfaces;

import me.staff4.GringottsTool.DTO.IncomingMessage;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.MessageHadler.MessageCommand;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

public interface MessageCommandExecutor {
    MessageCommand command();
    void execute(MessageCommandExecutorResponder responder, IncomingMessage incomingMessage)
            throws InvalidDataException, NoDataFound, IOException, ParseException, GeneralSecurityException;
}
