package org.lhoffjann.Manuscript;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.itextpdf.awt.geom.AffineTransform;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;





public class FileCreator {

    public void createJPG(Faksimile faksimile) throws IOException {
            BufferedImage image = ImageIO.read(faksimile.getTIFPath().toFile());//Or image.jpg or image.tiff, etc.
            ImageIO.write(image, "jpg", faksimile.getJPGPath().toFile());
        }
    public void createJPG(Path pathTif, Path pathJPG) throws IOException {
        BufferedImage image = ImageIO.read(pathTif.toFile());//Or image.jpg or image.tiff, etc.
        ImageIO.write(image, "jpg", pathJPG.toFile());
    }
    public void createPDF(Faksimile faksimile) {
        String tiffFilePath = faksimile.getJPGPath().toString(); // .gif and .jpg are ok too!
        String pdfFilePath = faksimile.getPDFPath().toString();

        try {
            // Load TIFF image
            BufferedImage image = ImageIO.read(new File(tiffFilePath));

            // Get the dimensions of the image
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            // Create PDF document with the same dimensions as the image
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(new PDRectangle(imageWidth, imageHeight));
            document.addPage(page);

            // Create a content stream for the PDF
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Create a PDImageXObject from the TIFF image
            PDImageXObject pdfImage = LosslessFactory.createFromImage(document, image);

            // Draw the image on the PDF page at position (0, 0)
            contentStream.drawImage(pdfImage, 0, 0, imageWidth, imageHeight);

            // Close the content stream and save the PDF document
            contentStream.close();
            document.save(pdfFilePath);
            document.close();

            System.out.println("TIFF converted to PDF successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


        public void createPDFe(Faksimile faksimile){
            Document document = new Document( PageSize.A4,0, 0, 0, 0);
            String input = faksimile.getJPGPath().toString(); // .gif and .jpg are ok too!
            String output = faksimile.getPDFPath().toString();

            try {
                FileOutputStream fos = new FileOutputStream(output);
                PdfWriter writer = PdfWriter.getInstance(document, fos);
                writer.open();
                document.open();
                document.setMargins(0,0,0,0);
                Image image = Image.getInstance(input);

                
               float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin()) / image.getWidth()) * 100;
                image.scalePercent(scaler);
                
                document.add(image);
                document.close();
                writer.close();
            }
            catch (Exception e) {
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