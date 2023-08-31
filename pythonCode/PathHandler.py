from enum import Enum, auto
from pathlib import Path
from dotenv import load_dotenv
import os




class FileType(Enum):
    JPG = auto()
    PDF = auto()
    TXT = auto()
    TIF = auto()
    XML = auto()

    @classmethod
    def get_file_type(cls, input: str):
        if input == 'jpg':
            return cls.JPG
        elif input == 'pdf':
            return cls.PDF
        elif input == 'txt':
            return cls.TXT
        elif input == 'tif':
            return cls.TIF
        elif input == 'xml':
            return cls.XML
        else:
            raise TypeError(f"Don't know filetype {input}")


class ManuscriptPathHandler:
    def __init__(self, path):
        self.base_path: Path = path

    @property
    def pdf_path(self):
        return self.base_path / "pdf"

    @property
    def ocr_path(self):
        return self.base_path / "ocr"

    @property
    def jpg_path(self):
        return self.base_path / "jpg"

    def generate_new_file_path(self, path: Path, suffix: FileType) -> Path:
        if suffix == FileType.JPG:
            self.jpg_path.mkdir(parents=True,exist_ok=True)
            return (self.jpg_path / path.stem).with_suffix('.jpg')
        if suffix == FileType.PDF:
            self.pdf_path.mkdir(parents=True, exist_ok=True)
            return (self.pdf_path / path.stem).with_suffix('.pdf')
        if suffix == FileType.TXT:
            self.ocr_path.mkdir(parents=True, exist_ok=True)
            return (self.ocr_path / path.stem).with_suffix('.txt')
        if suffix == FileType.TIF:
            self.base_path.mkdir(parents=True, exist_ok=True)
            return (self.base_path / path.stem).with_suffix('.tif')
        if suffix == FileType.XML:
            self.base_path.mkdir(parents=True, exist_ok=True)
            dotenv_path = Path.cwd() / '.env'
            load_dotenv(dotenv_path)
            if os.getenv("path_xml_folder") == "":
                self.base_path.mkdir(parents=True, exist_ok=True)
                return (self.base_path / path.stem).with_suffix('.xml')
            else:
                Path(os.getenv("path_xml_folder")).mkdir(parents=True, exist_ok=True)
                return (Path(os.getenv("path_xml_folder")) / path.stem).with_suffix('.xml')

    def check_for_existing_files(self):
        raise NotImplementedError