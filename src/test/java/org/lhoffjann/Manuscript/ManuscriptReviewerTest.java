package org.lhoffjann.Manuscript;

import org.gitlab4j.api.GitLabApiException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ManuscriptReviewerTest {
    @Test
    public void getAllFolders() {
        ManuscriptReviewer manuscriptReviewer = new ManuscriptReviewer();
        manuscriptReviewer.getManuscriptList();
    }
    @Test
    public void showWindow() {
        ManuscriptReviewer manuscriptReviewer = new ManuscriptReviewer();


    }
}
