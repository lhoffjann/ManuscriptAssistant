package org.lhoffjann.Manuscript;

import io.github.cdimascio.dotenv.Dotenv;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathHandler {
    private Path basePath;
    private Path manuscriptPath;
    private Path masterImages;

    public PathHandler(){
        Dotenv dotenv = Dotenv.load();
        this.basePath = Paths.get(dotenv.get("path_scans"));



    }

    public Path getBasePath() {
        return basePath;
    }

    public Path getMasterImagesPath() {
        return masterImages;
    }

    public void setManuscriptPath(String manuscriptID) {
        Dotenv dotenv = Dotenv.load();
        manuscriptPath = Path.of(basePath.toString(),manuscriptID);
        masterImages = Path.of(dotenv.get("path_master_images"),manuscriptID);

    }
    private Path pdfPath(){
        return Paths.get( manuscriptPath.toString(), "pdf");
    }
    private Path ocrPath(){
        return Paths.get( manuscriptPath.toString(), "ocr");
    }
    private Path jpgPath(){
        return Paths.get( manuscriptPath.toString(), "jpg");
    }




    public Path getManuscriptPath() {
        return manuscriptPath;
    }

    public Path getMasterImagesFilePath(int orderNumber,PageType pageType){
        if (!getMasterImagesPath().toFile().exists()) {
            getMasterImagesPath().toFile().mkdir();
        }
        return Paths.get(getMasterImagesPath().toString(), manuscriptPath.getFileName() + "_" +String.format("%04d", orderNumber)  + "_" + pageType.token + ".tif");
    }


    public Path getFilePath(int orderNumber, FileType suffix, PageType pageType, ScanQuality scanQuality) {
        if (suffix == FileType.JPG) {
            if (!jpgPath().toFile().exists()) {
                jpgPath().toFile().mkdir();
            }
            if(scanQuality == ScanQuality.BAD){

                return Paths.get(jpgPath().toString(), manuscriptPath.getFileName() + "_" +String.format("%04d", orderNumber)  + "_" + pageType.token + "_ne" + ".jpg");
            }
            return Paths.get(jpgPath().toString(), manuscriptPath.getFileName() + "_" +String.format("%04d", orderNumber)  + "_" + pageType.token + ".jpg");
        }
        if (suffix == FileType.PDF) {
            if (!pdfPath().toFile().exists()) {
                pdfPath().toFile().mkdir();
            }
            if(scanQuality == ScanQuality.BAD){
                return Paths.get(pdfPath().toString(), manuscriptPath.getFileName() + "_" +String.format("%04d", orderNumber)  + "_" + pageType.token + "_ne" + ".pdf");
            }
            return Paths.get(pdfPath().toString(), manuscriptPath.getFileName() + "_" +String.format("%04d", orderNumber)  + "_" + pageType.token + ".pdf");
        }
        if (suffix == FileType.TXT) {
            if (!ocrPath().toFile().exists()) {
                ocrPath().toFile().mkdir();
            }
            if(scanQuality == ScanQuality.BAD){
                return Paths.get(ocrPath().toString(), manuscriptPath.getFileName() + "_" +String.format("%04d", orderNumber)  + "_" + pageType.token + "_ne" + ".txt");
            }
            return Paths.get(ocrPath().toString(), manuscriptPath.getFileName() + "_" +String.format("%04d", orderNumber)  + "_" + pageType.token + ".txt");
        }
        if (suffix == FileType.TIF) {
            if (!getManuscriptPath().toFile().exists()) {
                getManuscriptPath().toFile().mkdir();
            }
            if(scanQuality == ScanQuality.BAD){
                return Paths.get(manuscriptPath.toString(),  manuscriptPath.getFileName() + "_" +String.format("%04d", orderNumber)  + "_" + pageType.token + "_ne" + ".tif");
            }
            return Paths.get(manuscriptPath.toString(),  manuscriptPath.getFileName() + "_" +String.format("%04d", orderNumber)  + "_" + pageType.token + ".tif");
        }
        System.out.println("There was a Error in the PathHandler");
        return Paths.get(manuscriptPath.toString());

    }
}

