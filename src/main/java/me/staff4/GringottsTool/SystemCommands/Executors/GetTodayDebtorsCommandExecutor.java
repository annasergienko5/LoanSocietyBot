package me.staff4.GringottsTool.SystemCommands.Executors;

import me.staff4.GringottsTool.SystemCommands.Command;
import org.springframework.stereotype.Component;

@Component
public class GetTodayDebtorsCommandExecutor extends AbsAdminCommandExecutor implements SystemCommandExecutorCommand {
    @Override
    public final void exec(final SystemCommandExecutorResponder responder) {
        super.sendToAdminChat(responder, "getTodayDebtors");
    }

    @Override
    public final Command command() {
        return Command.TODAY_DEBTORS;
    }
}
