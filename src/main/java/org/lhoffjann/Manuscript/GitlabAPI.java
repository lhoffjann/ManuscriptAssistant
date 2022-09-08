package org.lhoffjann.Manuscript;

import io.github.cdimascio.dotenv.Dotenv;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.IssuesApi;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitlabAPI {
    private GitLabApi gitLabApi;
    private Project project;
    private String projectID;

    public GitlabAPI() throws GitLabApiException {
        Dotenv dotenv = Dotenv.load();
        gitLabApi = new GitLabApi("https://gitlab.ub.uni-bielefeld.de/", dotenv.get("gitlab"));
        projectID = dotenv.get("gitlabProjectID");
        project = gitLabApi.getProjectApi().getProject("3837");
    }


    public List<Issue> getIssue() throws GitLabApiException {
        List<Issue> issues =  gitLabApi.getIssuesApi().getIssues("3837");
        return issues;
    }
    public Issue findIssues(String manuscriptID) throws GitLabApiException, IOException {
        List<Issue> issues = getIssue();
        for (Issue issue: issues){
            Pattern pattern = Pattern.compile(manuscriptID, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(issue.getTitle());
            if (matcher.find()){
                return issue;
            }
        }
        return createIssue(manuscriptID);
    }
    public Issue createIssue(String manuscriptID) throws IOException, GitLabApiException {

        Charset charset = StandardCharsets.UTF_8;
        String issueDesc = new String(Files.readAllBytes(IssueDesc.ISSUE_NEW.getFilepath()), charset);
        Issue issue =  gitLabApi.getIssuesApi().createIssue(projectID, manuscriptID, issueDesc);
        return issue;
    }
    public Issue updateIssue(String manuscriptID, IssueDesc issueDesc) throws GitLabApiException, IOException {
        Issue issue = findIssues(manuscriptID);
        Charset charset = StandardCharsets.UTF_8;
        String issueDescStr = new String(Files.readAllBytes(issueDesc.getFilepath()), charset);
        Issue updatedIssue = gitLabApi
                .getIssuesApi()
                .updateIssue(projectID,
                        issue.getIid(),
                        issue.getTitle(),
                        issueDescStr,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
        return updatedIssue;
    }



}
