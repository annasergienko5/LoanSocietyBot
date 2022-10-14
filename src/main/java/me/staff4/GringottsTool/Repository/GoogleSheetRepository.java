package me.staff4.GringottsTool.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.Enteties.Cards;
import me.staff4.GringottsTool.Enteties.Contributions;
import me.staff4.GringottsTool.Enteties.Info;
import me.staff4.GringottsTool.Enteties.Partner;
import me.staff4.GringottsTool.Enteties.QueueItem;
import me.staff4.GringottsTool.Enteties.Transaction;
import me.staff4.GringottsTool.Exeptions.GoogleTokenException;
import me.staff4.GringottsTool.Exeptions.HealthExeption;
import me.staff4.GringottsTool.Exeptions.InvalidDataException;
import me.staff4.GringottsTool.Exeptions.NoDataFound;
import me.staff4.GringottsTool.Healthcheckable;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import me.staff4.GringottsTool.Sorters.Sorter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;


@Component
public class GoogleSheetRepository implements Repository, Healthcheckable {
    private static final String QUEUE_LOAN = "Очередь!A2:C";
    private static final String CARDS_RANGE = "Карточки!A2:G";
    private static final String INFO_RANGE = "Сводка!B7:B11";
    private static final String PARTNERS_RANGE = "Участники!A2:M";
    private static final String IS_PARTNER_RANGE = "Участники!B2:B";
    private static final String DEBT_RANGE = "Участники!A2:M";
    private static final String DUCK_LIST_RANGE = "Участники!A2:L";
    private static final String TODAY_PAY_PERSONS_RANGE = "Участники!A2:J";
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
    private static final int BEGIN_OF_TRANSACTION_RANGE = 5;
    private static final int MAX_LOAN_RATIO = 5;
    private static final double OVERDUE_REPAYMENT_RATIO = 0.5;
    private static final int NUMBER_OF_OVERDUES_WITHOUT_RATIO = 6;
    private static final int GETPARTNERS_MAX_PARAMETR_COUNT = 3;

    private Sheets sheets = GoogleSheets.getSheetsService();
    private Logger log = LogManager.getLogger();

    public GoogleSheetRepository() throws GeneralSecurityException, IOException, GoogleTokenException {
    }

