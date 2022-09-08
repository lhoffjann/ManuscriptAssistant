package org.lhoffjann.Manuscript;

public class Page {
    private int id;
    private Faksimile front;
    private Faksimile back;

    public Page(int id, Faksimile front, Faksimile back) {
        this.id = id;
        this.front = front;
        this.back = back;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFront(Faksimile front) {
        this.front = front;
    }

    public void setBack(Faksimile back) {
        this.back = back;
    }

    public Faksimile getFront() {
        return front;
    }

    public Faksimile getBack() {
        return back;
    }
}
