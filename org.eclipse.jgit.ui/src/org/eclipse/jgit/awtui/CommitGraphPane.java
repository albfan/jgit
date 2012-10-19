/*
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 * and other copyright owners as documented in the project's IP log.
 *
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Distribution License v1.0 which
 * accompanies this distribution, is reproduced below, and is
 * available at http://www.eclipse.org/org/documents/edl-v10.php
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - Neither the name of the Eclipse Foundation, Inc. nor the
 *   names of its contributors may be used to endorse or promote
 *   products derived from this software without specific prior
 *   written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.eclipse.jgit.awtui;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revwalk.RevWalk;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.IOException;

/**
 * Draws a commit graph in a JTable.
 * <p>
 * This class is currently a very primitive commit visualization tool. It shows
 * a table of 3 columns:
 * <ol>
 * <li>Commit graph and short message</li>
 * <li>Author name and email address</li>
 * <li>Author date and time</li>
 * </ul>
 */
public class CommitGraphPane extends JTable {
	private static final long serialVersionUID = 1L;

    static final Stroke[] strokeCache;

    static {
        strokeCache = new Stroke[4];
        for (int i = 1; i < strokeCache.length; i++)
            strokeCache[i] = new BasicStroke(i);
    }

	public CommitGraphPane() {
		configureHeader(false);
		setShowHorizontalLines(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowMargin(0);
        configureRowHeight();
	}

    public void setDecorate(boolean decorate) {
        configureHeader(decorate);
    }

    private void configureRowHeight() {
		int h = 0;
		for (int i = 0; i<getColumnCount(); ++i) {
			TableCellRenderer renderer = getDefaultRenderer(getColumnClass(i));
			Component c = renderer.getTableCellRendererComponent(this, "ÅOj", false, false, 0, i);
			h = Math.max(h, c.getPreferredSize().height);
		}
		setRowHeight(h +getRowMargin());
	}

	@Override
	public void setModel(final TableModel dataModel) {
		if (dataModel != null && !(dataModel instanceof CommitTableModel))
			throw new ClassCastException(UIText.get().mustBeSpecialTableModel);
		super.setModel(dataModel);
	}

	@Override
	protected TableModel createDefaultDataModel() {
		return new CommitTableModel();
	}

	private void configureHeader(boolean decorate) {
		final JTableHeader th = getTableHeader();
		final TableColumnModel cols = th.getColumnModel();

		final TableColumn graph = cols.getColumn(0);
        graph.setHeaderValue("");
        graph.setCellRenderer(new GraphCellRender(decorate));

        final TableColumn author = cols.getColumn(1);
        author.setHeaderValue(UIText.get().author);
        author.setCellRenderer(new NameCellRender());

        final TableColumn date = cols.getColumn(2);
        date.setHeaderValue(UIText.get().date);
        date.setCellRenderer(new DateCellRender());
	}

    static Stroke stroke(final int width) {
		if (width < strokeCache.length)
			return strokeCache[width];
		return new BasicStroke(width);
	}

    public void fill(RevWalk walk, int maxValue) throws IOException {
        ((CommitTableModel)getModel()).fill(walk, maxValue);
    }

    public void clear() {
        ((CommitTableModel)getModel()).getCommits().clear();
    }
}
