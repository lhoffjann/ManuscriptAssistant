package org.lhoffjann.Manuscript;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class XMLCreator {
    private List<String> ocrContent = new ArrayList<>();

    private void readOCRFile(Faksimile faksimile) throws IOException {
        if (faksimile.getOCRPath().toFile().exists()) {
            final BufferedReader br = new BufferedReader(new FileReader(faksimile.getOCRPath().toFile()));
            List<String> pageContent = new ArrayList<>();
            pageContent.add(addPageBreaks(faksimile));
            String line;
            while ((line = br.readLine()) != null) {
                pageContent.add(addLineBreaks(line));
            }
            ocrContent.addAll(pageContent);
        }
    }

    public String addPageBreaks(Faksimile faksimile) {
        String pageBreak = "<pb type =\"" + faksimile.getPageParameter().side + "\" facs=\"#" + faksimile.getName()
                + "\"/>";
        return pageBreak;
    }

    public String addLineBreaks(String line) {
        if (!line.trim().isEmpty()) {
            if (line.charAt(line.length() - 1) == '-') {
                return line.substring(0, line.length() - 1) + "<lb type=\"inWord\"/>";
            }
            return line + "<lb/>";
        }
        return line;
    }

    private void prepareOCR(Manuscript manuscript) throws IOException {

        for (Page page : manuscript.getPageList()) {
            if (page.getFront().getPageParameter() != PageType.FRONT_COLOUR_CARD) {
                readOCRFile(page.getFront());
                readOCRFile(page.getBack());
            }
        }
    }

    private String buildGraphic(Faksimile faksimile) {
        String fileName = FilenameUtils.removeExtension(faksimile.getOCRPath().getFileName().toString());
        String rend = faksimile.getPageParameter().rend;
        return "<graphic xmlns=\"http://www.tei-c.org/ns/1.0\" xml:id=\"" + fileName + "\" url=\"" + fileName
                + "\" rend=\"" + rend + "\"/>";
    }

    public void createXML(Manuscript manuscript) throws IOException, JDOMException {
        prepareOCR(manuscript);
        Dotenv dotenv = Dotenv.load();
        Path pathXmlPath = Path.of(dotenv.get("path_xml_template"));
        SAXBuilder sax = new SAXBuilder();

        Document doc;
        try {
            doc = sax.build(pathXmlPath.toFile());
        } catch (Exception e) {
            System.out.println("The XML could not be opened.");
            return;
        }

        Element rootNode = doc.getRootElement();
        Element teiHeaderNode = rootNode.getChild("teiHeader", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        Element facsimileNode = rootNode.getChild("facsimile", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        Element textNode = rootNode.getChild("text", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        Element bodyNode = textNode.getChild("body", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        Element div1Node = bodyNode.getChild("div", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        Element div2Node = div1Node.getChild("div", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));

        for (Page page : manuscript.getPageList()) {
            if (page.getFront().getPageParameter() != PageType.FRONT_COLOUR_CARD) {
                String front = buildGraphic(page.getFront());
                StringReader stringReader = new StringReader(front);
                Document graphicsFront = sax.build(stringReader);
                Element rootE = graphicsFront.getRootElement();
                facsimileNode.addContent(rootE.detach());
                String back = buildGraphic(page.getBack());
                StringReader stringReader1 = new StringReader(back);
                Document graphicsBack = sax.build(stringReader1);
                Element root = graphicsBack.getRootElement();
                facsimileNode.addContent(root.detach());
            }
        }

        String finalOCR = "<div xmlns=\"http://www.tei-c.org/ns/1.0\">" + String.join("", ocrContent) + "</div>";
        finalOCR = finalOCR.replace("&", " &amp;");
        StringReader stringReader = new StringReader(finalOCR);
        Document ocr = sax.build(stringReader);
        Element rootE = ocr.getRootElement();
        div2Node.setContent(rootE.cloneContent());
        XMLOutputter xmlOutput = new XMLOutputter();

        try (FileOutputStream output = new FileOutputStream(
                Path.of(dotenv.get("path_xml_folder"), manuscript.getManuscriptID()).toString() + ".xml")) {
            xmlOutput.output(doc, output);
            System.out.println("XML created");
        }
    }
}
