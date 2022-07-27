package com.example.GringottsTool.Repository;

import com.example.GringottsTool.Constants;
import com.example.GringottsTool.Enteties.*;
import com.example.GringottsTool.Exeptions.NoDataFound;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Component
public class Repository {
    private static final String CONTRIBUTION_RANGE = "Взносы!A:AD";
    private static final String LAST_CONTRIBUTION_RANGE = "Взносы!A:AD";
    private static final String CARDS_RANGE = "Держатели!A2:B";
    private static final String INFO_RANGE = "Сводка!B7:B11";
    private static final String PARTNERS_RANGE = "Участники!A2:M";
    private static final String IS_PARTNER_RANGE = "Участники!B2:B";
    private static final String DEBT_RANGE = "Участники!A2:M";
    private static final String DUCK_LIST_RANGE = "Участники!A2:L";
    private static final String TODAY_PAY_PERSONS_RANGE = "Участники!A2:I";
    private static final String PROXY_RANGE = "Прокси!A2:A";
    private static final Logger logger = LogManager.getLogger();
    Sheets sheets = GoogleSheets.getSheetsService();

    public Repository() throws GeneralSecurityException, IOException {
    }

    private List<List<Object>> getDataFromTable(String range) throws IOException, NoDataFound {
        ValueRange response = sheets.spreadsheets().values().get(Constants.SHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            logger.info("No data found.");
            throw new NoDataFound("No data found");
        }
        return values;
    }

