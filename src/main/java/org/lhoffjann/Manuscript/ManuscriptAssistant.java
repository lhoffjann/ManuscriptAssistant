package org.lhoffjann.Manuscript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
public class ManuscriptAssistant {
    public void copyFaksimileToMasterImages(Path scan, Path masterImage) throws IOException {
        if (scan.toFile().exists()) {
            Files.copy(scan, masterImage);
        }
    }
    public void copyTIFFsToMasterImage(Manuscript manuscript) throws IOException {
        for (Page page: manuscript.getPageList()){
            copyFaksimileToMasterImages(page.getFront().getTIFPath(),page.getFront().getMasterImagesPath());
            copyFaksimileToMasterImages(page.getBack().getTIFPath(),page.getBack().getMasterImagesPath());
        }
    }
    public void swapPage(Manuscript manuscript, int pageNumber1, int pageNumber2) throws Exception {
        Page page1 = manuscript.getPage(pageNumber1);
        Page page2 = manuscript.getPage(pageNumber2);

        FaksimileHandler frontPages = swapFaksimiles(page1.getFront(), page2.getFront());
        FaksimileHandler backPages = swapFaksimiles(page1.getBack(), page2.getBack());

        page1.setFront(frontPages.getFirstFaksimile());
        page1.setBack(backPages.getFirstFaksimile());
        page2.setFront(frontPages.getSecondFaksimile());
        page2.setBack(backPages.getSecondFaksimile());
    }

    public void swapFrontAndBack(Manuscript manuscript, int pageNumber) throws Exception {
        Page page = manuscript.getPage(pageNumber);
        FaksimileHandler faksimiles = swapFaksimiles(page.getFront(),page.getBack());
        page.setFront(faksimiles.getFirstFaksimile());
        page.setBack(faksimiles.getSecondFaksimile());
    }
    private FaksimileHandler swapFaksimiles(Faksimile faksimile1, Faksimile faksimile2) throws Exception {
        Faksimile tempFaksimile1 = faksimile1;
        Faksimile tempFaksimile2 = faksimile2;
        faksimile1 = tempFaksimile2;
        faksimile2 = tempFaksimile1;
        swapFiles(faksimile1.getTIFPath().toFile(),faksimile2.getTIFPath().toFile());
        swapFiles(faksimile1.getOCRPath().toFile(),faksimile2.getOCRPath().toFile());
        swapFiles(faksimile1.getPDFPath().toFile(),faksimile2.getPDFPath().toFile());
        swapFiles(faksimile1.getJPGPath().toFile(),faksimile2.getJPGPath().toFile());
        faksimile1 = swapValues(tempFaksimile1,faksimile1);
        faksimile2 = swapValues(tempFaksimile2, faksimile2);
        return new FaksimileHandler(faksimile1, faksimile2);
    }

    private Faksimile swapValues(Faksimile oldFaksimile, Faksimile newFaksimile) throws IOException {
        newFaksimile.setOrderNumber(oldFaksimile.getOrderNumber());
        newFaksimile.setPageParameter(oldFaksimile.getPageParameter());
        return newFaksimile;
    }

    private void swapFiles(File file1, File file2) throws Exception {
        if(file1.exists() && file2.exists()) {
            ChecksumCalculator checksumCalculator = new ChecksumCalculator();
            File file1storage = new File(file1.getParent() +
                    FilenameUtils.getBaseName(file1.getName()) + "storage." +
                    FilenameUtils.getExtension(file1.getName()));
            File file2storage = new File(file2.getParent() +
                    FilenameUtils.getBaseName(file2.getName()) + "storage." +
                    FilenameUtils.getExtension(file2.getName()));
            Files.move(file1.toPath(), file1storage.toPath());
            Files.move(file2.toPath(), file2storage.toPath());
            Files.move(file1storage.toPath(), file2.toPath());
            Files.move(file2storage.toPath(), file1.toPath());
        }
    }

}
