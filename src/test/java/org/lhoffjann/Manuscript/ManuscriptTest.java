
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
