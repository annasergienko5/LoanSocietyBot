package com.githab.KonstantinZhee.service;

import com.githab.KonstantinZhee.DAO.Constants;
import com.githab.KonstantinZhee.DAO.GoogleSheets;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Service {
    private static final Logger logger = LogManager.getLogger();

    public void readAllFromSheet(String range) throws GeneralSecurityException, IOException {
        Sheets sheets = GoogleSheets.getSheetsService();
        ValueRange response = sheets.spreadsheets().values().get(Constants.SHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            logger.info("No data found.");
        } else {
            for (List row : values) {
                logger.info(row);
            }
        }
    }
    public void addRowToSheet() throws GeneralSecurityException, IOException {
        Sheets sheets = GoogleSheets.getSheetsService();
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
        Sheets sheets = GoogleSheets.getSheetsService();
        ValueRange body = new ValueRange()
                .setValues(Arrays.asList(Arrays.asList(500.545)));
        UpdateValuesResponse result = sheets.spreadsheets().values()
                .update(Constants.SHEET_ID, "C3",body)
                .setValueInputOption("RAW")
                .execute();
        logger.info("Value updated.");
    }
    public void deleteRow() throws GeneralSecurityException, IOException {
        Sheets sheets = GoogleSheets.getSheetsService();
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

