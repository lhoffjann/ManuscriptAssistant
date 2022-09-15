package org.lhoffjann.Manuscript;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String pageBreak = "<pb type =\"" + faksimile.getPageParameter().side + "\" facs=\"#" +faksimile.getTIFPath().getFileName()+ "\"/>";
        return pageBreak;
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
        if (emptyLines != null){
            int NumberOfEmptyLines = emptyLines.size();

            Pattern pattern = Pattern.compile("<pb ", Pattern.CASE_INSENSITIVE);

            while (emptyLines.size() > 0) {
                //System.out.println("hello");
                //System.out.println(ocrContent.get(emptyLines.get(0) + 1));
                //System.out.println(ocrContent.get(emptyLines.get(0) - 1));
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
        Document doc = sax.build(new File("src/main/resources/MS_transcript.xml"));
        Element rootNode = doc.getRootElement();
        System.out.println(rootNode);
        Element teiHeaderNode= rootNode.getChild("teiHeader", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
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
                System.out.println(root.getContent());
                facsimileNode.addContent(root.detach());
                System.out.println(front);
                System.out.println(back);
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
                     new FileOutputStream(Path.of(dotenv.get("path_xml_folder") , manuscript.getManuscriptID()).toString()+ ".xml")) {
            xmlOutput.output(doc, output);
        }







    }

}
/*
    def add_pb_and_lb(self, content_of_ocr) -> list:
        processed_lines = [self.get_pagebreak()]
        for line in content_of_ocr:
            if line:
                if line[-1] == '-':
                    processed_lines.append(line + LineEnd.LINE_WITH_DIVIS.value)
                else:
                    processed_lines.append(line + LineEnd.LINE_WITHOUT_DIVIS.value)
            else:
                processed_lines.append(line)
        return processed_lines


 *     class XMLGenerator:
    def __init__(self, manuscript: Manuscript):
        self.manuscript = manuscript
        self.xml_struct = self._load_xml_struct()
        self.list_of_faksimile = self.manuscript.get_list_of_faksimile()
        self.ocr_content = self.manuscript.get_ocr()
        self.xml_exists = self.manuscript.xml_file.exists()

    def _load_xml_struct(self):
        parser = etree.XMLParser(recover=True)
        xml_structure = etree.parse("MS_transcript.xml", parser)
        return xml_structure

    def add_paragraphs_to_ocr_content(self):
        empty_lines = []
        count_lines = 0
        for line in self.ocr_content:
            if not line:
                empty_lines.append(count_lines)
            count_lines += 1
        number_empty_lines = len(empty_lines)

        conut_p = 0
        count_p_close = 0
        count_both = 0
        while empty_lines:
            if len(empty_lines) == number_empty_lines:
                self.ocr_content[empty_lines.pop(0)] = '<p>'
                conut_p += 1
            elif re.match('<pb ', self.ocr_content[empty_lines[0] + 1]):
                self.ocr_content[empty_lines.pop(0)] = '</p>'
                count_p_close += 1
            elif re.match('<pb ', self.ocr_content[empty_lines[0] - 1]) :
                self.ocr_content[empty_lines.pop(0)] = '<p>'
                conut_p += 1
            else:
                self.ocr_content[empty_lines.pop(0)] = '</p><p>'
                count_both += 1
        print(f"Inserted <p>: {conut_p}\n  Inserted <\\p>: {count_p_close}\n")
        if conut_p != count_p_close:
            print("Warning: The XML generator will likely fail because of mismatching paragraph tags.")

    def create_xml_entry(self):
        # Checking if XML already exists and checking with user if it should be overwritten when existing
        print(self.manuscript.xml_file)
        if self.xml_exists:
            xml_exists = inquirer.select(
                message="The XML file seems to be already created. Do you want to overwrite it?",
                choices=[
                    Choice(0, name="No, keep the existing one."),
                    Choice(1, name="Yes, create a new one.")],
                default=0,
            ).execute()
            if xml_exists == 0:
                print('Skipping: generating XML')
                return
            print('Creating new XML')

        for elem in self.xml_struct.iterfind("//*"):
            if elem.tag == "{http://www.tei-c.org/ns/1.0}msDesc":
                elem.attrib['{http://www.w3.org/XML/1998/namespace}id'] = self.manuscript.id
            if elem.tag == "{http://www.tei-c.org/ns/1.0}div" and elem.attrib.has_key('{http://www.w3.org/XML/1998/namespace}id'):
                try:
                    text = ''.join(self.ocr_content)
                    s2 = "<div>" + text + "</div>"
                    text = etree.fromstring(s2)
                    text.attrib['{http://www.w3.org/XML/1998/namespace}id'] = ""
                    elem.getparent().replace(elem, text)
                except Exception as e:
                    print("Could not insert OCR. Please check your paragraph declaration and rerun the XML "
                          "generator.")
                    print(f"Error message for Reference: {str(e)}")
            if elem.tag == "{http://www.tei-c.org/ns/1.0}facsimile":
                list_of_graphics = self.manuscript.get_list_of_faksimile()
                elem.getparent().replace(elem, list_of_graphics)

    def write_xml_structure_to_file(self):
        self.xml_struct.write(self.manuscript.xml_file, pretty_print=True, encoding='utf-8', xml_declaration=True)

 */