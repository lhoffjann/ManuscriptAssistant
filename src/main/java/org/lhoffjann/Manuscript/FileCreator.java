package org.lhoffjann.Manuscript;



import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;



public class FileCreator {

        public void createJPG(Faksimile faksimile) throws IOException {
            BufferedImage image = ImageIO.read(faksimile.getTIFPath().toFile());//Or image.jpg or image.tiff, etc.
            String[] formatNames = ImageIO.getWriterFormatNames();
            ImageIO.write(image, "jpg", faksimile.getJPGPath().toFile());
        }
    public void createJPG(Path pathTif, Path pathJPG) throws IOException {
        BufferedImage image = ImageIO.read(pathTif.toFile());//Or image.jpg or image.tiff, etc.
        ImageIO.write(image, "jpg", pathJPG.toFile());
    }
        

        public void createPDF(Faksimile faksimile){
            Document document = new Document();
            String input = faksimile.getJPGPath().toString(); // .gif and .jpg are ok too!
            String output = faksimile.getPDFPath().toString();

            try {
                FileOutputStream fos = new FileOutputStream(output);
                PdfWriter writer = PdfWriter.getInstance(document, fos);
                writer.open();
                document.open();
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
            ocrCreator.createOCR(faksimile);
        }
}