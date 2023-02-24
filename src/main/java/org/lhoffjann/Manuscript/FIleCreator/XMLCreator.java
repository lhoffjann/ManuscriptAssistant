package org.lhoffjann.Manuscript.FIleCreator;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.lhoffjann.Manuscript.Faksimile.Faksimile;
import org.lhoffjann.Manuscript.Manuscript.Manuscript;
import org.lhoffjann.Manuscript.Page.Page;
import org.lhoffjann.Manuscript.enums.PageType;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class XMLCreator {
    final private List<String> ocrContent = new ArrayList<>();
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

    private String addPageBreaks(Faksimile faksimile) {
        return "<pb type =\"" + faksimile.getPageParameter().side + "\" facs=\"#" +faksimile.getTIFPath().getFileName()+ "\"/>";
    }

    public String addLineBreaks(String line) {
        if(!line.trim().isEmpty()){
            if (line.charAt(line.length() - 1) == '-'){
                line = line + "<lb type=\"inWord\"/>";
            }else{
                line = line + "<lb/>";
            }
        }
        return line;
    }
    private void prepareOCR(Manuscript manuscript) throws IOException {

        for (Page page:manuscript.getPageList()){
            if(page.getFront().getPageParameter() != PageType.FRONT_COLOUR_CARD) {
                readOCRFile(page.getFront());
                readOCRFile(page.getBack());
            }
        }
        replaceEmptyLines();
    }

    private List<Integer> getCountEmptyLines(){
        List<Integer> emptyLines = new ArrayList<>();
        int empty = 0;
        for(String string : ocrContent){
            //System.out.println(string);
            empty++;
            if (string.trim().isEmpty()){
                emptyLines.add(empty - 1);
            }
        }
        return emptyLines;
    }


    private void replaceEmptyLines() {
        List<Integer> emptyLines = getCountEmptyLines();
        int openingParagraph = 0;
        int closingParagraph = 0;
        int bothParagraphs = 0;
        int NumberOfEmptyLines = emptyLines.size();

        Pattern pattern = Pattern.compile("<pb ", Pattern.CASE_INSENSITIVE);

        while (emptyLines.size() > 0) {
            if (emptyLines.size() == NumberOfEmptyLines) {
                ocrContent.set(emptyLines.remove(0), "<p>");
                openingParagraph++;
            } else if (pattern.matcher(ocrContent.get(emptyLines.get(0) + 1)).find()) {
                ocrContent.set(emptyLines.remove(0), "</p>");
                closingParagraph++;
            } else if (pattern.matcher(ocrContent.get(emptyLines.get(0) - 1)).find()) {
                ocrContent.set(emptyLines.remove(0), "<p>");
                openingParagraph++;
            } else {
                ocrContent.set(emptyLines.remove(0), "</p><p>");
                bothParagraphs++;
            }


        }

        System.out.println(openingParagraph);
        System.out.println(closingParagraph);
        System.out.println(bothParagraphs);
    }
    private String buildGraphic(Faksimile faksimile){
        String fileName = FilenameUtils.removeExtension(faksimile.getOCRPath().getFileName().toString());
        String rend = faksimile.getPageParameter().rend;
        return "<graphic xmlns=\"http://www.tei-c.org/ns/1.0\" xml:id=\"" + fileName + "\" url=\""+fileName+"\" rend=\""+ rend +"\"/>";

    }


    public void createXML(Manuscript manuscript) throws IOException,JDOMException {
        prepareOCR(manuscript);

        SAXBuilder sax = new SAXBuilder();
        Document doc = sax.build(getClass().getResourceAsStream("/MS_transcript.xml"));
        Element rootNode = doc.getRootElement();
        Element facsimileNode = rootNode.getChild("facsimile", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        Element textNode = rootNode.getChild("text", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        Element bodyNode = textNode.getChild("body", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        Element div1Node = bodyNode.getChild("div", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        Element div2Node = div1Node.getChild("div", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));

        for (Page page: manuscript.getPageList()){
            if(page.getFront().getPageParameter() != PageType.FRONT_COLOUR_CARD) {
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

        // write to console
        // xmlOutput.output(doc, System.out);

        // write to a file
        Dotenv dotenv = Dotenv.load();

        try (FileOutputStream output =
                     new FileOutputStream(Path.of(dotenv.get("path_xml_folder") , manuscript.getManuscriptID())+ ".xml")) {
            xmlOutput.output(doc, output);
            System.out.println("XML created");
        }

    }

}
