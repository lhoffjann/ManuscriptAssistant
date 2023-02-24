package org.lhoffjann.Manuscript.Page;

import org.lhoffjann.Manuscript.Faksimile.FaksimileFactory;
import org.lhoffjann.Manuscript.PathHandler;

import java.nio.file.Path;

public class PageFactory {
    public Page createPage(int cursor, int pageNumber, Path pathFront, Path pathBack, PathHandler pathHandler) throws Exception {
        FaksimileFactory faksimileFactory = new FaksimileFactory();
        return new Page(pageNumber,
                        faksimileFactory.createFaksimile(pathFront,cursor + 1, pathHandler),
                        faksimileFactory.createFaksimile(pathBack,cursor + 2, pathHandler));
    }
}


