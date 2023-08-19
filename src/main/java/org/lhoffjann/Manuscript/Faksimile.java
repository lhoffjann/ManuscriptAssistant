package org.lhoffjann.Manuscript;

import java.io.IOException;
import java.nio.file.Path;

public class Faksimile {

    private int orderNumber;
    private final String uniqueID;
    private PageType pageParameter;

    private PathHandler pathHandler;

    private ScanQuality scanQuality;
    public Faksimile(int orderNumber, String uniqueID, PageType pageParameter, PathHandler pathHandler, ScanQuality scanQuality) {

        this.orderNumber = orderNumber;
        this.uniqueID = uniqueID;
        this.pageParameter = pageParameter;
        this.pathHandler = pathHandler;
        this.scanQuality = scanQuality;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
    public PageType getPageParameter() {
        return pageParameter;
    }
    public PathHandler getPathHandler() {
        return pathHandler;
    }

    public ScanQuality getScanQuality() {
        return scanQuality;
    }

    public void setScanQuality(ScanQuality scanQuality) {
        this.scanQuality = scanQuality;
    }
    public void changeScanQuality(ScanQuality scanQuality) throws IOException {
        FaksimileHelper faksimileHelper = new FaksimileHelper();
        for (FileType fileType : FileType.values()) {
            System.out.println(fileType);
            Path filePath = pathHandler.getFilePath(orderNumber, fileType, this.pageParameter, this.scanQuality);
            if (filePath.toFile().exists()) {
                faksimileHelper.renameFiles(filePath, pathHandler.getFilePath(orderNumber, fileType, pageParameter, scanQuality));
            }
        }
        setScanQuality(scanQuality);
    }

    public void changePageParameter(PageType pageParameter) throws IOException {
        FaksimileHelper faksimileHelper = new FaksimileHelper();
        for (FileType fileType : FileType.values()) {

            Path filePath = pathHandler.getFilePath(orderNumber, fileType, this.pageParameter, scanQuality);
            if (filePath.toFile().exists()) {
                faksimileHelper.renameFiles(filePath, pathHandler.getFilePath(orderNumber, fileType, pageParameter,scanQuality));
            }
        }
        setPageParameter(pageParameter);
    }
    public void setPageParameter (PageType pageParameter) {
        this.pageParameter = pageParameter;
    }
    public Path getOCRPath(){
        return pathHandler.getFilePath(orderNumber, FileType.TXT, pageParameter,scanQuality);
    }
    public Path getPDFPath(){
        return pathHandler.getFilePath(orderNumber, FileType.PDF, pageParameter, scanQuality);
    }
    public Path getJPGPath(){
        return pathHandler.getFilePath(orderNumber, FileType.JPG, pageParameter, scanQuality);
    }
    public Path getTIFPath(){
        return pathHandler.getFilePath(orderNumber, FileType.TIF, pageParameter, scanQuality);
    }
    public String getName(){
        return pathHandler.getFileName(orderNumber, pageParameter, scanQuality);
    }
    public Path getMasterImagesPath(){return pathHandler.getMasterImagesFilePath(orderNumber, pageParameter);}
}
