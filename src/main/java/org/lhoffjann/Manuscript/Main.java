package org.lhoffjann.Manuscript;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.ListResult;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import jline.TerminalFactory;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.util.HashMap;

import static org.fusesource.jansi.Ansi.ansi;

public class Main {
    private static Manuscript getManuscript() throws Exception {
        ManuscriptSelector manuscriptSelector = new ManuscriptSelector();
        String manuscriptID = manuscriptSelector.selectManuscript();
        ManuscriptFactory manuscriptFactory = new ManuscriptFactory();
        Manuscript manuscript = manuscriptFactory.createManuscript(manuscriptID);
        return manuscript;

    }
    public static void main(String[] args) {
        AnsiConsole.systemInstall();                                      // #1
        System.out.println(ansi().eraseScreen().render("Simple list example:"));

        try {
            ConsolePrompt prompt = new ConsolePrompt();                     // #2
            PromptBuilder promptBuilder = prompt.getPromptBuilder();        // #3

            promptBuilder.createListPrompt()                                // #4
                    .name("action")
                    .message("What do you want to do?")
                    .newItem("review").text("Review manuscript").add()  // without name (name defaults to text)
                    .newItem("XML").text("generate XML").add()
                    .newItem("PDF").text("generate PDF").add()
                    .newItem("Switch").text("switch Pages").add()
                    .newItem("GitlabIssue").text("Create/Update Gitlab Issue").add()
                    .addPrompt();

            HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build()); // #5
            ListResult result1 = (ListResult) result.get("action");
            System.out.println(result1.getSelectedId());
            if(result1.getSelectedId() == "review"){
                ManuscriptReviewer manuscriptReviewer = new ManuscriptReviewer();
                manuscriptReviewer.startReviewing(getManuscript());

            } else if (result1.getSelectedId() == "XML") {
                XMLCreator xmlCreator = new XMLCreator();
                xmlCreator.createXML(getManuscript());

            }else if (result1.getSelectedId() == "PDF") {
                PDFCreator pdfCreator = new PDFCreator();
                pdfCreator.createPDF(getManuscript());

            }else if (result1.getSelectedId() == "Switch") {
                ManuscriptReviewer manuscriptReviewer = new ManuscriptReviewer();
                manuscriptReviewer.swapFaksimileOrPages(getManuscript());

            }else if (result1.getSelectedId() == "GitlabIssue") {
                GitlabAPI gitlabAPI= new GitlabAPI();
                gitlabAPI.selectUpdate(getManuscript().getManuscriptID());

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                TerminalFactory.get().restore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


