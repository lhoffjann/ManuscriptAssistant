package org.lhoffjann.Manuscript;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.security.MessageDigest;

public class ChecksumCalculator {
    public String createChecksum(Path filename) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fs = new FileInputStream(filename.toString());
        BufferedInputStream bs = new BufferedInputStream(fs);
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = bs.read(buffer, 0, buffer.length)) != -1) {
            md.update(buffer, 0, bytesRead);
        }
        byte[] digest = md.digest();

        StringBuilder sb = new StringBuilder();
        for (byte bite : digest) {
            sb.append(String.format("%02x", bite & 0xff));
        }
        return sb.toString();
    }
    public String generateUniqueIdentifier(Path path) throws Exception {
        if(path != null) {
            return createChecksum(path);
        }else {
            return null;
        }
    }
}
