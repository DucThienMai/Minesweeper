import java.io.File;

public class SoundManage {
    private final String winPath;
    private final String losePath;
    private final String bombPath;
    private final boolean isWindows =
        System.getProperty("os.name", "").toLowerCase().contains("win");

    public SoundManage() {
        winPath = new File("assets/audio/win.mp3").getAbsolutePath();
        losePath = new File("assets/audio/lose.wav").getAbsolutePath();
        bombPath = new File("assets/audio/bomb.mp3").getAbsolutePath();
    }

    public void winSound() {
        play(winPath);
    }

    public void loseSound() {
        play(losePath);
    }

    public void bombSound() {
        play(bombPath);
    }

    private void play(String path) {
        File f = new File(path);
        if (!f.exists()) return;
        try {
            if (isWindows) {
                playWindows(path);
            } else {
                playSampled(f);
            }
        } catch (Exception ignored) {
        }
    }

    private void playWindows(String path) throws Exception {
        String script =
                "Add-Type -AssemblyName PresentationCore;" +
                "$p = New-Object System.Windows.Media.MediaPlayer;" +
                "$p.Open([uri]'" + path.replace("'", "''") + "');" +
                "$p.Play();" +
                "Start-Sleep -Seconds 5";
        ProcessBuilder pb = new ProcessBuilder(
                "powershell", "-NoProfile", "-WindowStyle", "Hidden",
                "-ExecutionPolicy", "Bypass", "-Command", script);
        pb.start(); // fire and forget
    }

    private void playSampled(File f) throws Exception {
        javax.sound.sampled.AudioInputStream ais =
                javax.sound.sampled.AudioSystem.getAudioInputStream(f);
        javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
        clip.open(ais);
        clip.start();
    }
}
