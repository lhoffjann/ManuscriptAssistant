package org.lhoffjann.Manuscript;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum IssueDesc {
        ISSUE_NEW("src/main/resources/Issue_new"),
        ISSUE_SCANNED("src/main/resources/Issue_scanned"),
        ISSUE_REVIEWED("src/main/resources/Issue_reviewed"),
        ISSUE_REVIEWED_NE("src/main/resources/Issue_reviewed_NE"),
        ISSUE_POSTPROCESSED("src/main/resources/Issue_postprocessed");


        private String filepath;

        private IssueDesc(String filepath) {
            this.filepath = filepath;
        }

        public Path getFilepath() {
            return Paths.get(filepath);
        }
}
