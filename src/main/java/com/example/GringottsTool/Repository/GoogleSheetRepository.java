package com.example.GringottsTool.Repository;

import com.example.GringottsTool.Constants;
import com.example.GringottsTool.Enteties.Cards;
import com.example.GringottsTool.Enteties.Contributions;
import com.example.GringottsTool.Enteties.Info;
import com.example.GringottsTool.Enteties.Partner;
import com.example.GringottsTool.Enteties.QueueItem;
import com.example.GringottsTool.Enteties.Transaction;
import com.example.GringottsTool.Exeptions.GoogleTokenException;
import com.example.GringottsTool.Exeptions.HealthExeption;
import com.example.GringottsTool.Exeptions.InvalidDataException;
import com.example.GringottsTool.Exeptions.NoDataFound;
import com.example.GringottsTool.Healthcheckable;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

@Component
public class GoogleSheetRepository implements Repository, Healthcheckable {
    private static final String QUEUE_LOAN = "Очередь!A2:C";
    private static final String CARDS_RANGE = "Держатели!A2:B";
    private static final String INFO_RANGE = "Сводка!B7:B11";
    private static final String PARTNERS_RANGE = "Участники!A2:M";
    private static final String IS_PARTNER_RANGE = "Участники!B2:B";
    private static final String DEBT_RANGE = "Участники!A2:M";
    private static final String DUCK_LIST_RANGE = "Участники!A2:L";
    private static final String TODAY_PAY_PERSONS_RANGE = "Участники!A2:I";
    private static final String PROXY_RANGE = "Прокси!A2:A";
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int THIRD = 2;
    private static final int FOURTH = 3;
    private static final int FIFTH = 4;
    private static final int SIXTH = 5;
    private static final int SEVENTH = 6;
    private static final int EIGHTH = 7;
    private static final int NINTH = 8;
    private static final int TENTH = 9;
    private static final int ELEVENTH = 10;
    private static final int TWELFTH = 11;
    private static final int THIRTEENTH = 12;
    private static final int CARDS_PARAMETR_COUNT = 7;
    private static final int BEGIN_OF_TRANSACTION_RANGE = 5;
    private static final int QUEUE_COLUMN_NUMBER = 3;
    private static final int MAX_LOAN_RATIO = 5;
    private static final double OVERDUE_REPAYMENT_RATIO = 0.5;
    private static final int NUMBER_OF_OVERDUES_WITHOUT_RATIO = 6;
    private static final int GETPARTNERS_MAX_PARAMETR_COUNT = 3;

    private Sheets sheets = GoogleSheets.getSheetsService();
    private Logger log = LogManager.getLogger();

    public GoogleSheetRepository() throws GeneralSecurityException, IOException, GoogleTokenException {
    }

    private  List<List<Object>> getDataFromTable(final String range) throws IOException, NoDataFound {
        ValueRange response = sheets.spreadsheets().values().get(Constants.SHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        return values;
    }

    public final List<Contributions> getContributions() throws NoDataFound, IOException {
        List<List<Object>> date = getDataFromTable("Взносы!1:1");
        List<List<Object>> values = getDataFromTable("Взносы!A2:AD");
        List<Contributions> result = new ArrayList<>();
        for (List<? extends Object> row : values) {
            String name = row.get(FIRST).toString();
            ArrayList<Contributions.Contribution> pay = new ArrayList<>();
            String also;
            try {
                also = row.get(row.size() - 1).toString();
            } catch (IndexOutOfBoundsException e) {
                also = "не было";
            }
            for (int i = FOURTH; i < (row.size() - 1); i++) {
                if (!row.get(i).toString().equals("!") && !row.get(i).toString().isEmpty()) {
                    pay.add(new Contributions.Contribution(date.get(FIRST).get(i).toString(), row.get(i).toString()));
                }
            }
            result.add(new Contributions(name, pay, also));
        }
        return result;
    }

    public final List<Cards> getCards() throws NoDataFound, IOException {
        List<List<Object>> values = getDataFromTable(CARDS_RANGE);
        List<Cards> result = new ArrayList<>();
        List<List<String>> lists = new ArrayList<>();
        int i = -1;
        for (List<? extends Object> row : values) {
            if (row.size() > 1 && !row.get(1).toString().equals("")) {
                i++;
                lists.add(new ArrayList<>());
                lists.get(i).add(row.get(FIRST).toString());
                lists.get(i).add(row.get(SECOND).toString());
                continue;
            }
            if (row.size() != 0 && !row.get(FIRST).toString().equals("")) {
                lists.get(i).add(row.get(FIRST).toString());
            }
        }
        for (List<String> list : lists) {
            int listSize = list.size();
            for (int j = 0; j < (CARDS_PARAMETR_COUNT - listSize); j++) {
                list.add(null);
            }
            String card = list.get(FIRST);
            double sum = Double.parseDouble(list.get(SECOND).replace(",", "."));
            String name = list.get(THIRD);
            long numberPhone = Long.parseLong(list.get(FOURTH));
            String city = list.get(FIFTH);
            String bank = list.get(SIXTH);
            String payWay = list.get(SEVENTH);
            String link = list.get(EIGHTH);
            result.add(new Cards(card, sum, name, numberPhone, city, bank, payWay, link));
        }
        return result;
    }

    public final Info getInfo() throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(INFO_RANGE);
        int capital = makeInt(values.get(FIRST).toString());
        int borrowedMoney = makeInt(values.get(SECOND).toString());
        int overdue = makeInt(values.get(THIRD).toString());
        int reserve = makeInt(values.get(FOURTH).toString());
        int active = makeInt(values.get(FIFTH).toString());
        return new Info(capital, borrowedMoney, overdue, reserve, active);
    }

