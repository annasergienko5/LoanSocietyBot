package me.staff4.GringottsTool.SystemCommands.Executors;

import me.staff4.GringottsTool.SystemCommands.Command;
import org.springframework.stereotype.Component;

@Component
public class GetOverdueDebtorsCommandExecutor extends AbsAdminCommandExecutor implements SystemCommandExecutorCommand {
    @Override
    public final void exec(final SystemCommandExecutorResponder responder) {
        super.sendToAdminChat(responder, "/overduedebts");
    }

    @Override
    public final Command command() {
        return Command.OVERDUE_DEBTORS;
    }
}