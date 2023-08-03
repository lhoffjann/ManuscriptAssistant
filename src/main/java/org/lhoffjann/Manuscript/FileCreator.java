package org.lhoffjann.Manuscript;

import java.awt.image.BufferedImage;

import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;


import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import io.github.cdimascio.dotenv.Dotenv;

public class FileCreator {

    private static BufferedImage compressImage(BufferedImage image, float compressionQuality) {
        Iterator<javax.imageio.ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No writers found");
        }
        javax.imageio.ImageWriter writer = writers.next();
        javax.imageio.ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(compressionQuality);

        BufferedImage compressedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            javax.imageio.IIOImage outputImage = new javax.imageio.IIOImage(image, null, null);
            writer.setOutput(ImageIO.createImageOutputStream(outputStream));
            writer.write(null, outputImage, param);
            writer.dispose();

            compressedImage = ImageIO.read(new java.io.ByteArrayInputStream(outputStream.toByteArray()));
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return compressedImage;
    }



    public void createJPG(Path pathTif, Path pathJPG) throws IOException {
        Dotenv dotenv = Dotenv.load();
        final float compressionPercentage = Float.parseFloat(dotenv.get("compression_factor_jpg"));
        final float compressionQuality = compressionPercentage / 100;
        System.out.println(
          compressionPercentage  
        );
        System.out.println(
            compressionQuality
        );
        BufferedImage image = ImageIO.read(pathTif.toFile());// Or image.jpg or image.tiff, etc.
        ImageIO.write(compressImage(image, compressionQuality), "jpg", pathJPG.toFile());
    }

    public void createPDF(Faksimile faksimile) {
        Dotenv dotenv = Dotenv.load();
        final String tiffFilePath = faksimile.getJPGPath().toString(); // .gif and .jpg are ok too!
        final String pdfFilePath = faksimile.getPDFPath().toString();
        final float percentScale = Float.parseFloat(dotenv.get("scaling_factor_pdf"));
        final float scalingFactor = percentScale / 100;
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