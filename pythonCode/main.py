from manuscript import Manuscript
from pathlib import Path
import os
from navigater import Navigator
from InquirerPy import inquirer
from dotenv import load_dotenv
from gitlabAPIhandler import GitlabAPIHandler
from xmlGenerator import XMLGenerator
#TODO: select Option via Enum, via String
def main():
    
    dotenv_path = Path.cwd() / '.env'
    load_dotenv(dotenv_path)
    path_to_scans = Path(os.getenv("path_scans"))
    path_to_master_images = Path(os.getenv("path_master_images")) 
    gitlabapi = GitlabAPIHandler()
    navigator = Navigator(path_to_scans, path_to_master_images, gitlabapi)
    action = inquirer.select(
        message="What do you want to do?",
        choices=["Scan a manuscript",
                 "Review a manuscript",
                 "create a new scan job",
                 "Generate XML",
                 "Generate PDF"],
        default=None,
    ).execute()
    if action == "create a new scan job":
        navigator.create_new_scan_issue()
        #has to look at everything that was scanned everything master_images and with the issues
        #compare it with the issue json
        #generate a list of scanable issues
    if action == "Scan a manuscript":
        navigator.fill_manuscripts_in_master_images()
        print(navigator.manuscripts_in_master_images)
    if action == "Review a manuscript":
        navigator.fill_scanned_manuscripts()
        manuscript = navigator.select_manuscript()
        review_manuscript = Manuscript(id=manuscript, path=path_to_scans / manuscript)
        review_manuscript.fill_manuscript()
        review_manuscript.prepare_ms_for_review()
        review_manuscript.review_manuscript()
        xmlgenerator = XMLGenerator(review_manuscript)
        xmlgenerator.add_paragraphs_to_ocr_content()
        xmlgenerator.create_xml_entry()
        xmlgenerator.write_xml_structure_to_file()
        review_manuscript.finalize_ms()

    if action == "Generate XML":
        navigator.fill_scanned_manuscripts()
        manuscript = navigator.select_manuscript()
        review_manuscript = Manuscript(id=manuscript, path=path_to_scans / manuscript)
        review_manuscript.fill_manuscript()
        review_manuscript.prepare_ms_for_review()
        xmlgenerator = XMLGenerator(review_manuscript)
        xmlgenerator.add_paragraphs_to_ocr_content()
        xmlgenerator.create_xml_entry()
        xmlgenerator.write_xml_structure_to_file()

    if action == "Generate PDF":
        navigator.fill_scanned_manuscripts()
        manuscript = navigator.select_manuscript()
        review_manuscript = Manuscript(id=manuscript, path=path_to_scans / manuscript)
        review_manuscript.fill_manuscript()
        review_manuscript.prepare_ms_for_review()
        review_manuscript.finalize_ms()




if __name__ == '__main__':
    main()

