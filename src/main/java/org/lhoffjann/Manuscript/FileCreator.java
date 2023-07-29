package org.lhoffjann.Manuscript;

import java.awt.image.BufferedImage;

import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class FileCreator {

    public void createJPG(Faksimile faksimile) throws IOException {
        BufferedImage image = ImageIO.read(faksimile.getTIFPath().toFile());// Or image.jpg or image.tiff, etc.
        ImageIO.write(image, "jpg", faksimile.getJPGPath().toFile());
    }

    public void createJPG(Path pathTif, Path pathJPG) throws IOException {
        BufferedImage image = ImageIO.read(pathTif.toFile());// Or image.jpg or image.tiff, etc.
        ImageIO.write(image, "jpg", pathJPG.toFile());
    }

    public void createPDF(Faksimile faksimile) {
        String tiffFilePath = faksimile.getJPGPath().toString(); // .gif and .jpg are ok too!
        String pdfFilePath = faksimile.getPDFPath().toString();

        try {
            BufferedImage image = ImageIO.read(new File(tiffFilePath));

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            PDDocument document = new PDDocument();
            PDPage page = new PDPage(new PDRectangle(imageWidth, imageHeight));
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            PDImageXObject pdfImage = LosslessFactory.createFromImage(document, image);

            contentStream.drawImage(pdfImage, 0, 0, imageWidth, imageHeight);

            contentStream.close();
            document.save(pdfFilePath);
            document.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createOCR(Faksimile faksimile) throws Exception {
        OcrCreator ocrCreator = new OcrCreator();
        if (!faksimile.getOCRPath().toFile().exists()) {
            ocrCreator.createOCR(faksimile);
        }
    }
}