    public Contributions findContribution(String expend) throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(CONTRIBUTION_RANGE);
        for (List row : values) {
            String name = row.get(0).toString();
            if (name.equals(expend)) {
                ArrayList<Contributions.Contribution> pay = new ArrayList<>();
                String also;
                try {
                    also = row.get(29).toString();
                } catch (IndexOutOfBoundsException e) {
                    also = "не было";
                }

                for (int i = 3; i < 29; i++) {
                    if (!row.get(i).toString().equals("!") && !row.get(i).toString().isEmpty()) {
                        pay.add(new Contributions.Contribution(values.get(0).get(i).toString(), row.get(i).toString()));
                    }
                }
                return new Contributions(name, pay, also);
            }
        }
        return null;
    }

    public Contributions.Contribution findLastContribution(String expend) throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(LAST_CONTRIBUTION_RANGE);
        for (List row : values) {
            String name = row.get(0).toString();
            if (name.equals(expend)) {
                for (int i = 3; i < 29; i++) {
                    if (!row.get(i).toString().equals("!") && !row.get(i).toString().isEmpty()) {
                        return new Contributions.Contribution(values.get(0).get(i).toString(), row.get(i).toString());
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<Cards> findCards() throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(CARDS_RANGE);
        ArrayList<Cards> result = new ArrayList<>();
        List<List<String>> lists = new ArrayList<>();
        int i = -1;
        for (List row : values) {
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
            if (list.size() < 6) {
                list.add(null);
            }
            if (list.size() < 7) {
                list.add(null);
            }
            result.add(new Cards(list.get(0), Double.parseDouble(list.get(1).replace(",", ".")), list.get(2), list.get(3), list.get(4), list.get(5), list.get(6)));
        }
        return result;
    }

    public Info findInfo() throws IOException, NoDataFound {
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

    public ArrayList<Partner> findPartners(String expend) throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(PARTNERS_RANGE);
        ArrayList<Partner> result = new ArrayList<>();
        for (List<Object> row : values) {
            String name = row.get(0).toString();
            String tgId = row.get(1).toString();
            String city = row.get(3).toString();
            String searchStr = (name + " " + city).toLowerCase();
            if (isContains(searchStr, expend.toLowerCase()) || tgId.equals(expend)) {
                Partner partner = getNewPartner(row);
                result.add(partner);
            }
        }
        return result;
    }

    private Partner getNewPartner(List<Object> row) throws NoDataFound, IOException {
        int maxLoan = findInfo().getMaxLoan();
        String name = row.get(0).toString();
        String tgId = row.get(1).toString();
        String city = row.get(3).toString();
        String vk = row.get(2).toString();
        int contributions = Integer.parseInt(row.get(4).toString());
        double sumContributions = Double.parseDouble(row.get(5).toString().replace(",", "."));
        int loan = 0;
        int debt = 0;
        int dosrochka = 0;
        int prosrochka = 0;

        if (row.get(6).toString().equals("")) {
            loan = Integer.parseInt(row.get(6).toString());
        }
        if (row.get(7).toString().equals("")) {
            debt = Integer.parseInt(row.get(7).toString());
        }
        String returnDate = row.get(8).toString();
        if (!row.get(9).toString().equals("")) {
            dosrochka = Integer.parseInt(row.get(9).toString());
        }
        if (!row.get(10).toString().equals("")) {
            prosrochka = Integer.parseInt(row.get(10).toString());
        }
        int maxMyLoan = getMaxMyLoan(sumContributions, prosrochka, maxLoan);

        boolean elite = false;
        boolean vznosZaMesac = false;
        if (row.get(11).toString().equals("1")) {
            elite = true;
        }
        if (row.get(12).toString().equals("1")) {
            vznosZaMesac = true;
        }
        return new Partner(name, tgId, vk, city, maxMyLoan, contributions, sumContributions, loan, debt, returnDate, dosrochka, prosrochka, elite, vznosZaMesac);
    }

    private int getMaxMyLoan(double sumContributions,int prosrochka, int maxLoan){
        int maxMyLoan;
        if (prosrochka < 6){
            maxMyLoan = (int) (sumContributions * (5 - 0.5 * prosrochka));
        }else maxMyLoan = (int) sumContributions * 2;
        if (maxMyLoan > maxLoan){
            maxMyLoan = maxLoan;
        }
        return maxMyLoan;
    }

    public static boolean isContains(String str, String expend) {
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

    public boolean isPartner(String expend) throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(IS_PARTNER_RANGE);
        for (List row : values) {
            String tgId = row.get(0).toString();
            if (tgId.equals(expend)) {
                return true;
            }
        }
        return false;
    }

    public List<List<Partner>> findDebt() throws IOException, ParseException, NoDataFound {
        List<List<Object>> values = getDataFromTable(DEBT_RANGE);
        List<List<Partner>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        result.add(new ArrayList<>());
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        for (List row : values) {
            int debt = Integer.parseInt(row.get(7).toString());
            if (debt != 0) {
                Date date = dateFormat.parse(row.get(8).toString());
                String name = row.get(0).toString();
                String returnDate = row.get(8).toString();
                if (date.getTime() <= dateNow.getTime()) {
                    result.get(0).add(new Partner(name, debt, returnDate));
                } else result.get(1).add(new Partner(name, debt, returnDate));
            }
        }
        return result;
    }

    public ArrayList<Partner> getDuckList() throws IOException, NoDataFound {
        List<List<Object>> names = getDataFromTable(DUCK_LIST_RANGE);
        ArrayList<Partner> partners = new ArrayList<>();
        for (List row : names) {
            String name = row.get(0).toString();
            int elite = Integer.parseInt(row.get(11).toString());
            if (elite == 1) {
                partners.add(new Partner(name));
            }
        }
        return partners;
    }

    public LinkedList<Partner> getTodayPayPersons() throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(TODAY_PAY_PERSONS_RANGE);
        LinkedList<Partner> partners = new LinkedList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate nowDate = LocalDate.now(ZoneId.of(Constants.CRON_TIMEZONE));
        nowDate.format(dateTimeFormatter);
        for (List row : values) {
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

    public List<String> findProxy() throws IOException, NoDataFound {
        List<List<Object>> values = getDataFromTable(PROXY_RANGE);
        List<String> proxyList = new ArrayList<>();
        for (List row : values){
            proxyList.add(row.get(0).toString());
        }
        return proxyList;
    }
    
    public List<Transaction> getTransactions(Partner partner)  {
        String personRequestRange = "Займы!%s:%s".formatted(partner.getRowNumber(), partner.getRowNumber());
        ValueRange datesResponse = null;
        try {
            datesResponse = sheets.spreadsheets().values().get(Constants.SHEET_ID, "Займы!1:1").execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ValueRange personResponse = null;
        try {
            personResponse = sheets.spreadsheets().values().get(Constants.SHEET_ID, personRequestRange).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            for (List row : values) {
                for (int i = row.size() - 1; i >= 5; i--) {
                    String cellValue = row.get(i).toString();
                    if (cellValue.isBlank()) {
                    } else {
                        String date = ((dates.get(0)).get(i)).toString();
                        Transaction transaction = new Transaction(date, Integer.parseInt(cellValue));
                        transactions.add(transaction);
                    }
                }
            }
        }
        return transactions;
    }

    public Partner getPersonRowNumber(String tgId) {
        ValueRange resNames = null;
        try {
            resNames = sheets.spreadsheets().values().get(Constants.SHEET_ID, "Участники!A2:B").execute();
        } catch (IOException e) {
            e.printStackTrace();;
        }
        assert resNames != null;
        List<List<Object>> names = resNames.getValues();
        Partner partner = new Partner();
        partner.setTgId(tgId);
        for (List row : names) {
            String name = row.get(0).toString();
            String thisTgId = row.get(1).toString();
            if (tgId.equals(thisTgId)) {
                partner.setName(name);
                partner.setRowNumber(names.indexOf(row) + 2);
            }
        }
        return partner;
    }
}

