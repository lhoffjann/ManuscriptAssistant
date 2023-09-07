# Imports
from corner_detect import return_diagonal_lines
from pathlib import Path
from imagefiles import ImageFiles, PageTypes
from PathHandler import ManuscriptPathHandler
from ocrgenerator import OCRgenerator





# A page consists out of 2 Images.
# The front has an uneven ID the back has an even id. the id of the back is +1 of the front
class Page:

    def __init__(self, id, front: Path, back: Path, path_handler: ManuscriptPathHandler):
        self.id = id
        self.front = ImageFiles(front.name, front, PageTypes.FRONT, path_handler)
        self.back = ImageFiles(back.name, back, PageTypes.BACK_EMPTY, path_handler)
        self.needsManualCheck = False

    @property
    def front_page_name(self):
        return self.front.id

    # todo das muss sowohl fuer front als auch back passieren

    # Here the functions start
    def check_bad_scan(self):
        # Assumption: Diagonal lines on front and back indicates that there might be a bad scan
        if return_diagonal_lines(self.front.path) and return_diagonal_lines(self.back.path):
            self.needsManualCheck = True

    def create_pdf(self):
        for i in [self.front, self.back]:
            i.create_pdf()

    def create_jpg(self):
        for i in [self.front, self.back]:
            i.create_jpg()

    def create_ocr(self):
        for i in [self.front, self.back]:
            OCRgenerator(i).create_ocr()
    def review_page(self):
        for i in [self.front, self.back]:
            i.review_image()

    def get_ocr(self):
        return [OCRgenerator(i).add_page_break_and_line_break() for i in [self.front, self.back]]


    def get_faksimile(self) -> list:
        return [i.get_faksimile() for i in [self.front, self.back]]

    def create_final_pdf(self):
        list_of_pdf = []
        list_of_pdf.append(self.front.create_final_pdf())
        list_of_pdf.append(self.back.create_final_pdf())
        return list_of_pdf

# TODO: Damit koennte man sich mal auseinander setzen
"""
    def __str__():
        return f"{self.front} - {self.back}"

    def __eq__(self, other: Page):
        if self.front == other.front and self.back == other.back:
            return True

        return False
"""
