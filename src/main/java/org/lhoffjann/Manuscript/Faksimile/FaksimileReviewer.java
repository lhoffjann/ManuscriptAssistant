package org.lhoffjann.Manuscript.Faksimile;

import de.codeshelf.consoleui.elements.ConfirmChoice;
import de.codeshelf.consoleui.prompt.*;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import jline.TerminalFactory;
import org.lhoffjann.Manuscript.enums.PageType;
import org.lhoffjann.Manuscript.enums.ScanQuality;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

public class FaksimileReviewer {
    public void reviewFaksimile(Faksimile faksimile) throws IOException {
        if(faksimile.getTIFPath().toFile().exists()) {
            openFilesForReview(faksimile);
            checkPageType(faksimile);
            checkScanQuality(faksimile);
        }
    }

    private void checkScanQuality(Faksimile faksimile){
        try {
            ConsolePrompt prompt = new ConsolePrompt();                     // #2
            PromptBuilder promptBuilder = prompt.getPromptBuilder();        // #3
            promptBuilder.createConfirmPromp().name("scanQuality")
                    .message("Is the scan good?")
                    .defaultValue(ConfirmChoice.ConfirmationValue.YES)
                    .addPrompt();
            HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
            ConfirmChoice.ConfirmationValue scanQuality = ((ConfirmResult) result.get("scanQuality")).getConfirmed();
            if (scanQuality == ConfirmChoice.ConfirmationValue.YES){
                if (faksimile.getScanQuality() != ScanQuality.GOOD){
                    faksimile.changeScanQuality(ScanQuality.GOOD);
                }
            }else {
                if (faksimile.getScanQuality() != ScanQuality.BAD){
                    faksimile.changeScanQuality(ScanQuality.BAD);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                TerminalFactory.get().restore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPageType(Faksimile faksimile){
        try {
            ConsolePrompt prompt = new ConsolePrompt();                     // #2
            PromptBuilder promptBuilder = prompt.getPromptBuilder();        // #3
            ConfirmChoice.ConfirmationValue completed;
            promptBuilder.createConfirmPromp().name("pagetype")
                    .message("Is " + faksimile.getPageParameter().name() + " the right page type?")
                    .defaultValue(ConfirmChoice.ConfirmationValue.YES)
                    .addPrompt();
            HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
            completed = ((ConfirmResult) result.get("pagetype")).getConfirmed();
            if (completed == ConfirmChoice.ConfirmationValue.NO) selectPageType(faksimile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                TerminalFactory.get().restore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void selectPageType(Faksimile faksimile) throws IOException {
        ConsolePrompt prompt1 = new ConsolePrompt();                     // #2
        PromptBuilder promptBuilder1 = prompt1.getPromptBuilder();        // #3
        ListPromptBuilder listPromptBuilder = promptBuilder1.createListPrompt();
        listPromptBuilder.name("pagetype").message("Which is the right one?");
        for (PageType pageType: PageType.values()){
            listPromptBuilder.newItem().text(pageType.name()).add();
        }
        listPromptBuilder.addPrompt();
        HashMap<String, ? extends PromtResultItemIF> result1 = prompt1.prompt(promptBuilder1.build());
        ListResult result2 = (ListResult) result1.get("pagetype");
        for (PageType pageType: PageType.values()){
            if (pageType.name().equals(result2.getSelectedId())){
                System.out.println(result2.getSelectedId());
                faksimile.changePageParameter(pageType);
            }
        }
    }

    private void openFilesForReview(Faksimile faksimile) throws IOException {
        if(faksimile.getOCRPath()!=null){
            Desktop.getDesktop().open(faksimile.getOCRPath().toFile());
        }
        if (faksimile.getJPGPath()!= null){
            Desktop.getDesktop().open(faksimile.getJPGPath().toFile());
        }

    }
    private void checkOCR(Faksimile faksimile) throws IOException {

        ConfirmChoice.ConfirmationValue completed = ConfirmChoice.ConfirmationValue.NO;
        while (completed == ConfirmChoice.ConfirmationValue.NO){
            try {
                ConsolePrompt prompt = new ConsolePrompt();                     // #2
                PromptBuilder promptBuilder = prompt.getPromptBuilder();        // #3

                promptBuilder.createConfirmPromp().name("ocr")
                        .message("Did you save the OCR?")
                        .defaultValue(ConfirmChoice.ConfirmationValue.NO)
                        .addPrompt();
                HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
                completed = ((ConfirmResult) result.get("ocr")).getConfirmed();
            } finally {
                try {
                    TerminalFactory.get().restore();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
