package org.eclipse.jgit.awtui;

import org.eclipse.jgit.lib.PersonIdent;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
* Renderizador de nombre y email
* User: alberto
* Date: 19/10/12
* Time: 19:45
*/
class NameCellRender extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    public Component getTableCellRendererComponent( JTable table,  Object value,  boolean isSelected
            ,  boolean hasFocus,  int row,  int column) {
         PersonIdent pi = (PersonIdent) value;

         String valueStr;
        if (pi != null)
            valueStr = pi.getName() + " <" + pi.getEmailAddress() + ">";
        else
            valueStr = "";
        return super.getTableCellRendererComponent(table, valueStr, isSelected, hasFocus, row, column);
    }
}
