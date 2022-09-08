package org.lhoffjann.Manuscript;

import de.codeshelf.consoleui.elements.ConfirmChoice;
import de.codeshelf.consoleui.prompt.ConfirmResult;
import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.ListResult;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import jline.TerminalFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ManuscriptReviewer {
    public List<String> getManuscriptList(){
        PathHandler pathHandler = new PathHandler();
        File baseFolder = new File(pathHandler.getBasePath().toString());
        List<String> manuscripts = Arrays.stream(baseFolder.list()).sorted().toList();
        return manuscripts;
    }
    private String selectManuscript(){
        String resultString = "";
        try {
            ConsolePrompt prompt = new ConsolePrompt();                     // #2
            PromptBuilder promptBuilder = prompt.getPromptBuilder();        // #3
            ListPromptBuilder listPromptBuilder = promptBuilder.createListPrompt();
            listPromptBuilder.name("manuscript").message("What manuscript do you want to review?");
            for (String manuscript: getManuscriptList()){
                listPromptBuilder.newItem().text(manuscript).add();
            }
            listPromptBuilder.addPrompt();
            HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
            ListResult result1 = (ListResult) result.get("manuscript");
            System.out.println(result1.getSelectedId());
            resultString = result1.getSelectedId();
            return resultString;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                TerminalFactory.get().restore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }
    private void reviewFaksimile(Faksimile faksimile) throws IOException {
        FaksimileReviewer faksimileReviewer = new FaksimileReviewer();
        faksimileReviewer.reviewFaksimile(faksimile);

    }
    private void swapFaksimile(Manuscript manuscript, int pageNumber) throws Exception {
        ConsolePrompt prompt = new ConsolePrompt();                     // #2
        PromptBuilder promptBuilder = prompt.getPromptBuilder();        // #3
        ConfirmChoice.ConfirmationValue completed;
        promptBuilder.createConfirmPromp().name("pagetype")
                .message("Do you want to swap front and back?")
                .defaultValue(ConfirmChoice.ConfirmationValue.NO)
                .addPrompt();
        HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
        completed = ((ConfirmResult) result.get("pagetype")).getConfirmed();
        if (completed == ConfirmChoice.ConfirmationValue.YES){

            ManuscriptAssistant manuscriptAssistant = new ManuscriptAssistant();
            manuscriptAssistant.swapFrontAndBack(manuscript, pageNumber);
        }


    }
    public void swapPages(Manuscript manuscript,int pageNumber) throws Exception {
        ConsolePrompt prompt = new ConsolePrompt();                     // #2
        PromptBuilder promptBuilder = prompt.getPromptBuilder();        // #3
        ConfirmChoice.ConfirmationValue completed;
        promptBuilder.createConfirmPromp().name("pagetype")
                .message("Do you want to swap this Page with another one?")
                .defaultValue(ConfirmChoice.ConfirmationValue.NO)
                .addPrompt();
        HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
        completed = ((ConfirmResult) result.get("pagetype")).getConfirmed();
        if (completed == ConfirmChoice.ConfirmationValue.YES){
            ConsolePrompt prompt1 = new ConsolePrompt();                     // #2
            PromptBuilder promptBuilder1 = prompt1.getPromptBuilder();        // #3
            ListPromptBuilder listPromptBuilder1 = promptBuilder1.createListPrompt();
            listPromptBuilder1.name("manuscript").message("With which page do you want to swap?");
            for (Page manu : manuscript.getPageList()) {
                if (pageNumber != manu.getId()) {
                    System.out.println("hello");
                    listPromptBuilder1.newItem(String.valueOf(manu.getId())).text("Page: " + manu.getId()).add();
                }
            }
                listPromptBuilder1.addPrompt();
                HashMap<String, ? extends PromtResultItemIF> result2 = prompt1.prompt(promptBuilder1.build());
                ListResult result1 = (ListResult) result2.get("manuscript");

                int secondPage = Integer.parseInt(result1.getSelectedId());
                ManuscriptAssistant manuscriptAssistant = new ManuscriptAssistant();
                manuscriptAssistant.swapPage(manuscript, pageNumber, secondPage);



        }

    }


    public void startReviewing() throws Exception {
        String manuscriptID = selectManuscript();
        ManuscriptFactory manuscriptFactory = new ManuscriptFactory();
        Manuscript manuscript = manuscriptFactory.createManuscript(manuscriptID);
        for(Page page : manuscript.getPageList()){
            reviewFaksimile(page.getFront());
            reviewFaksimile(page.getBack());
            swapFaksimile(manuscript, page.getId());
            swapPages(manuscript, page.getId());
        }
        boolean manuscriptOK = true;
        for(Page page : manuscript.getPageList()){
            if (page.getFront().getScanQuality() == ScanQuality.BAD || page.getBack().getScanQuality() == ScanQuality.BAD){
                manuscriptOK = false;
            }
        }
        GitlabAPI gitlabAPI= new GitlabAPI();
        if(manuscriptOK){
            gitlabAPI.updateIssue(manuscript.getManuscriptID(), IssueDesc.ISSUE_REVIEWED);
            XMLCreator xmlCreator = new XMLCreator();
            xmlCreator.createXML(manuscript);
            ManuscriptAssistant manuscriptAssistant = new ManuscriptAssistant();
            manuscriptAssistant.copyTIFFsToMasterImage(manuscript);
        }else {
            gitlabAPI.updateIssue(manuscript.getManuscriptID(), IssueDesc.ISSUE_REVIEWED_NE);
        }

    }


}
