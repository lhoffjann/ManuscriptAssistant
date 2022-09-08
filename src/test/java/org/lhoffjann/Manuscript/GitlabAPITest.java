/**
package org.lhoffjann.Manuscript;


import org.gitlab4j.api.GitLabApiException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class GitlabAPITest {

    @Test
    public void getIssues() throws GitLabApiException {
        GitlabAPI gitlabAPI = new GitlabAPI();
        System.out.println(gitlabAPI.getIssue());

    }
    @Test
    public void findIssues() throws GitLabApiException, IOException {
        GitlabAPI gitlabAPI = new GitlabAPI();
        System.out.println(gitlabAPI.findIssues("2865"));

    }

    @Test
    public void updateIssue() throws GitLabApiException, IOException {
        GitlabAPI gitlabAPI = new GitlabAPI();
        System.out.println(gitlabAPI.updateIssue("MS_2877_test", IssueDesc.ISSUE_SCANNED));
    }
    @Test
    public void updateReviewIssue() throws GitLabApiException, IOException {
        GitlabAPI gitlabAPI = new GitlabAPI();
        System.out.println(gitlabAPI.updateIssue("MS_2877_test", IssueDesc.ISSUE_POSTPROCESSED));
    }
}
 **/
