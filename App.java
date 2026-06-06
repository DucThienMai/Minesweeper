import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


public class App {
    public static final String NORMAL = "▶️";
    public static final String SAD = "😂";
    public static final String WIN = "Congratulation 🥳";
    public static final String LOSE = "Try again 😑";
    public static final String FIRE = "Status: 🔥";

    public static final String MINE = "💣";
    public static final String FLAG = "🚩";

    public static final Font HEADER_FONT = new Font("SansSerif", Font.PLAIN, 22);
    public static final Font CELL_FONT = new Font("SansSerif", Font.BOLD, 24);
    public static final Font BTN_FONT = new Font("SansSerif", Font.PLAIN, 16);

    public final Utils util = new Utils();
    public final Store store = new Store();
    public final SoundManage soundManage = new SoundManage();
    public final Render render = new Render(this);
    public final Timer timer = new Timer(this);
    public final Features feature = new Features(this);
    public final Game game = new Game(this);

    public JFrame frame;
    public JPanel boardPanel;
    public JLabel gSafe;
    public JLabel gHints;
    public JLabel gLives;
    public JLabel gPlayer;
    public JLabel gBestTime;
    public JLabel gTimer;

    public static String disp(int count) {
        return count > 0 ? String.valueOf(count) : "";
    }

    private void buildUi() {
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main = new JPanel();
        main.setBackground(java.awt.Color.WHITE);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // game-options: level buttons
        JPanel options = new JPanel();
        options.setBackground(java.awt.Color.WHITE);
        options.add(makeLevelButton("Easy 💣2", 4));
        options.add(makeLevelButton("Medium 💣12", 8));
        options.add(makeLevelButton("Hard 💣30", 12));
        options.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(options);

        // game-header
        gSafe = makeHeaderLabel(e -> showSafeCell());
        gHints = makeHeaderLabel(e -> hintOn());
        gLives = makeHeaderLabel(null);
        gPlayer = makeHeaderLabel(e -> restart());

        JPanel header = new JPanel();
        header.setBackground(java.awt.Color.WHITE);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        for (JLabel l : new JLabel[] { gSafe, gHints, gLives, gPlayer }) {
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            header.add(l);
        }
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(header);

        // game-board
        boardPanel = new JPanel();
        boardPanel.setBackground(java.awt.Color.WHITE);
        boardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(boardPanel);

        // timer + best-time
        gTimer = new JLabel("00:00", SwingConstants.CENTER);
        gTimer.setFont(HEADER_FONT);
        gTimer.setAlignmentX(Component.CENTER_ALIGNMENT);
        gTimer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        main.add(gTimer);

        gBestTime = new JLabel("", SwingConstants.CENTER);
        gBestTime.setFont(HEADER_FONT);
        gBestTime.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(gBestTime);

        // undo button
        main.add(Box.createVerticalStrut(10));
        JButton undoBtn = new JButton("Undo 🔙");
        undoBtn.setFont(BTN_FONT);
        undoBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        undoBtn.addActionListener(e -> undo());
        main.add(undoBtn);

        frame.setContentPane(main);
    }

    private JButton makeLevelButton(String text, int levelSize) {
        JButton btn = new JButton(text);
        btn.setFont(BTN_FONT);
        btn.addActionListener(e -> handleLevel(levelSize));
        return btn;
    }

    private interface ClickAction {
        void run(MouseEvent e);
    }

    private JLabel makeHeaderLabel(ClickAction action) {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setFont(HEADER_FONT);
        label.setPreferredSize(new Dimension(260, 34));
        label.setMaximumSize(new Dimension(400, 40));
        if (action != null) {
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    action.run(e);
                }
            });
        }
        return label;
    }

    public void init() {
        game.init();
    }

    public void handleLevel(int levelSize) {
        switch (levelSize) {
            case 4:
                game.gLevel.size = 4;
                game.gLevel.mines = 2;
                game.gLevel.lives = 1;
                game.gLevel.hints = 1;
                game.gLevel.safeClicks = 1;
                game.gLevel.bestTime = store.getInt("bestTimeEasy");
                break;
            case 8:
                game.gLevel.size = 8;
                game.gLevel.mines = 12;
                game.gLevel.lives = 3;
                game.gLevel.hints = 3;
                game.gLevel.safeClicks = 2;
                game.gLevel.bestTime = store.getInt("bestTimeMedium");
                break;
            case 12:
                game.gLevel.size = 12;
                game.gLevel.mines = 30;
                game.gLevel.lives = 3;
                game.gLevel.hints = 3;
                game.gLevel.safeClicks = 3;
                game.gLevel.bestTime = store.getInt("bestTimeHard");
                break;
        }
        game.gameOver();
        game.restart();
        game.init();
    }

    public void cellClicked(JLabel thisCell, int i, int j) {
        game.cellClicked(thisCell, i, j);
    }

    public void handleFlag(int i, int j) {
        game.handleFlag(i, j);
    }

    public void hintOn() {
        feature.hintOn();
    }

    public void restart() {
        game.restart();
    }

    public void showSafeCell() {
        feature.showSafeCell();
    }

    public void undo() {
        feature.undo();
    }

    private void start() {
        buildUi();
        init();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().start());
    }
}
