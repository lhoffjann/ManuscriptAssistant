package org.lhoffjann.Manuscript.Manuscript;
import org.lhoffjann.Manuscript.Page.Page;
import org.lhoffjann.Manuscript.Page.PageFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class ManuscriptFactory {
    public Manuscript createManuscript(String manuscriptID) throws Exception {
        Manuscript manuscript = new Manuscript(manuscriptID);
        List<Page> pageList = createPageList(manuscript);
        manuscript.setPages(pageList);
        return manuscript;
    }
    private List<Path> createPathList(Path path) throws IOException {
        try (Stream<Path> paths = Files.list(path)) {
            return paths
                    .filter(Files::isRegularFile)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }
    private List<Page> createPageList(Manuscript manuscript) throws Exception {
        PageFactory pageFactory= new PageFactory();
        List<Page> pageList = new ArrayList<>();
        List<Path> pathList = createPathList(manuscript.getManuscriptPath());
        int pageCount = pathList.size() - 1;
        Path farbKarte = null;
        if (!(pathList.size() % 2 == 0)) {
            farbKarte = pathList.get(pathList.size() - 1);
            pathList.remove(pathList.size() - 1);
        }
        int pageNumber = 1;
        for (int i = 0; i <= pathList.size() - 2; i += 2) {
            pageList.add(pageFactory.createPage(i, pageNumber, pathList.get(i),pathList.get(i+1), manuscript.pathHandler));
            pageNumber++;
        }
        if (farbKarte != null) {
            pageList.add(pageFactory.createPage(pageCount, pageNumber, farbKarte,null, manuscript.pathHandler));
        }
        return pageList;
    }


}