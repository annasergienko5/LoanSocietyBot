package com.githab.KonstantinZhee;

import com.githab.KonstantinZhee.service.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;



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
