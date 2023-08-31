from pathlib import Path
import cv2
import re
from PIL import Image
from src.PathHandler import FileType, ManuscriptPathHandler
import os, sys, subprocess
from InquirerPy import inquirer
from InquirerPy.base.control import Choice
from enum import Enum, auto
from lxml import etree

class PageTypes(Enum):
    FRONT = ('V', "vorderseite", "content")
    BACK_WITH_REFERENCE_TO_FRONT = ('RV', 'rueckseite', "content")
    BACK_WITH_HANDWRITTEN_NOTE_NO_REFERENCE_TO_FRONT = ('RR', 'rueckseite', "content_irrelevant")
    BACK_WITH_PRINT_NO_REFERENCE_TO_FRONT = ('RD', 'rueckseite', "content_irrelevant")
    BACK_WITH_EXTENSION_OF_FRONT = ('RT', 'rueckseite', "content")
    BACK_EMPTY = ('RL', 'rueckseite', "empty")

    def __init__(self, short, side, rend):
        self.short = short
        self.side = side
        self.rend = rend


class ScanQuality(Enum):
    GOOD_SCAN = ""
    BAD_SCAN = "_ne"


class Check(Enum):
    NOT_DONE = auto()
    DONE = auto()





class ImageFiles:

    def __init__(self, id: str, path: Path, page_parameter, path_handler: ManuscriptPathHandler):
        self.id = id
        self.path = path
        self.page_parameter: FileType = page_parameter
        self.path_handler = path_handler
        self.path_jpg = self.path_handler.generate_new_file_path(self.path, FileType.JPG)
        self.path_ocr = self.path_handler.generate_new_file_path(self.path, FileType.TXT)
        self.path_pdf = self.path_handler.generate_new_file_path(self.path, FileType.PDF)
        self.ocr_has_no_content = True
        self.good_scan = None
        self.check_done: Check = Check.NOT_DONE

    def create_pdf(self) -> None:
        if self.path_pdf.exists():
            return
        image = Image.open(self.path_jpg)
        im = image.convert('RGB')
        im.save(self.path_pdf, quality = 20)

    def delete_files(self):
        raise NotImplementedError

    def create_jpg(self) -> None:
        if self.path_jpg.exists():
            return
        read_im = cv2.imread(str(self.path))
        cv2.imwrite(str(self.path_jpg), read_im, [int(cv2.IMWRITE_JPEG_QUALITY), 50])

    def check_if_review_was_done_and_set_parameter(self) -> None:
        for page_type in PageTypes:
            for good_scan in ScanQuality:
                if bool(re.search(page_type.short + good_scan.value, self.path.stem)):
                    self.page_parameter = page_type
                    self.check_done = Check.DONE

    def review_image(self):
        self.check_if_review_was_done_and_set_parameter()
        skip_scan = 1
        if self.check_done == Check.DONE:
            skip_scan = inquirer.select(
                message=f'{self.path.stem} seems to be already reviewed. Do you want to skip?',
                choices=[Choice(1, name="Yes, I want to skip the review"),
                         Choice(0, name="No, I want to review it again")],
                default=ScanQuality.GOOD_SCAN,
            ).execute()
            if skip_scan == 1:
                print(f'Skipping {self.path.stem}...')
                return
        if not self.path_ocr.exists():
            raise FileNotFoundError("There is no OCR of this file")
        if not self.path.exists():
            raise FileNotFoundError("There is no Scan to review")
        if not self.path_jpg.exists():
            raise FileNotFoundError("There is no JPG of this File")
        pdf_viewer = "xdg-open"
        editor = "atom"
        if sys.platform == "win32":
            os.startfile(self.path_jpg)
            os.startfile(self.path_ocr)
        else:
            subprocess.Popen([pdf_viewer, self.path_jpg])
            subprocess.Popen([editor, self.path_ocr])

        self.good_scan = inquirer.select(
            message="Is the scan good?",
            choices=[Choice(ScanQuality.GOOD_SCAN, name="Yes, the scan is fine"),
                     Choice(ScanQuality.BAD_SCAN, name="No, the scan has to be redone")],
            default=ScanQuality.GOOD_SCAN,
        ).execute()

        self.page_parameter = inquirer.select(
            message="What kind of page is it?",
            choices=[
                Choice(PageTypes.FRONT, name=PageTypes.FRONT.short),
                Choice(PageTypes.BACK_EMPTY, name=PageTypes.BACK_EMPTY.short),
                Choice(PageTypes.BACK_WITH_PRINT_NO_REFERENCE_TO_FRONT,
                       name=PageTypes.BACK_WITH_PRINT_NO_REFERENCE_TO_FRONT.short),
                Choice(PageTypes.BACK_WITH_REFERENCE_TO_FRONT, name=PageTypes.BACK_WITH_REFERENCE_TO_FRONT.short),
                Choice(PageTypes.BACK_WITH_HANDWRITTEN_NOTE_NO_REFERENCE_TO_FRONT,
                       name=PageTypes.BACK_WITH_HANDWRITTEN_NOTE_NO_REFERENCE_TO_FRONT.short),
                Choice(PageTypes.BACK_WITH_EXTENSION_OF_FRONT, name=PageTypes.BACK_WITH_EXTENSION_OF_FRONT.short),
            ],
            default=self.page_parameter,
        ).execute()
        while self.check_done == Check.NOT_DONE:
            self.check_done = inquirer.select(
                message="Are you done with the check AND SAVED THE OCR?",
                choices=[
                    Choice(Check.NOT_DONE, name="No, I am not done."),
                    Choice(Check.DONE, name="Yes, I am done AND SAVED THE OCR!")],
                default=Check.NOT_DONE,
            ).execute()
        if skip_scan == 1:
            self._rename_filepath_of_checked_files()

    def get_faksimile(self):
        xml_entry = etree.Element("graphic")
        xml_entry.attrib["{http://www.w3.org/XML/1998/namespace}id"] = self.path.stem
        xml_entry.attrib["url"] = self.path.stem
        xml_entry.attrib["rend"] = self.page_parameter.rend
        return xml_entry

    def create_final_pdf(self):
        self.check_if_review_was_done_and_set_parameter()
        if not self.page_parameter == PageTypes.BACK_EMPTY:
            return self.path_pdf

    def _rename_filepath_of_checked_files(self):
        self.path = self.path.rename(
            self.path.parent / (
                        self.path.stem + str(self.page_parameter.short) + str(self.good_scan.value) + self.path.suffix))
        self.path_ocr = self.path_ocr.rename(self.path_handler.generate_new_file_path(self.path, FileType.TXT))
        self.path_jpg = self.path_jpg.rename(self.path_handler.generate_new_file_path(self.path, FileType.JPG))
        if self.path_pdf.exists():
            self.path_pdf = self.path_pdf.rename(self.path_handler.generate_new_file_path(self.path, FileType.PDF))