    private List<List<Object>> getDataFromTable(final String range) throws IOException, NoDataFound {
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
        for (List<Object> list : values) {
            String name = getElement(list, FIRST).get().toString();
            String city = getElement(list, SECOND).get().toString();
            String card = getElement(list, THIRD).get().toString();
            String bank = getElement(list, FOURTH).get().toString();
            String numberPhone = getElement(list, FIFTH).get().toString();
            String sum = getElement(list, SIXTH).get().toString();
            if (card.equals("") && numberPhone.equals("")) {
                continue;
            }
            Map<?, ?> meta = null;
            if (getElement(list, SEVENTH).isPresent()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = getElement(list, SEVENTH).get().toString();
                meta = objectMapper.readValue(json, Map.class);
            }
            result.add(new Cards(card, sum, name, numberPhone, city, bank, meta));
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

        if (!row.get(SEVENTH).toString().equals("")) {
            loan = Integer.parseInt(row.get(SEVENTH).toString());
        }
        if (!row.get(EIGHTH).toString().equals("")) {
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

    public final List<String> getAllPartners() throws NoDataFound, IOException {
        List<List<Object>> values = getDataFromTable(IS_PARTNER_RANGE);
        List<String> resultList = new ArrayList<>();
        for (List<Object> row : values) {
            String tgId = getElement(row, FIRST).orElse("").toString();
            resultList.add(tgId);
        }
        return resultList;
    }

    public final List<Partner> getDebtors() throws IOException, NoDataFound, InvalidDataException {
        List<List<Object>> values = getDataFromTable(DEBT_RANGE);
        if (values.size() == 0) {
            return null;
        }
        List<Partner> result = new ArrayList<>();
        for (List<Object> row : values) {
            Partner partner = buildPartnerWithDebt(row, values.indexOf(row) + 2);
            if (partner != null) {
                result.add(partner);
            }
        }
        return new Sorter().sortDebtorsByDateToPay(result);
    }

    private Partner buildPartnerWithDebt(final List<Object> row, final int tableId) throws InvalidDataException {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if (row.size() >= NINTH) {
            String debtString = getElement(row, EIGHTH).orElse("").toString();
            if (!debtString.equals("0") & !debtString.isBlank()) {
                String dayToPay = getElement(row, NINTH).orElse("").toString();
                String name = getElement(row, FIRST).orElse("").toString();
                String tgId = getElement(row, SECOND).orElse("").toString();
                int debt;
                try {
                    debt = Integer.parseInt(debtString);
                    LocalDate.parse(dayToPay, dateTimeFormatter);
                } catch (DateTimeParseException e) {
                    log.info(dateTimeFormatter, e);
                    throw new InvalidDataException("Error in buildPartnerWithDebt",
                            DEBT_RANGE, NINTH, dayToPay, Constants.EXPECTED_CELL_VALUE_DATE, name);
                } catch (NumberFormatException e) {
                    throw new InvalidDataException("Error in buildPartnerWithDebt",
                            DEBT_RANGE, EIGHTH, debtString, Constants.EXPECTED_CELL_VALUE_NUMERIC_DECIMAL, name);
                }
                try {
                    Long.parseLong(tgId);
                } catch (NumberFormatException e) {
                    throw new InvalidDataException("Error in buildPartnerWithDebt",
                            DEBT_RANGE, SECOND, tgId, Constants.EXPECTED_CELL_VALUE_TG_ID, name);
                }
                Partner partner = new Partner(name, debt, dayToPay);
                partner.setTableId(tableId);
                partner.setTgId(tgId);
                return partner;
            }
        }
        return null;
    }

    public final List<Partner> getTodayDebtors() throws
            IOException, NoDataFound, InvalidDataException {
        List<List<Object>> values = getDataFromTable(TODAY_PAY_PERSONS_RANGE);
        if (values.size() > 0) {
            LinkedList<Partner> partners = new LinkedList<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate nowDate = LocalDate.now(ZoneId.of(Constants.CRON_TIMEZONE));
            nowDate.format(dateTimeFormatter);
            for (List<Object> row : values) {
                Partner partner = buildPartnerWithDebt(row, values.indexOf(row) + 2);
                if (partner != null) {
                    if (LocalDate.parse(partner.getReturnDate(), dateTimeFormatter).equals(nowDate)) {
                        partners.add(partner);
                    }
                }
            }
            return partners;
        }
        return null;
    }

    public final List<Partner> getDuckList() throws IOException, NoDataFound, InvalidDataException {
        List<List<Object>> names = getDataFromTable(DUCK_LIST_RANGE);
        ArrayList<Partner> partners = new ArrayList<>();
        for (List<? extends Object> row : names) {
            String name = row.get(0).toString();
            String elite = row.get(TWELFTH).toString();
            if (!elite.isBlank()) {
                int duck;
                try {
                    duck = Integer.parseInt(row.get(TWELFTH).toString());
                } catch (NumberFormatException e) {
                    throw new InvalidDataException("getDuckList", DUCK_LIST_RANGE, TWELFTH, elite,
                            Constants.EXPECTED_CELL_VALUE_LAST_3_MONTH, name);
                }
                if (duck >= 1) {
                    partners.add(new Partner(name));
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
                        float value;
                        try {
                            value = Float.parseFloat(cellValue.replace(",", "."));
                        } catch (NumberFormatException ignored) {
                            throw new InvalidDataException("Error in getTransactions", personRequestRange, i,
                                    cellValue, Constants.EXPECTED_CELL_VALUE_FLOAT, partner.getName());
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
                break;
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
                throw new InvalidDataException("Error in getQueue", QUEUE_LOAN, THIRD,
                        row.get(THIRD).toString(), Constants.EXPECTED_CELL_VALUE_NUMERIC_DECIMAL, name);
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
            throws InvalidDataException, NoDataFound, IOException, NumberFormatException {
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
