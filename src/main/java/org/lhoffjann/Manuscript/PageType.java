package org.lhoffjann.Manuscript;

public enum PageType {
    FRONT ("V", "vorderseite", "content", 'q'),
    BACK_EMPTY("RL", "rueckseite", "empty", 'w'),
    BACK_WITH_REFERENCE_TO_FRONT("RV", "rueckseite", "content", 'e'),
    BACK_WITH_HANDWRITTEN_NOTE_NO_REFERENCE_TO_FRONT("RR", "rueckseite", "content_irrelevant", 'r'),
    BACK_WITH_PRINT_NO_REFERENCE_TO_FRONT("RD", "rueckseite", "content_irrelevant", 't'),
    BACK_WITH_EXTENSION_OF_FRONT("RT", "rueckseite", "content", 'a'),

    FRONT_COLOUR_CARD("F", "farbkarte", "content", 's');
    public final String token;
    public final String side;
    public final String rend;
    public final char key;

    PageType(String token, String side, String rend, char key) {
        this.token = token;
        this.side = side;
        this.rend = rend;

        this.key = key;
    }

}
