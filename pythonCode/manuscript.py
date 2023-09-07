# Imports
from lxml import etree
import os
from pathlib import Path
from typing import List
from PyPDF2 import PdfFileMerger
from PathHandler import FileType, ManuscriptPathHandler
from gitlabAPIhandler import GitlabAPIHandler
from page import Page
import itertools
from dotenv import load_dotenv


# define Python user-defined exceptions
class Error(Exception):
    """Base class for other exceptions"""
    pass


class ManuscriptNotFilledError(Error):
    """Raised when the input value is too small"""
    pass


class Manuscript:
    def __init__(self, id: str, path: Path):
        self.id: str = id
        self.konvolut: str
        self.path: Path = path
        self.glapi: GitlabAPIHandler
        self.path_handler = ManuscriptPathHandler(self.path)
        self.final_ocr = self.path_handler.generate_new_file_path(self.path, FileType.TXT)
        self.xml_file = self.path_handler.generate_new_file_path(self.path, FileType.XML)
        self.pages: List[Page] = []
        self.page_count: int = 0

    # NAPS2 counts up from 0001 till finish. if you delete files and redo the prozedure it will fill the gaps before adding files to the end
    def scan_full_manuscript(self):
        os.system(
            """C:\\"Program Files (x86)"\\NAPS2\\NAPS2.Console.exe -o O:\\manuskripte_scans_fuer_digitalisierung\\scans\\MS_""" +
            self.id + "\\MS_" + self.id + "_$(nnnn)_.tif --tiffcomp none --split""")

    def rescan_single_pages(self):
        raise NotImplementedError

    def fill_manuscript(self) -> list[str]:
        files_grabbed = []
        files_grabbed.extend(self.path.glob('*.tif'))
        files_grabbed.sort()
        page_number = 1
        print('AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa')
        print(files_grabbed)
        for i in range(0, len(files_grabbed), 2):
            images = files_grabbed[i:i + 2]
            self.pages.append(Page(page_number, images[0], images[1], self.path_handler))
            page_number += 1
        self.count_pages()

    def count_pages(self):
        if not self.pages:
            raise ManuscriptNotFilledError
        self.page_count = len(self.pages)

    def prepare_ms_for_review(self):
        for page in self.pages:
            print('processing page :' + str(page.id))
            page.create_jpg()
            page.create_pdf()
            page.create_ocr()

    def create_ocr(self) -> None:
        for page in self.pages:
            page.create_ocr()

    def create_pdf(self):
        for page in self.pages:
            page.create_pdf()

    def create_jpg(self):
        for page in self.pages:
            page.create_jpg()

    def review_manuscript(self):
        for page in self.pages:
            page.review_page()

    def get_list_of_faksimile(self):
        faksimile = ["<facsimile>"]

        for page in self.pages:
            entries_for_front_and_back = page.get_faksimile()
            for i in entries_for_front_and_back:
                test = str(etree.tostring(i), "utf-8")
                faksimile.append(test)
        faksimile.append("</facsimile>")
        text = '&#xA;'.join(faksimile)
        text = etree.fromstring(text)
        return text

    def get_ocr(self):
        lines = []
        for page in self.pages:
            lines += page.get_ocr()
        return list(itertools.chain(*lines))

    def move_to_master_images(self):
        raise NotImplementedError

    def create_final_pdf(self):
        list_of_included_pdfs = []
        for page in self.pages:
            list_of_included_pdfs += page.create_final_pdf()
        list_of_included_pdfs = list(filter(None, list_of_included_pdfs))
        merger = PdfFileMerger()
        try:
            for pdf in list_of_included_pdfs:
                merger.append(str(pdf))
            dotenv_path = Path.cwd() / '.env'
            load_dotenv(dotenv_path)
            if os.getenv("path_pdf_folder") == "":
                self.path.mkdir(parents=True, exist_ok=True)
                merger.write(str((self.path / self.path.stem).with_suffix('.pdf')))
            else:
                Path(os.getenv("path_pdf_folder")).mkdir(parents=True, exist_ok=True)
                merger.write(str((Path(os.getenv("path_pdf_folder")) / self.path.stem).with_suffix('.pdf')))
        finally:
            merger.close()

    def finalize_ms(self):
        self.create_final_pdf()
