package org.lhoffjann.Manuscript;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class XMLCreator {
    private List<String> ocrContent;

    private List<Integer> getCountEmptyLines(Path path) throws IOException {

        final BufferedReader br = new BufferedReader(new FileReader(path.toFile()));
        String line;
        List<Integer> emptyLines = null;
        int empty = 0;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) {
                empty++;
                emptyLines.add(empty);

            }
        }
        return emptyLines;
    }
    private void replaceEmptyLines(Path path, List<Integer> emptyLines){


    }




    private void createOCRContent(Manuscript manuscript) throws IOException {
        for (Page page : manuscript.getPageList()) {
            List<Integer> emptyLines = getCountEmptyLines(page.getFront().getOCRPath());

        }
    }



    public void createXML(Manuscript manuscript) throws IOException, SAXException, ParserConfigurationException {
        createOCRContent(manuscript);
        /**
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File("src/main/resources/MS_transcript.xml"));
        doc.getDocumentElement().normalize();
        System.out.println(doc);
        NodeList nodeList = doc.getElementsByTagName("msDesc");
        Node first = nodeList.item(0);

        System.out.println(first.getNodeName());
        System.out.println(first.getNodeType());
        System.out.println(first.getFirstChild());
        System.out.println(first.getPrefix());
        System.out.println(first.getAttributes().item(0));
         */





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