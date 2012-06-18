package edu.uci.ics.asterix.om.base;

public class AMutableRectangle extends ARectangle {

    public AMutableRectangle(APoint p1, APoint p2) {
        super(p1, p2);
    }

    public void setValue(APoint p1, APoint p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

}