package com.example.GringottsTool.CRUD;

import com.example.GringottsTool.Constants;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Component
public class Service {
    private static final Logger logger = LogManager.getLogger();
    Sheets sheets = GoogleSheets.getSheetsService();

    public Service() throws GeneralSecurityException, IOException {
    }

    public ArrayList<String> findBy(String range, String expend) throws IOException {
        String majorDimension = new ValueRange().getMajorDimension();
        ValueRange response = sheets.spreadsheets().values().get(Constants.SHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        String dem = new DimensionRange()
                .setSheetId(0)
                .setDimension("Rows")
                .getDimension();
        logger.info(dem);
        ArrayList<String> result = new ArrayList<>();
        if (values == null || values.isEmpty()) {
            logger.info("No data found.");
        } else {
            for (List row : values) {
                for (Object value : row){
                    if (value.toString().equals(expend)) {
                        result.add(value.toString());
                    }
                }
            }
        }
        return result;
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

