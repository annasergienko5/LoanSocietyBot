package me.staff4.GringottsTool.Repository;

import me.staff4.GringottsTool.Constants;
import me.staff4.GringottsTool.Exeptions.GoogleTokenException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public final class GoogleSheets {

    private GoogleSheets() {
    }

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final Logger LOG = LogManager.getLogger();

    private static GoogleCredentials getCredentials() throws GoogleTokenException {
        Path path = Path.of(Constants.CREDENTIALS_FILE_PATH);
        GoogleCredentials credentials;
        if (Files.exists(path)) {
            try (InputStream inputStream = Files.newInputStream(path)) {
                credentials = ServiceAccountCredentials.fromStream(inputStream).createScoped(SCOPES);
                LOG.info("ServiceAccountJsonFile just read.");
            } catch (IOException | RuntimeException e) {
                throw new GoogleTokenException("Could not read JSON-file or wrong fields of JSON-file in path: "
                        + Constants.CREDENTIALS_FILE_PATH);
            }
        } else {
            throw new GoogleTokenException("Token not found or wrong name of path in environment.");
        }
        return credentials;
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException, GoogleTokenException {
        GoogleCredentials credential = getCredentials();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
                new HttpCredentialsAdapter(credential)).setApplicationName(Constants.APPLICATION_NAME).build();
    }
}

