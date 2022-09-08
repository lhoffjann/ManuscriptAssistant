package org.lhoffjann.Manuscript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FaksimileHelper {
    public void renameFiles(Path oldFilePath, Path newFilePath) throws IOException {
        // File (or directory) with old name
        File file = oldFilePath.toFile();
        File file2 = newFilePath.toFile();
        if (file2.exists())
            throw new java.io.IOException("file exists");

        boolean success = file.renameTo(file2);

    }
}
