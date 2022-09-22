package org.lhoffjann.Manuscript;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.ListResult;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
        projectID = "3837";
        project = gitLabApi.getProjectApi().getProject("3837");
    }


    public List<Issue> getIssue() throws GitLabApiException {
        List<Issue> issues = gitLabApi.getIssuesApi().getIssues("3837");
        return issues;
    }

    public Issue findIssues(String manuscriptID) throws GitLabApiException, IOException {
        List<Issue> issues = getIssue();
        for (Issue issue : issues) {
            Pattern pattern = Pattern.compile(manuscriptID, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(issue.getTitle());
            if (matcher.find()) {
                return issue;
            }
        }
        return createIssue(manuscriptID);
    }

    public Issue createIssue(String manuscriptID) throws IOException, GitLabApiException {

        Charset charset = StandardCharsets.UTF_8;
        String issueDesc = new String(getClass().getResourceAsStream(IssueDesc.ISSUE_NEW.getFilepath()).readAllBytes(), charset);
        Issue issue = gitLabApi.getIssuesApi().createIssue(projectID, manuscriptID, issueDesc);
        return issue;
    }

    public Issue updateIssue(String manuscriptID, IssueDesc issueDesc) throws GitLabApiException, IOException {
        Issue issue = findIssues(manuscriptID);
        Charset charset = StandardCharsets.UTF_8;
        String issueDescStr = new String(getClass().getResourceAsStream(issueDesc.getFilepath()).readAllBytes(), charset);
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

    public void selectUpdate(String manuscriptID) throws IOException, GitLabApiException {
        ConsolePrompt prompt = new ConsolePrompt();                     // #2
        PromptBuilder promptBuilder = prompt.getPromptBuilder();        // #3

        promptBuilder.createListPrompt()                                // #4
                .name("action")
                .message("What do you want to do?")
                .newItem("Issue_new").add()
                .newItem("Issue_scanned").add()
                .newItem("Issue_reviewed").add()
                .newItem("Issue_reviewed_NE").add()
                .newItem("Issue_postprocessed").add()
                .newItem("GitlabIssue").add()
                .addPrompt();

        HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build()); // #5
        ListResult result1 = (ListResult) result.get("action");
        System.out.println(result1.getSelectedId());
        if (result1.getSelectedId() == "Issue_new") {
            updateIssue(manuscriptID, IssueDesc.ISSUE_NEW);
        } else if (result1.getSelectedId() == "Issue_scanned") {
            updateIssue(manuscriptID, IssueDesc.ISSUE_SCANNED);
        } else if (result1.getSelectedId() == "Issue_reviewed") {
            updateIssue(manuscriptID, IssueDesc.ISSUE_REVIEWED);
        } else if (result1.getSelectedId() == "Issue_reviewed_NE") {
            updateIssue(manuscriptID, IssueDesc.ISSUE_REVIEWED_NE);
        } else if (result1.getSelectedId() == "Issue_postprocessed") {
            updateIssue(manuscriptID, IssueDesc.ISSUE_POSTPROCESSED);
        }
    }
}

