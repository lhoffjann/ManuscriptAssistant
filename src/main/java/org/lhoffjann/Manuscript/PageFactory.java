package org.lhoffjann.Manuscript;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class PageFactory {
    public Page createPage(int cursor, int pageNumber, Path pathFront, Path pathBack, PathHandler pathHandler) throws Exception {
        FaksimileFactory faksimileFactory = new FaksimileFactory();


        return new Page(pageNumber,
                        faksimileFactory.createFaksimile(pathFront,cursor + 1, pathHandler),
                        faksimileFactory.createFaksimile(pathBack,cursor + 2, pathHandler));

    }


}


