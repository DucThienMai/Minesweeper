public class Timer {
    private final App a;
    private javax.swing.Timer timerInterval = null;

    public Timer(App a) {
        this.a = a;
    }

    public void start() {
        timerInterval = new javax.swing.Timer(1000, e -> {
            a.game.gGame.secsPassed++;
            int secs = a.game.gGame.secsPassed;
            int minutes = secs / 60;
            int seconds = secs % 60;
            a.gTimer.setText(String.format("%02d:%02d", minutes, seconds));
        });
        timerInterval.start();
    }

    public void stop() {
        if (timerInterval != null) {
            timerInterval.stop();
            timerInterval = null;
        }
    }

    public void setBestTime() {
        int levelSize = a.game.gLevel.size;
        String level = levelSize == 4 ? "bestTimeEasy"
                : (levelSize != 0 ? "bestTimeMedium" : "bestTimeHard");

        int currentTime = a.game.gGame.secsPassed;
        String stored = a.store.get(level);

        if (stored == null || stored.isEmpty() || currentTime < Integer.parseInt(stored)) {
            a.store.set(level, String.valueOf(currentTime));
            a.game.gLevel.bestTime = currentTime;
            renderBestTime();
        }
    }

    public void renderBestTime() {
        int bestTime = a.game.gLevel.bestTime;
        if (bestTime < 60) {
            a.gBestTime.setText("😱 " + bestTime + " seconds");
        } else if (bestTime > 60) {
            double mins = bestTime / 60.0;
            a.gBestTime.setText("😱 " + String.format("%.2f", mins) + " minutes");
        }
    }
}
