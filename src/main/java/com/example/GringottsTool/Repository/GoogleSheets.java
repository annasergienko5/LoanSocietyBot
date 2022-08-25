package com.example.GringottsTool.Repository;
import com.example.GringottsTool.Constants;
import com.example.GringottsTool.Exeptions.GoogleTokenException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleSheets {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static Logger log = LogManager.getLogger();

    private static Credential getCredentals() throws IOException, GeneralSecurityException, GoogleTokenException {
        InputStream inputStream = null;
        Path path = Path.of(Constants.CREDENTIALS_FILE_PATH);
        Credential credential = null;
        if (Files.exists(path)){
        try {
            inputStream = Files.newInputStream(path);
            log.info(inputStream);
        } catch (IOException | RuntimeException e){
            log.error("Resource not found in path: " + Constants.CREDENTIALS_FILE_PATH, e);
            System.exit(1);
        }
            GoogleClientSecrets googleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream));
            GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, googleClientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(Constants.TOKEN_STORED_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver localServerReceiver = new LocalServerReceiver.Builder().setPort(8888).build();
            credential = new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow,
                    localServerReceiver).authorize("user");
        } else {
            log.info("Token not found or wrong name of path.");
            throw new GoogleTokenException("Token not found or wrong name of path.");
        }
        return credential;
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException, GoogleTokenException {
        Credential credential = getCredentals();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),JSON_FACTORY, credential)
                .setApplicationName(Constants.APPLICATION_NAME)
                .build();

    }
}

