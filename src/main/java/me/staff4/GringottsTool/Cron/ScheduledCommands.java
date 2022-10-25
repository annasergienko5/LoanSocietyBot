package me.staff4.GringottsTool.Cron;

import me.staff4.GringottsTool.SystemCommands.Command;
import me.staff4.GringottsTool.SystemCommands.SystemCommandManager;
import org.springframework.scheduling.annotation.Scheduled;

public class ScheduledCommands {
    private static final String CRON_DEBT_SCHEDULE = "${cron.expression.debt}";
    private static final String CRON_TODAY_PAYERS = "${cron.expression.todayPayers}";
    private static final String CRON_OVERDUE_DEBTORS = "${cron.expression.overdueDebtors}";
    private static final String CRON_ZONE = "${cron.expression.zone}";

    private final SystemCommandManager manager;

    public ScheduledCommands(final SystemCommandManager manager) {
        this.manager = manager;
    }


    @Scheduled(cron = CRON_DEBT_SCHEDULE, zone = CRON_ZONE)
    private void triggerDebts() {
        manager.trigger(Command.DEBTORS);
    }


    @Scheduled(cron = CRON_TODAY_PAYERS, zone = CRON_ZONE)
    private void triggerTodayDebts() {
        manager.trigger(Command.TODAY_DEBTORS);
    }


    @Scheduled(cron = CRON_OVERDUE_DEBTORS, zone = CRON_ZONE)
    private void triggerOverdueDebtors() {
        manager.trigger(Command.OVERDUE_DEBTORS);
    }
}
