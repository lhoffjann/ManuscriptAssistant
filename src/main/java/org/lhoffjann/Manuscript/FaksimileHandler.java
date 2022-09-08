package org.lhoffjann.Manuscript;

public class FaksimileHandler {
    private final Faksimile firstFaksimile;
    private final Faksimile secondFaksimile;

    public FaksimileHandler(Faksimile firstFaksimile, Faksimile secondFaksimile) {
        this.firstFaksimile = firstFaksimile;
        this.secondFaksimile = secondFaksimile;
    }

    public Faksimile getFirstFaksimile() {
        return firstFaksimile;
    }

    public Faksimile getSecondFaksimile() {
        return secondFaksimile;
    }
}
