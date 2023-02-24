package org.lhoffjann.Manuscript.Faksimile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FaksimileHelper {
    public static void renameFiles(Path oldFilePath, Path newFilePath) throws IOException {
        File file = oldFilePath.toFile();
        File file2 = newFilePath.toFile();
        if (file2.exists())
            throw new java.io.IOException("file exists");
        file.renameTo(file2);
    }
}
