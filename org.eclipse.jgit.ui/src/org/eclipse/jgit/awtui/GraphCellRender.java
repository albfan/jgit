package org.eclipse.jgit.awtui;

import org.eclipse.jgit.revplot.PlotCommit;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Renderizador de commits
 * User: alberto
 * Date: 19/10/12
 * Time: 19:45
 */
public class GraphCellRender extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    private final AWTPlotRenderer renderer;

    PlotCommit<SwingLane> commit;

    public GraphCellRender(boolean decorate) {
        renderer = new AWTPlotRenderer(this);
        renderer.setDecorate(decorate);
    }

    @SuppressWarnings("unchecked")
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected
            , boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        commit = (PlotCommit<SwingLane>) value;
        return this;
    }

    @Override
    protected void paintComponent(Graphics inputGraphics) {
        if (inputGraphics == null)
            return;
        renderer.paint(inputGraphics, commit);
    }
}
