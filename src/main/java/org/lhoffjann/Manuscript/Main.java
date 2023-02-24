package org.lhoffjann.Manuscript;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.ListResult;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import jline.TerminalFactory;
import org.fusesource.jansi.AnsiConsole;
import org.lhoffjann.Manuscript.FIleCreator.PDFCreator;
import org.lhoffjann.Manuscript.FIleCreator.XMLCreator;
import org.lhoffjann.Manuscript.Manuscript.Manuscript;
import org.lhoffjann.Manuscript.Manuscript.ManuscriptFactory;
import org.lhoffjann.Manuscript.Manuscript.ManuscriptReviewer;
import org.lhoffjann.Manuscript.Manuscript.ManuscriptSelector;

import java.util.HashMap;

import static org.fusesource.jansi.Ansi.ansi;

public class Main {
    public static void main(String[] args) throws Exception {
        startTask(taskSelectionPrompt());
    }

    private static String taskSelectionPrompt() {
        AnsiConsole.systemInstall();
        System.out.println(ansi().eraseScreen().render("What do you want to do?:"));
        String task = "";
        try {
            ConsolePrompt prompt = new ConsolePrompt();
            PromptBuilder promptBuilder = prompt.getPromptBuilder();
            promptBuilder.createListPrompt()
                    .name("action")
                    .message("What do you want to do?")
                    .newItem("review").text("Review manuscript").add()
                    .newItem("XML").text("generate XML").add()
                    .newItem("PDF").text("generate PDF").add()
                    .newItem("Switch").text("switch Pages").add()
                    .newItem("GitlabIssue").text("Create/Update Gitlab Issue").add()
                    .addPrompt();
            HashMap<String, ? extends PromtResultItemIF> initialPromptResult = prompt.prompt(promptBuilder.build());
            task = ((ListResult) initialPromptResult.get("action")).getSelectedId();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                TerminalFactory.get().restore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return task;
    }

    public static void startTask(String task) throws Exception {
        ManuscriptReviewer manuscriptReviewer = new ManuscriptReviewer();
        switch (task) {
            case "review":
                manuscriptReviewer.startReviewing(getManuscript());
            case "XML":
                XMLCreator xmlCreator = new XMLCreator();
                xmlCreator.createXML(getManuscript());
            case "PDF":
                PDFCreator pdfCreator = new PDFCreator();
                pdfCreator.createPDF(getManuscript());
            case "Switch":
                manuscriptReviewer.swapFaksimileOrPages(getManuscript());
            case "GitlabIssue":
                GitlabAPI gitlabAPI = new GitlabAPI();
                gitlabAPI.selectUpdate(getManuscript().getManuscriptID());
        }
    }

    private static Manuscript getManuscript() throws Exception {
        ManuscriptSelector manuscriptSelector = new ManuscriptSelector();
        String manuscriptID = manuscriptSelector.selectManuscript();
        ManuscriptFactory manuscriptFactory = new ManuscriptFactory();
        return manuscriptFactory.createManuscript(manuscriptID);
    }
}



