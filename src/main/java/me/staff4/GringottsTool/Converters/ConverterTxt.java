package me.staff4.GringottsTool.Converters;

import me.staff4.GringottsTool.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ConverterTxt {
    public final String toTxtFile(final String namePerson, final String text) throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate nowDate = LocalDate.now(ZoneId.of(Constants.CRON_TIMEZONE));
        nowDate.format(dateTimeFormatter);
        Path path;
        try {
            String filePath = System.getProperty("java.io.tmpdir") + "/"
                    + String.format(Constants.FULL_SEARCH_FILENAME_ABOUT_FULLCREDIT, nowDate, namePerson) + ".txt";
            path = Files.createFile(Paths.get(filePath));
        Files.write(path, text.getBytes());
        } catch (IOException e) {
            throw new IOException(Constants.ERROR_WRITING_TXT_FILE);
        }
        return path.toString();
    }
}
