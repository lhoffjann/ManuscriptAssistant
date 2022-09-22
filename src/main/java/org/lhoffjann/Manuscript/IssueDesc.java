package org.lhoffjann.Manuscript;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum IssueDesc {
        ISSUE_NEW("/Issue_new.txt"),
        ISSUE_SCANNED("/Issue_scanned.txt"),
        ISSUE_REVIEWED("/Issue_reviewed.txt"),
        ISSUE_REVIEWED_NE("/Issue_reviewed_NE.txt"),
        ISSUE_POSTPROCESSED("/Issue_postprocessed.txt");


        private String filepath;

        private IssueDesc(String filepath) {
            this.filepath = filepath;
        }

        public String  getFilepath() {
            return filepath;
        }
}
