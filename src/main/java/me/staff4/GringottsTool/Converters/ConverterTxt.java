package me.staff4.GringottsTool.Converters;

import com.ibm.icu.text.Transliterator;
import lombok.extern.slf4j.Slf4j;
import me.staff4.GringottsTool.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ConverterTxt {
    public final String toTxtFile(final String namePerson, final String text) throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate nowDate = LocalDate.now(ZoneId.of(Constants.CRON_TIMEZONE));
        nowDate.format(dateTimeFormatter);
        Transliterator toLatinTrans = Transliterator.getInstance("Cyrillic-Latin");
        String fileName = toLatinTrans.transliterate(namePerson + " " + nowDate + ".txt");
        Path path;
        try {
            Path tempPath = Files.createTempFile(null, null);
            path = tempPath.resolveSibling(fileName);
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
            Files.write(path, text.getBytes());
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            throw new IOException(Constants.ERROR_WRITING_TXT_FILE);
        }
        return path.toString();
    }
}
