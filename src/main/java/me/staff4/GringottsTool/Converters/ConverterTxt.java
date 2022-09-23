package me.staff4.GringottsTool.Converters;
import me.staff4.GringottsTool.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConverterTxt {
    public final String toTxtFile(final String text) throws IOException {
        Path tempPath;
        try {
            tempPath = Files.createTempFile(null, ".txt");
            Files.write(tempPath, text.getBytes());
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            throw new IOException(Constants.ERROR_WRITING_TXT_FILE);
        }
        return tempPath.toString();
    }
}
