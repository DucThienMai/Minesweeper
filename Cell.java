public class Cell {
    public int minesAroundCount = 0;
    public boolean isShown = false;
    public boolean isMarked = false;
    public boolean isMine = false;
    public int i;
    public int j;

    public Cell(int i, int j) {
        this.i = i;
        this.j = j;
    }
}
