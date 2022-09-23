package me.staff4.GringottsTool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


@Component
public final class HelpFileReader {
    private final Logger log = LogManager.getLogger();
    public String read(final String path) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            while (reader.ready()) {
                result.append(reader.readLine());
                result.append("\n");
            }
        } catch (IOException e) {
            log.info(Constants.ERROR_READ_FILE);
        }
        return result.toString();
    }
}
