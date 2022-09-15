package me.staff4.GringottsTool.Converters;

import me.staff4.GringottsTool.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConverterTxt {
    public final String toTxtFile(final String fileName, final String text) throws IOException {
        Path path;
        try {
            Path pathDirectory = Paths.get(Constants.TEMPORARY_FILES_DIRECTORY);
            if (!Files.exists(pathDirectory)) {
                Files.createDirectory(pathDirectory);
            }
        path = Files.createFile(Paths.get(Constants.TEMPORARY_FILES_DIRECTORY + "/" + fileName + ".txt"));
        Files.write(path, text.getBytes());
        } catch (IOException e) {
            throw new IOException(Constants.ERROR_WRITING_TXT_FILE);
        }
        return path.toString();
    }
}
