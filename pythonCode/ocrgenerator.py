from src.imagefiles import ImageFiles
from enum import Enum
from src.textExtractor import detect_text

class LineEnd(Enum):
    LINE_WITH_DIVIS = '<lb type="inWord"/>'
    LINE_WITHOUT_DIVIS = '<lb/>'


class OCRgenerator:
    def __init__(self, image_file):
        self.image_file = image_file
        self.ocr_content: list[str]
        if not self.image_file.path_ocr.exists():
            self.create_ocr()
        if self.image_file.path_ocr.exists():
            with open(str(self.image_file.path_ocr), 'r', encoding="utf-8") as f:
                self.ocr_content = f.read().splitlines()

    def create_ocr(self) -> None:
        if self.image_file.path_ocr.exists():
            return
        ocr = detect_text(str(self.image_file.path_jpg))
        if not ocr == '0':
            self.image_file.ocr_has_no_content = False
        if ocr == '0':
            ocr = ""
        with open(str(self.image_file.path_ocr), 'w', encoding='utf-8') as f:
            f.write(ocr)



    def add_page_break_and_line_break(self) -> list:
        processed_lines = [f'<pb type ="{self.image_file.page_parameter.side}" facs="#{self.image_file.path.stem}"/>']
        for line in self.ocr_content:
            if line:
                if line[-1] == '-':
                    processed_lines.append(line + LineEnd.LINE_WITH_DIVIS.value)
                else:
                    processed_lines.append(line + LineEnd.LINE_WITHOUT_DIVIS.value)
            else:
                processed_lines.append(line)
        return processed_lines

