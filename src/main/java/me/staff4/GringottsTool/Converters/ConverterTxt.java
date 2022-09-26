package me.staff4.GringottsTool.Converters;
import me.staff4.GringottsTool.Constants;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConverterTxt {
    public final String saveToTxtFile(final String text) throws IOException {
        Path tempPath;
        Writer out = null;
        try {
            tempPath = Files.createTempFile(null, ".txt");
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(tempPath.toFile()), StandardCharsets.UTF_8));
            out.write(text);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            throw new IOException(Constants.ERROR_WRITING_TXT_FILE);
        } finally {
            assert out != null;
            out.close();
        }
        return tempPath.toString();
    }
}
