package me.staff4.GringottsTool.Sorters;

import me.staff4.GringottsTool.Enteties.Partner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class Sorter {
    public final List<Partner> sortDebtorsByDateToPay(final List<Partner> partners) {
        if (partners.size() == 0) {
            return null;
        }
        partners.sort(new Comparator<>() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            public int compare(final Partner p1, final Partner p2) {
                return LocalDate.parse(p1.getReturnDate(), dateTimeFormatter).
                        compareTo(LocalDate.parse(p2.getReturnDate(), dateTimeFormatter));
            }
        });
        return partners;
    }
}
