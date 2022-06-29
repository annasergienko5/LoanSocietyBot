package com.githab.KonstantinZhee;

import com.githab.KonstantinZhee.DAO.GoogleSheets;
import com.githab.KonstantinZhee.service.Service;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


public class Main {
    private static final Logger logger = LogManager.getLogger();
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        String range = "Лист1!A1:C3";
        Service service = new Service();
        service.readAllFromSheet(range);
        service.addRowToSheet();
        service.updateValue();
        service.deleteRow();
    }
}
