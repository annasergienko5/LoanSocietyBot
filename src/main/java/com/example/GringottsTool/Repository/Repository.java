package com.example.GringottsTool.Repository;

import com.example.GringottsTool.Constants;
import com.example.GringottsTool.Enteties.Cards;
import com.example.GringottsTool.Enteties.Contributions;
import com.example.GringottsTool.Enteties.Info;
import com.example.GringottsTool.Enteties.Partner;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class Repository {
    private static final Logger logger = LogManager.getLogger();
    Sheets sheets = GoogleSheets.getSheetsService();

    public Repository() throws GeneralSecurityException, IOException {
    }

    public Contributions findContribution(String expend) throws IOException {
        String range = "Взносы!A:AD";
        ValueRange response = sheets.spreadsheets().values().get(Constants.SHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        ArrayList<Cards> result = new ArrayList<>();
        if (values == null || values.isEmpty()) {
            logger.info("No data found.");
        } else {
            for (List row : values){
                String name = row.get(0).toString();
                if (name.equals(expend)){
                    HashMap<String, String> pay = new HashMap<>();
                    String also = row.get(29).toString();
                    for (int i = 3; i < 29; i++) {
                        if (!row.get(i).toString().equals("!")){
                            pay.put(values.get(0).get(i).toString(), row.get(i).toString());
                        }
                    }
                    return new Contributions(name, pay, also);
                }
            }
        }
        return null;
    }
    public ArrayList<Cards> findCards() throws IOException {
        String range = "Держатели!A2:B";
        ValueRange response = sheets.spreadsheets().values().get(Constants.SHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        ArrayList<Cards> result = new ArrayList<>();
        if (values == null || values.isEmpty()) {
            logger.info("No data found.");
        } else {
            List<List<String>> lists = new ArrayList<>();
            int i = -1;
            for (List row : values){
                if (row.size() > 1 && !row.get(1).toString().equals("")) {
                    i++;
                    lists.add(new ArrayList<>());
                    lists.get(i).add(row.get(0).toString());
                    lists.get(i).add(row.get(1).toString());
                    continue;
                }
                if (row.size() != 0 && !row.get(0).toString().equals("")){
                   lists.get(i).add(row.get(0).toString());
                }
            }
            for (List<String> list : lists){
                if (list.size() < 6){
                    list.add(null);
                }
                if (list.size() < 7){
                    list.add(null);
                }
                result.add(new Cards(list.get(0), Double.parseDouble(list.get(1).replace(",", ".")), list.get(2), list.get(3), list.get(4), list.get(5), list.get(6)));
            }
        }
        return result;
    }

    public Info findInfo() throws IOException {
        String range = "Сводка!B7:B11";
        ValueRange response = sheets.spreadsheets().values().get(Constants.SHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            logger.info("No data found.");
        } else {
            String capital = values.get(0).toString();
            String borrowedMoney = values.get(1).toString();
            String overdue = values.get(2).toString();
            String reserve = values.get(3).toString();
            String active = values.get(4).toString();
            return new Info(capital, borrowedMoney, overdue, reserve, active);
        }return null;
    }

    public ArrayList<Partner> findPartner(String expend) throws IOException {
        String range = "Участники!A2:M";
        ValueRange response = sheets.spreadsheets().values().get(Constants.SHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        ArrayList<Partner> result = new ArrayList<>();
        if (values == null || values.isEmpty()) {
            logger.info("No data found.");
        } else {
            for (List row : values) {
                String name = row.get(0).toString();
                String tgId = row.get(1).toString();
                if (name.equalsIgnoreCase(expend) || tgId.equals(expend)) {
                    String vk = row.get(2).toString();
                    String city = row.get(3).toString();
                    int contributions = Integer.parseInt(row.get(4).toString());
                    double sumContributions = Double.parseDouble(row.get(5).toString().replace(",", "."));
                    int loan = 0;
                    int debt = 0;
                    int dosrochka = 0;
                    int prosrochka = 0;
                    if (row.get(6).toString() != ""){
                        loan = Integer.parseInt(row.get(6).toString());
                    }
                    if (row.get(7).toString() != ""){
                        debt = Integer.parseInt(row.get(7).toString());
                    }
                    String returnDate = row.get(8).toString();
                    if (!row.get(9).toString().equals("")){
                        dosrochka = Integer.parseInt(row.get(9).toString());
                    }
                    if (!row.get(10).toString().equals("")){
                        prosrochka = Integer.parseInt(row.get(10).toString());
                    }

                    boolean elite = false;
                    boolean vznosZaMesac = false;
                    if (row.get(11).toString().equals("1")){
                        elite = true;
                    }
                    if (row.get(12).toString().equals("1")){
                        vznosZaMesac = true;
                    }
                    result.add(new Partner(name, tgId, vk, city, contributions, sumContributions, loan, debt, returnDate, dosrochka, prosrochka, elite, vznosZaMesac));
                }
            }
        }
        return result;
    }
    public  HashMap<Boolean, List<Partner>> findDebt() throws IOException, ParseException {
        String range = "Участники!A2:M";
        ValueRange response = sheets.spreadsheets().values().get(Constants.SHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        HashMap<Boolean, List<Partner>> result = new HashMap<>();
        result.put(true, new ArrayList<>());
        result.put(false, new ArrayList<>());
        if (values == null || values.isEmpty()) {
            logger.info("No data found.");
        } else {
            Date dateNow = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            for (List row : values) {
                int debt = Integer.parseInt(row.get(7).toString());
                if (debt != 0){
                    boolean isProsrochka;
                    Date date = dateFormat.parse(row.get(8).toString());
                    String name = row.get(0).toString();
                    String returnDate = row.get(8).toString();
                    if (date.getTime() <= dateNow.getTime()){
                        isProsrochka = true;
                    }else isProsrochka = false;
                    result.get(isProsrochka).add(new Partner(name, debt, returnDate ));
                    }
                }
            }
        return result;
        }

    public ArrayList<Partner> getDuckList() throws IOException {
        ValueRange resNames = sheets.spreadsheets().values().get(Constants.SHEET_ID, "Участники!A2:L").execute();
        List<List<Object>> names = resNames.getValues();
        ArrayList<Partner> partners = new ArrayList<>();
        if (names == null || names.isEmpty()) {
            logger.info("No data found.");
        } else {
            for (List row : names) {
                String name = row.get(0).toString();
                int elite = Integer.parseInt(row.get(11).toString());
                logger.info("elite" + elite);
                if (elite == 1) {
                    partners.add(new Partner(name));
                }
            }
        }
        return partners;
    }


    public StringBuilder readAllFromSheet(String range) throws GeneralSecurityException, IOException {
        ValueRange response = sheets.spreadsheets().values().get(Constants.SHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        StringBuilder result = new StringBuilder();
        if (values == null || values.isEmpty()) {
            logger.info("No data found.");
        } else {
            for (List row : values) {
                result.append(row).append("\n");
                logger.info(row);
            }
        }
        return result;
    }
    public void addRowToSheet() throws GeneralSecurityException, IOException {
        ValueRange appendBody = new ValueRange()
                .setValues(Arrays.asList(Arrays.asList("3","Stepan", "4000")));
        AppendValuesResponse appendResult = sheets.spreadsheets().values()
                .append(Constants.SHEET_ID,"Лист1",appendBody )
                .setValueInputOption("USER_ENTERED")
                .setIncludeValuesInResponse(true)
                .execute();
        logger.info("Row added.");
    }
    public void updateValue() throws GeneralSecurityException, IOException {
        ValueRange body = new ValueRange()
                .setValues(Arrays.asList(Arrays.asList(500.545)));
        UpdateValuesResponse result = sheets.spreadsheets().values()
                .update(Constants.SHEET_ID, "C3",body)
                .setValueInputOption("RAW")
                .execute();
        logger.info("Value updated.");
    }
    public void deleteRow() throws GeneralSecurityException, IOException {
        DeleteDimensionRequest deleteDimensionRequest = new DeleteDimensionRequest()
                .setRange( new DimensionRange()
                        .setSheetId(0)
                        .setDimension("Rows")
                        .setStartIndex(10));
        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setDeleteDimension(deleteDimensionRequest));
        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        sheets.spreadsheets().batchUpdate(Constants.SHEET_ID,body).execute();
        logger.info("Rows deleted.");
    }
}

