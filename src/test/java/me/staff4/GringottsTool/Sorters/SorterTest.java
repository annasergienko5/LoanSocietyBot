package me.staff4.GringottsTool.Sorters;

import me.staff4.GringottsTool.Enteties.Partner;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class SorterTest {

    @Test
    void sortDebtorsByDateToPay() {
        List<Partner> partners = new ArrayList<Partner>();
        partners.add(new Partner("One", 6, "01.09.2023"));
        partners.add(new Partner("Two", 1, "15.08.1936"));
        partners.add(new Partner("Three", 4, "01.02.2023"));
        List<Partner> sortedPartners = new Sorter().sortDebtorsByDateToPay(partners);
        assert sortedPartners != null;
        assertEquals("15.08.1936", sortedPartners.get(0).getReturnDate());
        assertEquals("01.02.2023", sortedPartners.get(1).getReturnDate());
        assertEquals("01.09.2023", sortedPartners.get(2).getReturnDate());
    }
}