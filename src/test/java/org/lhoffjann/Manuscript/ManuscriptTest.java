
package org.lhoffjann.Manuscript;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;


import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ManuscriptTest {
    private Manuscript actualManuscript;
    private ManuscriptAssistant testManuscriptAssistant;

    @BeforeEach
    public void setUp() throws Exception {
        ManuscriptFactory testManuscriptFactory = new ManuscriptFactory();
        actualManuscript = testManuscriptFactory.createManuscript("MS_2877_test");
    }

        /**
        FaksimileFactory faksimileFactory = new FaksimileFactory();
        testManuscript = new Manuscript("MS_2877_test");
        List<Page> testPages = Arrays.asList(
                new Page(1, faksimileFactory.createFaksimile(Paths.get("/mnt/larchiv/manuskripte_scans_fuer_digitalisierung/scans/MS_2877_test/MS_2877_0001_V.tif"), 1), faksimileFactory.createFaksimile(Paths.get("/mnt/larchiv/manuskripte_scans_fuer_digitalisierung/scans/MS_2877_test/MS_2877_0002_RL.tif"), 2)),
                new Page(2, faksimileFactory.createFaksimile(Paths.get("/mnt/larchiv/manuskripte_scans_fuer_digitalisierung/scans/MS_2877_test/MS_2877_0003_V.tif"), 3), faksimileFactory.createFaksimile(Paths.get("/mnt/larchiv/manuskripte_scans_fuer_digitalisierung/scans/MS_2877_test/MS_2877_0004_RL.tif"), 4)),
                new Page(3, faksimileFactory.createFaksimile(Paths.get("/mnt/larchiv/manuskripte_scans_fuer_digitalisierung/scans/MS_2877_test/MS_2877_0005_V.tif"), 5), faksimileFactory.createFaksimile(Paths.get("/mnt/larchiv/manuskripte_scans_fuer_digitalisierung/scans/MS_2877_test/MS_2877_0006_RL.tif"), 6)),
                new Page(4, faksimileFactory.createFaksimile(Paths.get("/mnt/larchiv/manuskripte_scans_fuer_digitalisierung/scans/MS_2877_test/MS_2877_0007_F.tif"), 7), faksimileFactory.createFaksimile(null, 8)));
        testManuscript.setPages(testPages);
    }

        @Test
        public void createManuscript () {
            System.out.println("this is me");
            for (int i = 0; i < testManuscript.getPageList().size(); i++) {
                assertEquals(testManuscript.getPageList().get(i).getId(), actualManuscript.getPageList().get(i).getId());
                assertEquals(testManuscript.getPageList().get(i).getFront().getOrderNumber(), actualManuscript.getPageList().get(i).getFront().getOrderNumber());
                assertEquals(testManuscript.getPageList().get(i).getFront().getUniqueID(), actualManuscript.getPageList().get(i).getFront().getUniqueID());
                assertEquals(testManuscript.getPageList().get(i).getFront().getPath(), actualManuscript.getPageList().get(i).getFront().getPath());
                assertEquals(testManuscript.getPageList().get(i).getBack().getOrderNumber(), actualManuscript.getPageList().get(i).getBack().getOrderNumber());
                assertEquals(testManuscript.getPageList().get(i).getBack().getUniqueID(), actualManuscript.getPageList().get(i).getBack().getUniqueID());
                assertEquals(testManuscript.getPageList().get(i).getBack().getPath(), actualManuscript.getPageList().get(i).getBack().getPath());
            }
        }
**/
    @Test
    public void checkPageTypeIdentifier () {
    for( Page page: actualManuscript.getPageList()){
        //assertEquals(PageType.FRONT,page.front.getPageParameter());
        assertEquals(PageType.BACK_EMPTY,page.getBack().getPageParameter());
        }
    }
    @Test
    public void checkSwapFrontAndBack() throws Exception {
        testManuscriptAssistant = new ManuscriptAssistant();
        String identifier = actualManuscript.getPage(0).getFront().getUniqueID();
        testManuscriptAssistant.swapFrontAndBack(actualManuscript, 0);
        assertEquals(identifier, actualManuscript.getPage(0).getBack().getUniqueID());
        }
    @Test
    public void checkSwapSecondAndThirdPage() throws Exception {
        testManuscriptAssistant = new ManuscriptAssistant();
        String identifier0 = actualManuscript.getPage(1).getFront().getUniqueID();
        String identifier1 = actualManuscript.getPage(1).getBack().getUniqueID();
        String identifier2 = actualManuscript.getPage(2).getFront().getUniqueID();
        String identifier3 = actualManuscript.getPage(2).getBack().getUniqueID();
        testManuscriptAssistant.swapPage(actualManuscript, 1,2);
        assertEquals(identifier2, actualManuscript.getPage(1).getFront().getUniqueID());
        assertEquals(identifier3, actualManuscript.getPage(1).getBack().getUniqueID());
        assertEquals(identifier0, actualManuscript.getPage(2).getFront().getUniqueID());
        assertEquals(identifier1, actualManuscript.getPage(2).getBack().getUniqueID());
    }
    @Test
    public void xmltester() throws IOException, ParserConfigurationException, SAXException {
        testManuscriptAssistant = new ManuscriptAssistant();
        testManuscriptAssistant.copyTIFFsToMasterImage(actualManuscript);

    }

    }
