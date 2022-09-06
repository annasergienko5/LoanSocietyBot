package com.example.GringottsTool.Repository;

import com.example.GringottsTool.Constants;
import com.example.GringottsTool.Enteties.*;
import com.example.GringottsTool.Exeptions.GoogleTokenException;
import com.example.GringottsTool.Exeptions.HealthExeption;
import com.example.GringottsTool.Exeptions.InvalidDataException;
import com.example.GringottsTool.Exeptions.NoDataFound;
import com.example.GringottsTool.Healthcheckable;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private final int FIRST = 0;
    Sheets sheets = GoogleSheets.getSheetsService();
    Logger log = LogManager.getLogger();

    public GoogleSheetRepository() throws GeneralSecurityException, IOException, GoogleTokenException {
    }

    private List<List<Object>> getDataFromTable(String range) throws IOException, NoDataFound {
        ValueRange response = sheets.spreadsheets().values().get(Constants.SHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            throw new NoDataFound(Constants.NOT_FOUND_DATA);
        }
        return values;
    }

    public List<Contributions> getContributions() throws NoDataFound, IOException {
        List<List<Object>> date = getDataFromTable("Взносы!1:1");
        List<List<Object>> values = getDataFromTable("Взносы!A2:AD");
        List<Contributions> result = new ArrayList<>();
        for (List<? extends Object> row : values) {
            String name = row.get(0).toString();
            ArrayList<Contributions.Contribution> pay = new ArrayList<>();
            String also;
            try {
                also = row.get(row.size() - 1).toString();
            } catch (IndexOutOfBoundsException e) {
                also = "не было";
            }
            for (int i = 3; i < (row.size() - 1); i++) {
                if (!row.get(i).toString().equals("!") && !row.get(i).toString().isEmpty()) {
                    pay.add(new Contributions.Contribution(date.get(0).get(i).toString(), row.get(i).toString()));
                }
            }
            result.add(new Contributions(name, pay, also));
        }
        return result;
    }


    public List<Cards> getCards() throws NoDataFound, IOException {
        List<List<Object>> values = getDataFromTable(CARDS_RANGE);
        List<Cards> result = new ArrayList<>();
        List<List<String>> lists = new ArrayList<>();
        int i = -1;
        for (List<? extends Object> row : values) {
            if (row.size() > 1 && !row.get(1).toString().equals("")) {
                i++;
                lists.add(new ArrayList<>());
                lists.get(i).add(row.get(0).toString());
                lists.get(i).add(row.get(1).toString());
                continue;
            }
            if (row.size() != 0 && !row.get(0).toString().equals("")) {
                lists.get(i).add(row.get(0).toString());
            }
        }
        for (List<String> list : lists) {
            if (list.size() < 7) {
                list.add(null);
            }
            if (list.size() < 8) {
                list.add(null);
            }
            String card = list.get(0);
            double sum = Double.parseDouble(list.get(1).replace(",", "."));
            String name = list.get(2);
            long numberPhone = Long.parseLong(list.get(3));
            String city = list.get(4);
            String bank = list.get(5);
            String payWay = list.get(6);
            String link = list.get(7);
            result.add(new Cards(card, sum, name, numberPhone, city, bank, payWay, link));
        }
        return result;
    }

    public Info getInfo() throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(INFO_RANGE);
        int capital = makeInt(values.get(0).toString());
        int borrowedMoney = makeInt(values.get(1).toString());
        int overdue = makeInt(values.get(2).toString());
        int reserve = makeInt(values.get(3).toString());
        int active = makeInt(values.get(4).toString());
        return new Info(capital, borrowedMoney, overdue, reserve, active);
    }

    private int makeInt(String str) {
        return Integer.parseInt(str.replaceAll("[\\D]", ""));
    }

    public List<Partner> getPartners(String nameOrTgId) throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(PARTNERS_RANGE);
        List<Partner> result = new ArrayList<>();
        for (List<Object> row : values) {
            int tableID = 2 + values.indexOf(row);
            String name = row.get(0).toString();
            String tgId = row.get(1).toString();
            String city = row.get(3).toString();
            String searchStr = (name + " " + city).toLowerCase();
            if (isContains(searchStr, nameOrTgId.toLowerCase()) || tgId.equals(nameOrTgId)) {
                Partner partner = getNewPartner(row, tableID);
                result.add(partner);
            }
        }
        return result;
    }

    private Partner findPartner(int tableId) throws NoDataFound, IOException {
        List<List<Object>> values = getDataFromTable(String.format("Участники!A%d:M%d", tableId, tableId));
        List<Object> row = values.get(0);
        int tableID = 2 + values.indexOf(row);
        if (values.size() != 0) {
            return getNewPartner(row, tableID);
        } else return null;
    }

    private Partner getNewPartner(List<Object> row, int tableID) throws NoDataFound, IOException {
        int maxLoan = getInfo().getMaxLoan();
        String name = row.get(0).toString();
        String tgId = row.get(1).toString();
        String city = row.get(3).toString();
        String vk = row.get(2).toString();
        int contributions = Integer.parseInt(row.get(4).toString());
        double sumContributions = Double.parseDouble(row.get(5).toString().replace(",", "."));
        int loan = 0;
        int debt = 0;
        int earlyRepayment = 0;
        int overdueRepayment = 0;

        if (row.get(6).toString().equals("")) {
            loan = Integer.parseInt(row.get(6).toString());
        }
        if (row.get(7).toString().equals("")) {
            debt = Integer.parseInt(row.get(7).toString());
        }
        String returnDate = row.get(8).toString();
        if (!row.get(9).toString().equals("")) {
            earlyRepayment = Integer.parseInt(row.get(9).toString());
        }
        if (!row.get(10).toString().equals("")) {
            overdueRepayment = Integer.parseInt(row.get(10).toString());
        }
        int maxMyLoan = getMaxMyLoan(sumContributions, overdueRepayment, maxLoan);

        boolean elite = false;
        boolean isPayedThisMonth = false;
        if (row.get(11).toString().equals("1")) {
            elite = true;
        }
        if (row.get(12).toString().equals("1")) {
            isPayedThisMonth = true;
        }
        return new Partner(tableID, name, tgId, vk, city, maxMyLoan, contributions, sumContributions, loan, debt, returnDate, earlyRepayment, overdueRepayment, elite, isPayedThisMonth);
    }

    private int getMaxMyLoan(double sumContributions, int overdueRepayment, int maxLoan) {
        int maxMyLoan;
        if (overdueRepayment < 6) {
            maxMyLoan = (int) (sumContributions * (5 - 0.5 * overdueRepayment));
        } else maxMyLoan = (int) sumContributions * 2;
        if (maxMyLoan > maxLoan) {
            maxMyLoan = maxLoan;
        }
        return maxMyLoan;
    }

    private boolean isContains(String str, String expend) {
        String[] strArray = expend.split(" ");
        if (strArray.length == 1 && str.contains(expend)) {
            return true;
        }
        if (strArray.length == 2 && str.contains(strArray[0]) && str.contains(strArray[1])) {
            return true;
        }
        if (strArray.length == 3 && str.contains(strArray[0]) && str.contains(strArray[1]) && str.contains(strArray[2])) {
            return true;
        } else return false;
    }

    public boolean isPartner(String checkingTgId) throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(IS_PARTNER_RANGE);
        for (List<Object> row : values) {
            String tgId = getElement(row, FIRST).orElse("").toString();
            if (tgId.equals(checkingTgId)) {
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

    public List<Partner> getDebtors() throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(DEBT_RANGE);
        List<Partner> result = new ArrayList<>();
        for (List<? extends Object> row : values) {
            int debt = Integer.parseInt(row.get(7).toString());
            if (debt != 0) {
                String name = row.get(0).toString();
                String returnDate = row.get(8).toString();
                int tableId = values.indexOf(row) + 2;
                Partner partner = new Partner(name, debt, returnDate);
                partner.setTableId(tableId);
                result.add(partner);
            }
        }
        return result;
    }

    public List<Partner> getDuckList() throws IOException, NoDataFound {
        List<List<Object>> names = getDataFromTable(DUCK_LIST_RANGE);
        ArrayList<Partner> partners = new ArrayList<>();
        for (List<? extends Object> row : names) {
            String name = row.get(0).toString();
            int elite = Integer.parseInt(row.get(11).toString());
            if (elite == 1) {
                partners.add(new Partner(name));
            }
        }
        return partners;
    }

    public List<Partner> getTodayDebtors() throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(TODAY_PAY_PERSONS_RANGE);
        LinkedList<Partner> partners = new LinkedList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate nowDate = LocalDate.now(ZoneId.of(Constants.CRON_TIMEZONE));
        nowDate.format(dateTimeFormatter);
        for (List<? extends Object> row : values) {
            int debt = Integer.parseInt(row.get(7).toString());
            if (debt != 0) {
                LocalDate dayBeforeToPay = LocalDate.parse(row.get(8).toString(), dateTimeFormatter).minusDays(1);
                String name = row.get(0).toString();
                String returnDate = row.get(8).toString();
                if (dayBeforeToPay.equals(nowDate)) {
                    Partner partner = new Partner(name, debt, returnDate);
                    partners.add(partner);
                }
            }
        }
        return partners;
    }

    public List<String> getProxy() throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(PROXY_RANGE);
        List<String> proxyList = new ArrayList<>();
        for (List<? extends Object> row : values) {
            proxyList.add(row.get(0).toString());
        }
        return proxyList;
    }

    public List<Transaction> getTransactions(Partner partner) throws IOException, InvalidDataException {
        String personRequestRange = "Займы!%s:%s".formatted(partner.getTableId(), partner.getTableId());
        ValueRange datesResponse = sheets.spreadsheets().values().get(Constants.SHEET_ID, "Займы!1:1").execute();
        ValueRange personResponse = sheets.spreadsheets().values().get(Constants.SHEET_ID, personRequestRange).execute();
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
                for (int i = row.size() - 1; i >= 5; i--) {
                    String cellValue = row.get(i).toString();
                    if (cellValue.isBlank()) {
                    } else {
                        String date = ((dates.get(0)).get(i)).toString();
                        int value;
                        try {
                            value = Integer.parseInt(cellValue);
                        } catch (NumberFormatException ignored) {
                            throw new InvalidDataException("Error in getTransactions", personRequestRange, i + 1, cellValue, Constants.NUMERIC_DECIMAL_EXPECTED_VALUE);
                        }
                        Transaction transaction = new Transaction(date, value);
                        transactions.add(transaction);
                    }
                }
            }
        }
        return transactions;
    }

    public Partner getPartnerByTgId(String tgId) throws IOException, NoDataFound {
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
            String name = row.get(0).toString();
            String thisTgId = row.get(1).toString();
            if (tgId.equals(thisTgId)) {
                partner.setName(name);
                partner.setTableId(names.indexOf(row) + 2);
            }
        }
        return partner;
    }

    public Queue<QueueItem> getQueue() throws NoDataFound, IOException, NumberFormatException, InvalidDataException {
        List<List<Object>> values = getDataFromTable(QUEUE_LOAN);
        Queue<QueueItem> result = new LinkedList<>();
        for (List<Object> row : values) {
            String name = row.get(0).toString();
            String tgId = row.get(1).toString();
            int sum;
            try {
                sum = Integer.parseInt(row.get(2).toString());
            } catch (NumberFormatException ignored) {
                throw new InvalidDataException("Error in getQueue", QUEUE_LOAN, 3, row.get(2).toString(), Constants.NUMERIC_DECIMAL_EXPECTED_VALUE);
            }
            result.add(new QueueItem(name, tgId, sum));
        }
        return result;
    }

    public String addQueueItem(String tableIdString, int sum) throws NoDataFound, IOException, NumberFormatException, InvalidDataException {
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
            } else name = partner.getName();
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

    private int getNumberInQueue(int tableId) throws InvalidDataException, NoDataFound, IOException, NumberFormatException {
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
    public void isAlive() throws HealthExeption {
        try {
            getInfo();
            log.info("GoogleSheetRepository is healthy");
        } catch (NoDataFound | IOException e) {
            throw new HealthExeption("There is a problem with GoogleSheetRepository");
        }
    }
}