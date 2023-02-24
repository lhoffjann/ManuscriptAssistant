package org.lhoffjann.Manuscript.FIleCreator;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.lhoffjann.Manuscript.Manuscript.Manuscript;
import org.lhoffjann.Manuscript.Page.Page;
import org.lhoffjann.Manuscript.enums.PageType;

import java.io.IOException;
import java.nio.file.Path;

public class PDFCreator {
    public void createPDF(Manuscript manuscript) throws IOException {

        PDFMergerUtility obj = new PDFMergerUtility();

        // Setting the destination file path
        Dotenv dotenv = Dotenv.load();
        System.out.println(Path.of(dotenv.get("path_pdf_folder"), manuscript.getManuscriptID())+ ".pdf");
        obj.setDestinationFileName(Path.of(dotenv.get("path_pdf_folder"), manuscript.getManuscriptID()) + ".pdf");

        // Add all source files, to be merged
        for(Page page: manuscript.getPageList()){
            if(page.getFront().getPageParameter() != PageType.FRONT_COLOUR_CARD){
                obj.addSource(page.getFront().getPDFPath().toFile());
            }

            if(page.getBack().getPageParameter() != PageType.BACK_EMPTY){
                System.out.println(page.getBack().getPageParameter());
                obj.addSource(page.getBack().getPDFPath().toFile());
            }
        }
        System.out.println(obj.getDestinationFileName());

        obj.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

        // Merging documents
        System.out.println(
                "PDF Documents merged to a single file");
    }

}
