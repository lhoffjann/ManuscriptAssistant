from InquirerPy import inquirer
import pandas as pd

from InquirerPy.separator import Separator
from pathlib import Path
import itertools
from src.gitlabAPIhandler import GitlabAPIHandler
from src.backend_db import JSONHandler
class Navigator:
    def __init__(self, path_scans: Path, path_master: Path, gitlabapihandler: GitlabAPIHandler):
        self.path_scans = path_scans
        self.path_masterimages = path_master
        self.manuscripts_in_master_images = []
        self.scanned_manuscripts = []
        self.gitlabapihandler = gitlabapihandler
        self.jsonhandler = JSONHandler('issue.json')
        self.to_be_scanned_manuscripts = []
        self.not_scanned_manuscripts = []
        self.all_manuscripts = []
    def fill_scanned_manuscripts(self) -> list[str]:
        for path in self.path_scans.iterdir():
            if path.is_dir():
                self.scanned_manuscripts.append(path.stem)
        self.scanned_manuscripts.sort()
    
    def fill_manuscripts_in_master_images(self) -> list[str]:
        for path in self.path_masterimages.iterdir():
            if path.is_dir():
                self.manuscripts_in_master_images.append(path.stem)
        self.manuscripts_in_master_images.sort()
    def select_manuscript(self):
        manuscript = inquirer.select(
            message="Which do you want to review?:",
            choices=self.scanned_manuscripts,
        ).execute()
        return manuscript
    
    def fill_all_manuscripts(self) -> None:
        df = pd.read_json("issue.json")
        self.all_manuscripts = list(df['MS-ID'])

    def fill_not_scanned_manuscripts(self):
        self.fill_all_manuscripts()
        self.fill_manuscripts_in_master_images()
        self.fill_scanned_manuscripts()
        all_scanned_manuscripts = self.scanned_manuscripts + self.manuscripts_in_master_images

        for manuscript in self.all_manuscripts:
            if (manuscript not in all_scanned_manuscripts):
                self.not_scanned_manuscripts.append(manuscript)
    def create_new_scan_issue(self):
        self.fill_not_scanned_manuscripts()
        existing_issues = self.gitlabapihandler.return_all_manuscript_Issues()
        not_existing_issues =[]

        for manuscript in self.not_scanned_manuscripts:
            if manuscript not in existing_issues:
                not_existing_issues.append(manuscript)

        issues_to_be_created = inquirer.fuzzy(
            message="Select manuscript(s)(use the right arrow key for multiselect):",
            choices=not_existing_issues,
            multiselect=True,
            keybindings = {"toggle":[{"key":'right' }]},
            max_height="70%",
        ).execute()
        
        for issue in issues_to_be_created:
            konvolut = self.jsonhandler.get_konvolut_of_issue(issue)
            self.gitlabapihandler.create_issue(issue, konvolut)
        
    def get_stats(self):
        self.fill_not_scanned_manuscripts(self)
        all_scanned_manuscripts = self.scanned_manuscripts + self.manuscripts_in_master_images
        all_scanned_manuscripts = list(set(all_scanned_manuscripts).intersection(self.all_manuscripts))
        print(len(all_scanned_manuscripts)/len(self.all_manuscripts))
        print(len(self.scanned_manuscripts)/len(self.all_manuscripts))
        print(len(self.manuscripts_in_master_images)/len(self.all_manuscripts))
        print(len(self.not_scanned_manuscripts)/len(self.all_manuscripts))
