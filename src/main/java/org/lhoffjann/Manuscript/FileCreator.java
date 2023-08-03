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

import io.github.cdimascio.dotenv.Dotenv;

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
        Dotenv dotenv = Dotenv.load();
        String tiffFilePath = faksimile.getJPGPath().toString(); // .gif and .jpg are ok too!
        String pdfFilePath = faksimile.getPDFPath().toString();
        float percentScale = Float.parseFloat(dotenv.get("scaling_factor_pdf"));
        float scalingFactor = percentScale / 100;
        try {
            BufferedImage image = ImageIO.read(new File(tiffFilePath));

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            int scaledWidth = (int) (imageWidth * scalingFactor);
            int scaledHeight = (int) (imageHeight * scalingFactor);
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(new PDRectangle(scaledWidth, scaledHeight));
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            PDImageXObject pdfImage = LosslessFactory.createFromImage(document, image);

            contentStream.drawImage(pdfImage, 0, 0, scaledWidth, scaledHeight);

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