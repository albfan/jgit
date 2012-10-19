package org.eclipse.jgit.awtui;

import org.eclipse.jgit.lib.PersonIdent;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
* Renderizador de fechas
* User: alberto
* Date: 19/10/12
* Time: 19:45
*/
class DateCellRender extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    private static final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus
            , int row, int column) {
        PersonIdent pi = (PersonIdent) value;

        String valueStr;
        if (pi != null)
            valueStr = fmt.format(pi.getWhen());
        else
            valueStr = "";
        return super.getTableCellRendererComponent(table, valueStr, isSelected, hasFocus, row, column);
    }
}