    private int makeInt(final String str) {
        return Integer.parseInt(str.replaceAll("[\\D]", ""));
    }

    public final List<Partner> getPartners(final String nameOrTgId) throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(PARTNERS_RANGE);
        List<Partner> result = new ArrayList<>();
        for (List<Object> row : values) {
            int tableID = 2 + values.indexOf(row);
            String name = row.get(FIRST).toString();
            String tgId = row.get(SECOND).toString();
            String city = row.get(FOURTH).toString();
            String searchStr = (name + " " + city).toLowerCase();
            if (isContains(searchStr, nameOrTgId.toLowerCase()) || tgId.equals(nameOrTgId)) {
                Partner partner = getNewPartner(row, tableID);
                result.add(partner);
            }
        }
        return result;
    }

    private Partner findPartner(final int tableId) throws NoDataFound, IOException {
        List<List<Object>> values = getDataFromTable(String.format("Участники!A%d:M%d", tableId, tableId));
        List<Object> row = values.get(FIRST);
        int tableID = 2 + values.indexOf(row);
        if (values.size() != 0) {
            return getNewPartner(row, tableID);
        } else {
            return null;
        }
    }

    private Partner getNewPartner(final List<Object> row, final int tableID) throws NoDataFound, IOException {
        int maxLoan = getInfo().getMaxLoan();
        String name = row.get(FIRST).toString();
        String tgId = row.get(SECOND).toString();
        String city = row.get(FOURTH).toString();
        String vk = row.get(THIRD).toString();
        int contributions = Integer.parseInt(row.get(FIFTH).toString());
        double sumContributions = Double.parseDouble(row.get(SIXTH).toString().replace(",", "."));
        int loan = 0;
        int debt = 0;
        int earlyRepayment = 0;
        int overdueRepayment = 0;

        if (row.get(SEVENTH).toString().equals("")) {
            loan = Integer.parseInt(row.get(SEVENTH).toString());
        }
        if (row.get(EIGHTH).toString().equals("")) {
            debt = Integer.parseInt(row.get(EIGHTH).toString());
        }
        String returnDate = row.get(NINTH).toString();
        if (!row.get(TENTH).toString().equals("")) {
            earlyRepayment = Integer.parseInt(row.get(TENTH).toString());
        }
        if (!row.get(ELEVENTH).toString().equals("")) {
            overdueRepayment = Integer.parseInt(row.get(ELEVENTH).toString());
        }
        int maxMyLoan = getMaxMyLoan(sumContributions, overdueRepayment, maxLoan);

        boolean elite = false;
        boolean isPayedThisMonth = false;
        if (row.get(TWELFTH).toString().equals("0")) {
            elite = true;
        }
        if (row.get(THIRTEENTH).toString().equals("0")) {
            isPayedThisMonth = true;
        }
        return new Partner(tableID, name, tgId, vk, city, maxMyLoan, contributions, sumContributions, loan, debt,
                returnDate, earlyRepayment, overdueRepayment, elite, isPayedThisMonth);
    }

    private int getMaxMyLoan(final double sumContributions, final int overdueRepayment, final int maxLoan) {
        int maxMyLoan;
        if (overdueRepayment < NUMBER_OF_OVERDUES_WITHOUT_RATIO) {
            maxMyLoan = (int) (sumContributions * (MAX_LOAN_RATIO - OVERDUE_REPAYMENT_RATIO * overdueRepayment));
        } else {
            maxMyLoan = (int) sumContributions * 2;
        }
        if (maxMyLoan > maxLoan) {
            maxMyLoan = maxLoan;
        }
        return maxMyLoan;
    }

    private boolean isContains(final String str, final String expend) {
        String[] strArray = expend.split(" ");
        if (strArray.length == 1 && str.contains(expend)) {
            return true;
        }
        if (strArray.length == 2 && str.contains(strArray[0]) && str.contains(strArray[1])) {
            return true;
        }
        if (strArray.length == GETPARTNERS_MAX_PARAMETR_COUNT && str.contains(strArray[0]) && str.contains(strArray[1])
                && str.contains(strArray[2])) {
            return true;
        }
        return false;
    }

    public final boolean isPartner(final long checkingTgId) throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(IS_PARTNER_RANGE);
        for (List<Object> row : values) {
            String tgId = getElement(row, FIRST).orElse("").toString();
            if (tgId.equals(String.valueOf(checkingTgId))) {
                return true;
            }
        }
        return false;
    }

    private <T> Optional<T> getElement(final List<T> list, final int index) {
        if (isEmpty(list)) {
            return Optional.empty();
        }
        if (index < 0 || index >= list.size()) {
            return Optional.empty();
        }
        return Optional.of(list.get(index));
    }

    private boolean isEmpty(final Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public final List<Partner> getDebtors() throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(DEBT_RANGE);
        List<Partner> result = new ArrayList<>();
        for (List<? extends Object> row : values) {
            int debt = Integer.parseInt(row.get(EIGHTH).toString());
            if (debt != 0) {
                String name = row.get(0).toString();
                String returnDate = row.get(NINTH).toString();
                int tableId = values.indexOf(row) + 2;
                Partner partner = new Partner(name, debt, returnDate);
                partner.setTableId(tableId);
                result.add(partner);
            }
        }
        return result;
    }

    public final List<Partner> getDuckList() throws IOException, NoDataFound {
        List<List<Object>> names = getDataFromTable(DUCK_LIST_RANGE);
        ArrayList<Partner> partners = new ArrayList<>();
        for (List<? extends Object> row : names) {
            String name = row.get(0).toString();
            int elite = Integer.parseInt(row.get(TWELFTH).toString());
            if (elite == 0) {
                partners.add(new Partner(name));
            }
        }
        return partners;
    }

    public final List<Partner> getTodayDebtors() throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(TODAY_PAY_PERSONS_RANGE);
        LinkedList<Partner> partners = new LinkedList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate nowDate = LocalDate.now(ZoneId.of(Constants.CRON_TIMEZONE));
        nowDate.format(dateTimeFormatter);
        for (List<? extends Object> row : values) {
            int debt = Integer.parseInt(row.get(EIGHTH).toString());
            if (debt != 0) {
                LocalDate dayBeforeToPay = LocalDate.parse(row.get(NINTH).toString(), dateTimeFormatter).minusDays(1);
                String name = row.get(FIRST).toString();
                String returnDate = row.get(NINTH).toString();
                if (dayBeforeToPay.equals(nowDate)) {
                    Partner partner = new Partner(name, debt, returnDate);
                    partners.add(partner);
                }
            }
        }
        return partners;
    }

    public final List<String> getProxy() throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(PROXY_RANGE);
        List<String> proxyList = new ArrayList<>();
        for (List<? extends Object> row : values) {
            proxyList.add(row.get(FIRST).toString());
        }
        return proxyList;
    }

    public final List<Transaction> getTransactions(final Partner partner) throws IOException, InvalidDataException {
        String personRequestRange = "Займы!%s:%s".formatted(partner.getTableId(), partner.getTableId());
        ValueRange datesResponse = sheets.spreadsheets().values().get(Constants.SHEET_ID, "Займы!1:1").execute();
        ValueRange personResponse = sheets.spreadsheets().values().get(Constants.SHEET_ID, personRequestRange)
                .execute();
        List<List<Object>> dates = null;
        List<List<Object>> values = null;
        if (datesResponse != null) {
            dates = datesResponse.getValues();
        }
        if (personResponse != null) {
            values = personResponse.getValues();
        }
        List<Transaction> transactions = new LinkedList<>();
        if (values != null) {
            for (List<? extends Object> row : values) {
                for (int i = row.size() - 1; i >= BEGIN_OF_TRANSACTION_RANGE; i--) {
                    String cellValue = row.get(i).toString();
                    if (!cellValue.isBlank()) {
                        String date = ((dates.get(FIRST)).get(i)).toString();
                        int value;
                        try {
                            value = Integer.parseInt(cellValue);
                        } catch (NumberFormatException ignored) {
                            throw new InvalidDataException("Error in getTransactions", personRequestRange, i + 1,
                                    cellValue, Constants.NUMERIC_DECIMAL_EXPECTED_VALUE);
                        }
                        Transaction transaction = new Transaction(date, value);
                        transactions.add(transaction);
                    }
                }
            }
        }
        return transactions;
    }

    public final Partner getPartnerByTgId(final String tgId) throws IOException, NoDataFound {
        ValueRange resNames = sheets.spreadsheets().values().get(Constants.SHEET_ID, "Участники!A2:B").execute();
        if (resNames == null) {
            throw new NoDataFound("No data found in sheet: \"Участники\"!");
        }
        List<List<Object>> names = resNames.getValues();
        Partner partner = new Partner();
        partner.setTgId(tgId);
        for (List<? extends Object> row : names) {
            if (row.size() < 2) {
                continue;
            }
            String name = row.get(FIRST).toString();
            String thisTgId = row.get(SECOND).toString();
            if (tgId.equals(thisTgId)) {
                partner.setName(name);
                partner.setTableId(names.indexOf(row) + 2);
            }
        }
        return partner;
    }

    public final Queue<QueueItem> getQueue()
            throws NoDataFound, IOException, NumberFormatException, InvalidDataException {
        List<List<Object>> values = getDataFromTable(QUEUE_LOAN);
        Queue<QueueItem> result = new LinkedList<>();
        for (List<Object> row : values) {
            String name = row.get(FIRST).toString();
            String tgId = row.get(SECOND).toString();
            int sum;
            try {
                sum = Integer.parseInt(row.get(THIRD).toString());
            } catch (NumberFormatException ignored) {
                throw new InvalidDataException("Error in getQueue", QUEUE_LOAN, QUEUE_COLUMN_NUMBER,
                        row.get(THIRD).toString(), Constants.NUMERIC_DECIMAL_EXPECTED_VALUE);
            }
            result.add(new QueueItem(name, tgId, sum));
        }
        return result;
    }

    public final String addQueueItem(final String tableIdString, final int sum)
            throws NoDataFound, IOException, NumberFormatException, InvalidDataException {
        int tableId;
        try {
            tableId = Integer.parseInt(tableIdString);
            if (tableId < 2) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            return Constants.NOT_PARTNER_FROM_ID;
        }
        int count = getNumberInQueue(tableId);
        if (count == 0) {
            String name;
            Partner partner = findPartner(tableId);
            if (partner == null) {
                return Constants.NOT_PARTNERS;
            } else {
                name = partner.getName();
            }
            ValueRange appendBody = new ValueRange()
                    .setValues(Arrays.asList(Arrays.asList(name, tableId, sum)));
            sheets.spreadsheets().values()
                    .append(Constants.SHEET_ID, "Очередь", appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setIncludeValuesInResponse(true)
                    .execute();
            return Constants.ADDED_IN_QUEUE;
        } else {
            ValueRange body = new ValueRange()
                    .setValues(Arrays.asList(Arrays.asList(sum)));
            sheets.spreadsheets().values()
                    .update(Constants.SHEET_ID, String.format("Очередь!C%d", count), body)
                    .setValueInputOption("RAW")
                    .execute();
            return Constants.ALREADY_ADDED_IN_QUEUE;
        }

    }

    private int getNumberInQueue(final int tableId)
            throws InvalidDataException, NoDataFound, IOException,  NumberFormatException {
        Queue<QueueItem> queues = getQueue();
        int count = 2;
        for (QueueItem q : queues) {
            if (Integer.parseInt(q.getTgId()) == tableId) {
                return count;
            }
            count++;
        }
        return 0;
    }

    @Override
    public final void isAlive() throws HealthExeption {
        try {
            getInfo();
            log.info("GoogleSheetRepository is healthy");
        } catch (NoDataFound | IOException e) {
            throw new HealthExeption("There is a problem with GoogleSheetRepository");
        }
    }
}
