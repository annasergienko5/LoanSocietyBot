package me.staff4.GringottsTool.Enteties;

import me.staff4.GringottsTool.Constants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class Info {
    private int capital;
    private int maxLoan;
    private int borrowedMoney;
    private int overdue;
    private int reserve;
    private int active;

    public Info(final int capital,
                final int borrowedMoney,
                final int overdue,
                final int reserve,
                final int active) {
        this.capital = capital;
        this.borrowedMoney = borrowedMoney;
        this.overdue = overdue;
        this.reserve = reserve;
        this.active = active;
        this.maxLoan = (int) (capital * Constants.MAXIMUM_LOAN_COEFFICIENT);
    }

    @Override
    public String toString() {
        return String.format("""

                        `%-12s%,12d₽
                        %-12s%,12d₽
                        %-12s%,12d₽
                        %-12s%,12d₽
                        %-12s%,12d₽
                        %-12s%,12d₽`""",
                "Капитал", this.capital,
                "Макс. займ", this.maxLoan,
                "Занято", this.borrowedMoney,
                "Просрочено", this.overdue,
                "Запас", this.reserve,
                "Актив", this.active);
    }
}
