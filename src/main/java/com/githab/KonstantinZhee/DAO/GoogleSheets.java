package com.githab.KonstantinZhee.DAO;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleSheets {
    private static final String APPLICATION_NAME = Constants.APPLICATION_NAME;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = Constants.CREDENTIALS_FILE_PATH;
    private static final String TOKENS_DIRECTORY_PATH = Constants.TOKENS_DIRECTORY_PATH;
    public static final String SHEET_ID = Constants.SHEET_ID;
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    private static Credential getCredentals() throws IOException, GeneralSecurityException {
        InputStream inputStream = GoogleSheets.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (inputStream == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets googleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream));
        GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, googleClientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver localServerReceiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow,
                localServerReceiver).authorize("user");
        return credential;
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = getCredentals();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

    }
}

