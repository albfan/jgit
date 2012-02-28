/*
 * Copyright (C) 2010, Robin Rosenberg <robin.rosenberg@dewire.com>
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

package org.eclipse.jgit.pgm;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;

import org.eclipse.jgit.awtui.CommitGraphPane;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;

class Glog extends RevWalkTextBuiltin {
	final JFrame frame;

	final CommitGraphPane graphPane;
    private final JComboBox comboBranches;
    private ActionListener actionListener;
    private JTextField textField;
    private String[] args;

    Glog() {
		frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		graphPane = new CommitGraphPane();

		JScrollPane graphScroll = new JScrollPane(graphPane);

		JPanel buttons = new JPanel(new FlowLayout());
        JButton repaint = new JButton();
        repaint.setText(CLIText.get().repaint);
        repaint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphPane.repaint();
			}
		});
		buttons.add(repaint);

        JPanel branches = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        branches.add(new JLabel("Git Branches:"), c);
        c.gridy = 1;
        comboBranches = new JComboBox();                        
        branches.add(comboBranches, c);        
        textField = new JTextField();
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    showCommits(new String[]{textField.getText()});
                } catch (Exception e1) {
                }
            }
        });
        c.gridy = 2;
        branches.add(new JLabel("Git Revisions:"), c);
        c.gridy = 3;
        c.anchor = GridBagConstraints.NORTH;
        branches.add(textField, c);

        JPanel world = new JPanel(new BorderLayout());
		world.add(graphScroll, BorderLayout.CENTER);
        JPanel pBranch = new JPanel(new BorderLayout());
        pBranch.add(branches, BorderLayout.NORTH);
        world.add(pBranch, BorderLayout.EAST);
        world.add(buttons, BorderLayout.SOUTH);

		frame.getContentPane().add(world);
	}

    @Override
    protected void parseArguments(String[] args) {
        this.args = args;
        super.parseArguments(args);
    }

    @Override
    protected void run() throws Exception {
        String arg = null;
        for (int i = 0; i < args.length; i++) {
            arg = args[i];
            if (!arg.startsWith("-")) {
                break;
            }
        }
        if (arg != null) {
            textField.setText(arg);
        }
        comboBranches.removeActionListener(actionListener);
        comboBranches.removeAllItems();
        Map<String, Ref> refs = db.getAllRefs();
        Ref head = refs.get(Constants.HEAD);
        Iterator<String> it = refs.keySet().iterator();
        while (it.hasNext()) {
            String ref = it.next();
            if (ref.startsWith(Constants.R_HEADS) || ref.startsWith(Constants.R_REMOTES)) {
                comboBranches.addItem(ref);
            }
        }
        comboBranches.addItem("(All)");
        actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String branch = (String) comboBranches.getItemAt(comboBranches.getSelectedIndex());
                    showCommits(new String[]{branch});
                } catch (Exception e1) {
                }
            }
        };
        comboBranches.addActionListener(actionListener);
        super.run();
    }

    private void showCommits(String[] args) throws Exception {
        commits.clear();
        graphPane.getCommitList().clear();
        parseArguments(args);
        graphPane.invalidate();
        Glog.super.run();
    }

    @Override
	protected int walkLoop() throws Exception {
        graphPane.setDecorate(decorate);
		graphPane.getCommitList().source(walk);
		graphPane.getCommitList().fillTo(Integer.MAX_VALUE);

		frame.setTitle("[" + repoName() + "]");
		frame.pack();
		frame.setVisible(true);
		return graphPane.getCommitList().size();
	}

	@Override
	protected void show(final RevCommit c) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	protected RevWalk createWalk() {
		if (objects)
			throw die(CLIText.get().cannotUseObjectsWithGlog);
		final PlotWalk w = new PlotWalk(db);
		w.sort(RevSort.BOUNDARY, true);
		return w;
	}

	private String repoName() {
		final File gitDir = db.getDirectory();
		if (gitDir == null)
			return db.toString();
		String n = gitDir.getName();
		if (Constants.DOT_GIT.equals(n))
			n = gitDir.getParentFile().getName();
		return n;
	}
}
