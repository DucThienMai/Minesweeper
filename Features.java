import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Features {
    private final App a;

    public Deque<Object> gMoves = new ArrayDeque<>();
    public List<Cell> gNrMoves = new ArrayList<>();
    public boolean gHintOn = false;

    public Features(App a) {
        this.a = a;
    }

    public void hintOn() {
        if (a.game.gLevel.hints == 0) return;
        if (!a.game.gGame.isRun) return;

        gHintOn = true;
        a.game.gLevel.hints--;

        if (a.game.gLevel.size == 4) {
            a.gHints.setText("Hint: ✖️");
            return;
        }

        int remainHints = a.game.gLevel.hints;
        switch (remainHints) {
            case 2:
                a.gHints.setText("Hints: 💡💡✖️");
                break;
            case 1:
                a.gHints.setText("Hints: 💡✖️✖️");
                break;
            default:
                a.gHints.setText("Hints: ✖️✖️✖️");
                break;
        }
    }

    public void showHint(int iIndex, int jIndex) {
        Cell[][] board = a.game.gBoard;
        for (int i = iIndex - 1; i <= iIndex + 1; i++) {
            if (i < 0 || i > board.length - 1) continue;

            for (int j = jIndex - 1; j <= jIndex + 1; j++) {
                if (j < 0 || j > board[0].length - 1) continue;
                Cell curCell = board[i][j];

                if (curCell.isShown || curCell.isMarked) continue;

                if (curCell.isMine) a.render.renderCell(i, j, App.MINE);
                else if (curCell.minesAroundCount > 0)
                    a.render.renderCell(i, j, String.valueOf(curCell.minesAroundCount));
                else a.render.renderCell(i, j, " ");

                final int fi = i;
                final int fj = j;
                javax.swing.Timer t = new javax.swing.Timer(1000,
                        e -> a.render.renderCell(fi, fj, " "));
                t.setRepeats(false);
                t.start();
            }
        }
        gHintOn = false;
    }

    public void showSafeCell() {
        if (!a.game.gGame.isRun) return;
        if (a.game.gLevel.safeClicks == 0) return;

        a.game.gLevel.safeClicks--;
        int remains = a.game.gLevel.safeClicks;

        a.gSafe.setText(remains == 1 ? "🔍" : remains == 2 ? "🔍🔍" : "✖️");

        List<int[]> safeCells = new ArrayList<>();
        Cell[][] board = a.game.gBoard;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                Cell cell = board[i][j];
                if (cell.isMine || cell.isShown) continue;
                safeCells.add(new int[] { i, j });
            }
        }
        if (safeCells.isEmpty()) return;

        int[] randomIndex = safeCells.get(a.util.getRandomInt(0, safeCells.size()));
        final int si = randomIndex[0];
        final int sj = randomIndex[1];

        a.render.addSafe(si, sj);

        javax.swing.Timer t = new javax.swing.Timer(2000,
                e -> a.render.removeSafe(si, sj));
        t.setRepeats(false);
        t.start();
    }

    @SuppressWarnings("unchecked")
    public void undo() {
        if (!a.game.gGame.isRun) return;
        if (gMoves.isEmpty()) return;

        Object move = gMoves.pop();

        if (move instanceof List) {
            for (Cell c : (List<Cell>) move) {
                reverseMove(c);
            }
        } else {
            reverseMove((Cell) move);
        }
    }

    public void reverseMove(Cell cell) {
        cell.isShown = false;

        if (cell.isMine) {
            a.game.gLevel.lives++;
            a.gLives.setText(a.gLives.getText().equals("❤️❤️💔") ? "❤️❤️❤️" : "❤️❤️💔");
        }

        a.render.removePressed(cell.i, cell.j);
        a.render.renderCell(cell.i, cell.j, "");
    }
}
