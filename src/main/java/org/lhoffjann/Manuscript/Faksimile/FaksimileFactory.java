package org.lhoffjann.Manuscript.Faksimile;

import org.lhoffjann.Manuscript.ChecksumCalculator;
import org.lhoffjann.Manuscript.FIleCreator.FileCreator;
import org.lhoffjann.Manuscript.PathHandler;
import org.lhoffjann.Manuscript.enums.FileType;
import org.lhoffjann.Manuscript.enums.PageType;
import org.lhoffjann.Manuscript.enums.ScanQuality;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FaksimileFactory {
    private ScanQuality checkScanQuality(Path path){
        if(path == null){
            return null;
        }
        Pattern pattern = Pattern.compile("ne", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(path.getFileName().toString());
        boolean matchFound = matcher.find();
        if (matchFound){
            return ScanQuality.BAD;
        }
    return ScanQuality.GOOD;
    }
    private PageType identifyPageType(Path path, int orderNumber){
        if (path == null){
            return PageType.BACK_EMPTY;
        }
        for(PageType pageParameter: PageType.values()) {
            Pattern pattern = Pattern.compile(pageParameter.token, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(path.getFileName().toString());
            boolean matchFound = matcher.find();
            if (matchFound){
                return pageParameter;
            }
        }
        if(orderNumber%2 == 0){
            return PageType.BACK_EMPTY;
        }else{
            return PageType.FRONT;
        }
    }
    public Faksimile createFaksimile(Path path, int orderNumber, PathHandler pathHandler) throws Exception {
        ChecksumCalculator checksumCalculator = new ChecksumCalculator();
        PageType pageParameter = identifyPageType(path, orderNumber);
        ScanQuality scanQuality = checkScanQuality(path);
        Path pathTif = pathHandler.getFilePath(orderNumber, FileType.TIF, pageParameter, scanQuality);
        Path pathJPG = pathHandler.getFilePath(orderNumber,FileType.JPG, pageParameter, scanQuality);
        if(!pathTif.toFile().exists() && path != null){
            FaksimileHelper.renameFiles(path, pathTif);
        }
        String uniqueIdentifier = null;
        if(pathTif.toFile().exists()){
            if(!pathJPG.toFile().exists()){
                FileCreator.createJPG(pathTif,pathJPG);
            }
            uniqueIdentifier = checksumCalculator.generateUniqueIdentifier(pathJPG);
        }
        System.out.println("Loading faksimile: " +  orderNumber);
        System.out.println(uniqueIdentifier);
        System.out.println(path);
        Faksimile faksimile = new Faksimile(orderNumber, uniqueIdentifier, pageParameter, pathHandler, scanQuality);
        if(faksimile.getTIFPath().toFile().exists()) {
            if (!faksimile.getJPGPath().toFile().exists()) {
                FileCreator.createJPG(faksimile);
            }
            if(!faksimile.getPDFPath().toFile().exists()){
                FileCreator.createPDF(faksimile);
            }
        }
        if(faksimile.getJPGPath().toFile().exists() && !faksimile.getOCRPath().toFile().exists()) {
            FileCreator.createOCR(faksimile);
        }
        return faksimile;
    }



}
