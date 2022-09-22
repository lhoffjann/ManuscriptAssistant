package org.lhoffjann.Manuscript;

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

public class ManuscriptSelector {
    private List<String> getManuscriptList(){
        PathHandler pathHandler = new PathHandler();
        File baseFolder = new File(pathHandler.getBasePath().toString());
        List<String> manuscripts = Arrays.stream(baseFolder.list()).sorted().toList();
        return manuscripts;
    }
    public String selectManuscript(){
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
}
