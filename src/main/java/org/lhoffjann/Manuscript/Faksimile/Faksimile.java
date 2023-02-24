package org.lhoffjann.Manuscript.Faksimile;

import org.lhoffjann.Manuscript.PathHandler;
import org.lhoffjann.Manuscript.enums.FileType;
import org.lhoffjann.Manuscript.enums.PageType;
import org.lhoffjann.Manuscript.enums.ScanQuality;

import java.io.IOException;
import java.nio.file.Path;

public class Faksimile {

    private int orderNumber;
    private PageType pageParameter;

    final private PathHandler pathHandler;

    private ScanQuality scanQuality;
    public Faksimile(int orderNumber, String uniqueID, PageType pageParameter, PathHandler pathHandler, ScanQuality scanQuality) {

        this.orderNumber = orderNumber;
        this.pageParameter = pageParameter;
        this.pathHandler = pathHandler;
        this.scanQuality = scanQuality;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
    public PageType getPageParameter() {
        return pageParameter;
    }

    public ScanQuality getScanQuality() {
        return scanQuality;
    }

    public void setScanQuality(ScanQuality scanQuality) {
        this.scanQuality = scanQuality;
    }
    public void changeScanQuality(ScanQuality scanQuality) throws IOException {
        for (FileType fileType : FileType.values()) {
            System.out.println(fileType);
            Path filePath = pathHandler.getFilePath(orderNumber, fileType, this.pageParameter, this.scanQuality);
            if (filePath.toFile().exists()) {
                FaksimileHelper.renameFiles(filePath, pathHandler.getFilePath(orderNumber, fileType, pageParameter, scanQuality));
            }
        }
        setScanQuality(scanQuality);
    }

    public void changePageParameter(PageType pageParameter) throws IOException {
        for (FileType fileType : FileType.values()) {

            Path filePath = pathHandler.getFilePath(orderNumber, fileType, this.pageParameter, scanQuality);
            if (filePath.toFile().exists()) {
                FaksimileHelper.renameFiles(filePath, pathHandler.getFilePath(orderNumber, fileType, pageParameter,scanQuality));
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
    public Path getMasterImagesPath(){return pathHandler.getMasterImagesFilePath(orderNumber, pageParameter);}
}
