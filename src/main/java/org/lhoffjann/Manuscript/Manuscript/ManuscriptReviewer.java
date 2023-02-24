package org.lhoffjann.Manuscript.Manuscript;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.ListResult;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import org.lhoffjann.Manuscript.Faksimile.Faksimile;
import org.lhoffjann.Manuscript.Faksimile.FaksimileReviewer;
import org.lhoffjann.Manuscript.GitlabAPI;
import org.lhoffjann.Manuscript.FIleCreator.PDFCreator;
import org.lhoffjann.Manuscript.Page.Page;
import org.lhoffjann.Manuscript.FIleCreator.XMLCreator;
import org.lhoffjann.Manuscript.enums.IssueDesc;
import org.lhoffjann.Manuscript.enums.ScanQuality;

import java.io.IOException;
import java.util.HashMap;

public class ManuscriptReviewer {

    private void reviewFaksimile(Faksimile faksimile) throws IOException {
        FaksimileReviewer faksimileReviewer = new FaksimileReviewer();
        faksimileReviewer.reviewFaksimile(faksimile);


    }
    public void swapFaksimileOrPages(Manuscript manuscript) throws Exception {
        ConsolePrompt prompt = new ConsolePrompt();                     // #2
        PromptBuilder promptBuilder = prompt.getPromptBuilder();        // #3

        promptBuilder.createListPrompt()                                // #4
                .name("action")
                .message("What do you want to do?")
                .newItem("faksimile").text("swap the front and back of a faksimile").add()  // without name (name defaults to text)
                .newItem("pages").text("swap two Pages").add()
                .addPrompt();

        HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build()); // #5
        ListResult result1 = (ListResult) result.get("action");
        System.out.println(result1.getSelectedId());
        if(result1.getSelectedId().equals( "faksimile")){
            swapFaksimile(manuscript);

        } else if (result1.getSelectedId().equals("pages")) {
            swapPages(manuscript);

        }

    }

    private void swapFaksimile(Manuscript manuscript) throws Exception {
        ConsolePrompt prompt1 = new ConsolePrompt();                     // #2
        PromptBuilder promptBuilder1 = prompt1.getPromptBuilder();        // #3
        ListPromptBuilder listPromptBuilder1 = promptBuilder1.createListPrompt();
        listPromptBuilder1.name("manuscript").message("Which page do you want to swap the front and back?");
        for (Page manu : manuscript.getPageList()) {
            listPromptBuilder1.newItem(String.valueOf(manu.getId())).text("Page: " + manu.getId()).add();
        }
        listPromptBuilder1.addPrompt();
        HashMap<String, ? extends PromtResultItemIF> result2 = prompt1.prompt(promptBuilder1.build());
        ListResult result1 = (ListResult) result2.get("manuscript");

        int Page = Integer.parseInt(result1.getSelectedId());
        ManuscriptAssistant manuscriptAssistant = new ManuscriptAssistant();
        manuscriptAssistant.swapFrontAndBack(manuscript, Page);


    }

    public void swapPages(Manuscript manuscript) throws Exception {

        ConsolePrompt prompt1 = new ConsolePrompt();                     // #2
        PromptBuilder promptBuilder1 = prompt1.getPromptBuilder();        // #3
        ListPromptBuilder listPromptBuilder1 = promptBuilder1.createListPrompt();
        listPromptBuilder1.name("manuscript").message("Which page do you want to swap?");
        for (Page manu : manuscript.getPageList()) {
                listPromptBuilder1.newItem(String.valueOf(manu.getId())).text("Page: " + manu.getId()).add();
        }
        listPromptBuilder1.addPrompt();
        HashMap<String, ? extends PromtResultItemIF> result2 = prompt1.prompt(promptBuilder1.build());
        ListResult result1 = (ListResult) result2.get("manuscript");
        int firstPage = Integer.parseInt(result1.getSelectedId());

        ConsolePrompt prompt2 = new ConsolePrompt();                     // #2
        PromptBuilder promptBuilder2 = prompt2.getPromptBuilder();        // #3
        ListPromptBuilder listPromptBuilder2 = promptBuilder2.createListPrompt();
        listPromptBuilder2.name("manuscript").message("With which page do you want to swap?");
        for (Page manu : manuscript.getPageList()) {
            if (firstPage != manu.getId()) {
                listPromptBuilder2.newItem(String.valueOf(manu.getId())).text("Page: " + manu.getId()).add();
            }
        }
        listPromptBuilder2.addPrompt();
        HashMap<String, ? extends PromtResultItemIF> result3 = prompt2.prompt(promptBuilder2.build());
        ListResult result4 = (ListResult) result3.get("manuscript");

        int secondPage = Integer.parseInt(result4.getSelectedId());
        ManuscriptAssistant manuscriptAssistant = new ManuscriptAssistant();
        System.out.println(firstPage);
        System.out.println(secondPage);
        manuscriptAssistant.swapPage(manuscript, firstPage - 1, secondPage - 1);


    }




    public void startReviewing(Manuscript manuscript) throws Exception {
        for(Page page : manuscript.getPageList()){
            reviewFaksimile(page.getFront());
            reviewFaksimile(page.getBack());
        }
        boolean manuscriptOK = true;
        for(Page page : manuscript.getPageList()){
            if (page.getFront().getScanQuality() == ScanQuality.BAD || page.getBack().getScanQuality() == ScanQuality.BAD) {
                manuscriptOK = false;
                break;
            }
        }
        GitlabAPI gitlabAPI = new GitlabAPI();
        if(manuscriptOK){
            gitlabAPI.updateIssue(manuscript.getManuscriptID(), IssueDesc.ISSUE_REVIEWED);
            XMLCreator xmlCreator = new XMLCreator();
            xmlCreator.createXML(manuscript);
            PDFCreator pdfCreator = new PDFCreator();
            pdfCreator.createPDF(manuscript);
            ManuscriptAssistant manuscriptAssistant = new ManuscriptAssistant();
            manuscriptAssistant.copyTIFFsToMasterImage(manuscript);
        } else {
            gitlabAPI.updateIssue(manuscript.getManuscriptID(), IssueDesc.ISSUE_REVIEWED_NE);
        }

    }


}
