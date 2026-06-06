import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

public class Game {
    private final App a;

    public Cell[][] gBoard = new Cell[0][0];
    public GameState gGame = new GameState();
    public Level gLevel = new Level();

    public Game(App a) {
        this.a = a;
        this.gLevel.bestTime = a.store.getInt("bestTimeEasy");
    }

    public void init() {
        a.gPlayer.setText(App.NORMAL);
        gBoard = buildBoard();
        a.render.renderBoard(gBoard);

        int size = gLevel.size;

        a.gLives.setText(size == 4 ? "❤️" : "❤️❤️❤️");
        a.gHints.setText(size == 4 ? "Hint: 💡" : "Hints: 💡💡💡");
        a.gSafe.setText(size == 4 ? "🔍" : size == 8 ? "🔍🔍" : "🔍🔍🔍");

        if (gLevel.bestTime == 0) return; 
        a.timer.renderBestTime();
    }

    public Cell[][] buildBoard() {
        Cell[][] board = new Cell[gLevel.size][gLevel.size];
        for (int i = 0; i < gLevel.size; i++) {
            for (int j = 0; j < gLevel.size; j++) {
                board[i][j] = new Cell(i, j);
            }
        }
        return board;
    }

    public void placeMines(int maxMine, int iExist, int jExist) {
        int countMine = 0;
        while (countMine < maxMine) {
            int size = gLevel.size;
            int i = a.util.getRandomInt(0, size);
            int j = a.util.getRandomInt(0, size);

            if (gBoard[i][j].isMine) continue;
            if (i == iExist && j == jExist) continue;

            gBoard[i][j].isMine = true;
            countMine++;
        }
    }

    public int countMines3x3(Cell[][] thisBoard, int rowIndex, int colIndex) {
        int count = 0;
        for (int i = rowIndex - 1; i <= rowIndex + 1; i++) {
            if (i < 0 || i > thisBoard.length - 1) continue;
            for (int j = colIndex - 1; j <= colIndex + 1; j++) {
                if (j < 0 || j > thisBoard[0].length - 1) continue;
                if (i == rowIndex && j == colIndex) continue;
                if (thisBoard[i][j].isMine) count++;
            }
        }
        return count;
    }

    public void setNeighbor() {
        for (int i = 0; i < gBoard.length; i++) {
            for (int j = 0; j < gBoard.length; j++) {
                if (gBoard[i][j].isMine) continue;
                int mineCount = countMines3x3(gBoard, i, j);
                gBoard[i][j].minesAroundCount = mineCount; // 0 renders as empty
            }
        }
    }

    public void cellClicked(JLabel thisCell, int i, int j) {
        Cell cell = gBoard[i][j];

        if (cell.isShown || cell.isMarked) return;

        if (!gGame.isRun && gGame.isFirst) {
            gGame.isRun = true;
            gGame.isFirst = false;
            a.gPlayer.setText(App.FIRE);

            a.timer.start();

            placeMines(gLevel.mines, i, j);
            setNeighbor();
        }
        // Hint feature
        else if (a.feature.gHintOn) {
            a.feature.showHint(i, j);
            return;
        }
        // Click mine
        else if (cell.isMine) {
            if (gGame.isSound) a.soundManage.bombSound();

            cell.isShown = true;

            if (gLevel.lives > 1) {
                gLevel.lives--;

                a.feature.gMoves.push(cell);
                a.render.renderCell(i, j, App.MINE);

                int remainLives = gLevel.lives;
                if (remainLives == 2) {
                    a.gLives.setText("❤️❤️💔");
                } else if (remainLives == 1) {
                    a.gLives.setText("❤️💔💔");
                }
                a.gPlayer.setText(App.SAD);
                return;
            } else if (gLevel.lives == 1) {
                a.render.renderBombs();
                gameOver("lost");
                return;
            }
        }

        // Main
        if (gGame.isRun) {
            if (cell.minesAroundCount > 0) {
                thisCell.setText(String.valueOf(cell.minesAroundCount));
                a.feature.gMoves.push(cell);
            } else {
                a.render.renderNeighbors(i, j);
                List<Cell> snapshot = new ArrayList<>(a.feature.gNrMoves);
                snapshot.add(cell);
                a.feature.gMoves.push(snapshot);
                a.feature.gNrMoves = new ArrayList<>();
            }

            a.render.addPressed(i, j);
            cell.isShown = true;
            if (isWin()) gameOver("win");
        }
    }

    public void handleFlag(int i, int j) {
        Cell cell = gBoard[i][j];

        if (!gGame.isRun || (cell.isShown && !cell.isMine)) return;

        if (!cell.isMarked) {
            a.render.renderCell(i, j, App.FLAG);
            cell.isMarked = true;
            if (isWin()) gameOver("win");
        } else {
            if (cell.isMine && cell.isShown) return;
            a.render.renderCell(i, j, " ");
            cell.isMarked = false;
        }
    }

    public void gameOver() {
        gameOver(null);
    }

    public void gameOver(String status) {
        a.timer.stop();
        gGame.isRun = false;

        if ("win".equals(status)) {
            a.gPlayer.setText(App.WIN);
            a.timer.setBestTime();
            if (gGame.isSound) a.soundManage.winSound();
        } else if ("lost".equals(status)) {
            a.gLives.setText(gLevel.size == 4 ? "💔" : "💔💔💔");
            a.gPlayer.setText(App.LOSE);
            if (gGame.isSound) a.soundManage.loseSound();
        }
    }

    public boolean isWin() {
        for (int i = 0; i < gBoard.length; i++) {
            for (int j = 0; j < gBoard.length; j++) {
                Cell cell = gBoard[i][j];
                if (cell.isMine && !cell.isMarked) return false;
                if (!cell.isMine && !cell.isShown) return false;
            }
        }
        return true;
    }

    public void restart() {
        a.timer.stop();
        gGame.isRun = false;
        gGame.isFirst = true;
        gGame.shownCount = 0;
        gGame.markedCount = 0;
        gGame.secsPassed = 0;
        a.gTimer.setText("00:00");
        gLevel.hints = gLevel.size == 4 ? 1 : 3;
        gLevel.lives = gLevel.size == 4 ? 1 : 3;
        gLevel.safeClicks = 1;
        a.feature.gMoves.clear();
        a.feature.gNrMoves = new ArrayList<>();
        a.gBestTime.setText("");
        init();
    }
}
