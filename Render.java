import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Render {
    static final Color BASE_COLOR = new Color(0xDD, 0xDD, 0xDD);   // #ddd
    static final Color BORDER_COLOR = new Color(0xBB, 0xBB, 0xBB); // #bbb
    static final Color PRESSED_COLOR = Color.WHITE;                // #fff
    static final Color SAFE_COLOR = new Color(0xF5, 0xE3, 0xAD);   // #f5e3ad
    static final Color HOVER_COLOR = Color.CYAN;                   // aqua

    private final App a;

    private JLabel[][] cells;
    private boolean[][] pressed;
    private boolean[][] safe;

    public Render(App a) {
        this.a = a;
    }

    public void renderBoard(Cell[][] board) {
        int size = board.length;
        cells = new JLabel[size][size];
        pressed = new boolean[size][size];
        safe = new boolean[size][size];

        a.boardPanel.removeAll();
        a.boardPanel.setLayout(new GridLayout(size, size));

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final int fi = i;
                final int fj = j;

                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setOpaque(true);
                cell.setBackground(BASE_COLOR);
                cell.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
                cell.setPreferredSize(new Dimension(50, 50));
                cell.setFont(App.CELL_FONT);

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            a.handleFlag(fi, fj);
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            a.cellClicked(cells[fi][fj], fi, fj);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (!pressed[fi][fj] && !safe[fi][fj]) {
                            cells[fi][fj].setBackground(HOVER_COLOR);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        updateBg(fi, fj);
                    }
                });

                cells[i][j] = cell;
                a.boardPanel.add(cell);
            }
        }

        a.boardPanel.revalidate();
        a.boardPanel.repaint();
        a.frame.pack();
        a.frame.setLocationRelativeTo(null);
    }

    public void renderCell(int i, int j, String value) {
        cells[i][j].setText(value);
    }

    public void renderBombs() {
        Cell[][] board = a.game.gBoard;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j].isMine) renderCell(i, j, App.MINE);
            }
        }
    }

    public void renderNeighbors(int i, int j) {
        Cell[][] board = a.game.gBoard;
        for (int rowIdx = i - 1; rowIdx <= i + 1; rowIdx++) {
            if (rowIdx < 0 || rowIdx > board.length - 1) continue;

            for (int colIdx = j - 1; colIdx <= j + 1; colIdx++) {
                if (colIdx < 0 || colIdx > board[0].length - 1) continue;
                if (rowIdx == i && colIdx == j) continue;

                Cell currCell = board[rowIdx][colIdx];
                if (currCell.isMarked || currCell.isShown) continue;
                a.feature.gNrMoves.add(currCell);

                renderCell(rowIdx, colIdx, App.disp(currCell.minesAroundCount));
                currCell.isShown = true;
                addPressed(rowIdx, colIdx);
                if (currCell.minesAroundCount == 0) renderNeighbors(rowIdx, colIdx);
            }
        }
    }


    public void addPressed(int i, int j) {
        pressed[i][j] = true;
        updateBg(i, j);
    }

    public void removePressed(int i, int j) {
        pressed[i][j] = false;
        updateBg(i, j);
    }

    public void addSafe(int i, int j) {
        safe[i][j] = true;
        updateBg(i, j);
    }

    public void removeSafe(int i, int j) {
        safe[i][j] = false;
        updateBg(i, j);
    }

    private void updateBg(int i, int j) {
        JLabel c = cells[i][j];
        if (safe[i][j]) c.setBackground(SAFE_COLOR);
        else if (pressed[i][j]) c.setBackground(PRESSED_COLOR);
        else c.setBackground(BASE_COLOR);
    }
}
