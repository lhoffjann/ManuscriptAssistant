package org.lhoffjann.Manuscript.Manuscript;
import org.lhoffjann.Manuscript.Page.Page;
import org.lhoffjann.Manuscript.PathHandler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
public class Manuscript{
    private  final String manuscriptID;
    private List<Page> pages = new ArrayList<>();
    public PathHandler pathHandler;
    public Manuscript(String manuscriptID){
        this.manuscriptID = manuscriptID;
        this.pathHandler = new PathHandler();
        this.pathHandler.setManuscriptPath(this.manuscriptID);
    }
    public String getManuscriptID() {
        return this.manuscriptID;
    }
    public void setPages(List<Page> pages) {
        this.pages = pages;
    }
    public List<Page> getPageList(){
          return this.pages;
    }
    public Path getManuscriptPath(){
        return pathHandler.getManuscriptPath();
    }
    public Page getPage(int pageNumber){
        return pages.get(pageNumber);
    }
}


