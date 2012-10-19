package org.eclipse.jgit.awtui;

import org.eclipse.jgit.revplot.PlotLane;

import java.awt.*;

/**
* Created with IntelliJ IDEA.
* User: alberto
* Date: 19/10/12
* Time: 18:47
* To change this template use File | Settings | File Templates.
*/
public class SwingLane extends PlotLane {
    private Color color;

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && color.equals(((SwingLane)o).color);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ color.hashCode();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
