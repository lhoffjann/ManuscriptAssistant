import gitlab
from pathlib import Path
from dotenv import load_dotenv
import os
import re

dotenv_path = Path.cwd() / '.env'
load_dotenv(dotenv_path)
gitlab_token = os.getenv("gitlab")


class GitlabAPIHandler:
    #Todo: Create own dotenv file for these information
    def __init__(self):
        self.issue = None
        self.issue_id = None
        self.gl = gitlab.Gitlab('https://gitlab.ub.uni-bielefeld.de/', private_token=gitlab_token)
        self.gl.auth()
        self.id_proofreader = 144
        self.id_scan_operator = 1432
        self.ms_project = self.gl.projects.get(3837)

    def get_editable_issue(self):
        ms_issues = self.ms_project.issues.list()
        issue = ms_issues[self.issue_id - 1]
        editable_project = self.gl.projects.get(issue.project_id)
        editable_issue = editable_project.issues.get(issue.iid)
        return editable_issue

    def return_all_manuscript_Issues(self):
        issues = self.ms_project.issues.list(all=True)
        issue_titles = []
        for issue in issues:
            p = re.compile("MS_\d{4}")
            result = p.search(issue.title.split()[0])
            issue_titles.append(result.group(0))
        return issue_titles

    def find_issue(self):
        issues = self.ms_project.issues.list(all=True)
        for issue in issues:
            if f'MS_{self.ms_id}' == issue.title.split()[0]: #TODO: This can be probably an Enum too
                return issue.iid
        return None

    def create_issue(self, ms_id, konvolut):
        ms_id = ms_id
        konvolut = konvolut
        issue_note = ''
        with open('../gitlabIssueNote.txt', 'r') as file:
            issue_note = f"{file.read()}".format(**locals())
        issue_dict = {'title': f'{ms_id} ({konvolut})', 'description': issue_note}
        self.issue = self.ms_project.issues.create(issue_dict)
        self.issue.assignee_id = self.id_scan_operator
        self.issue_id = self.issue.iid
        self.issue.save()

    def update_issue(self):
        raise NotImplementedError
