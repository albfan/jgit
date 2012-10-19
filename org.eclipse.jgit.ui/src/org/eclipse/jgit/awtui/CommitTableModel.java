package org.eclipse.jgit.awtui;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: alberto
 * Date: 19/10/12
 * Time: 19:47
 * To change this template use File | Settings | File Templates.
 */
public class CommitTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private final SwingCommitList commits;

    PlotCommit<SwingLane> lastCommit;


    PersonIdent lastAuthor;

    public CommitTableModel() {
        commits = new SwingCommitList();
    }

    public SwingCommitList getCommits() {
        return commits;
    }

    public void fill(RevWalk walk, int maxValue) throws IOException {
        getCommits().source(walk);
        getCommits().fillTo(maxValue);
    }

    public int getColumnCount() {
        return 3;
    }

    public int getRowCount() {
        return commits != null ? commits.size() : 0;
    }

    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final PlotCommit<SwingLane> c = commits.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return c;
            case 1:
                return authorFor(c);
            case 2:
                return authorFor(c);
            default:
                return null;
        }
    }

    PersonIdent authorFor(final PlotCommit<SwingLane> c) {
        if (c != lastCommit) {
            lastCommit = c;
            lastAuthor = c.getAuthorIdent();
        }
        return lastAuthor;
    }
}
