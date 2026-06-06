import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Store {
    private final File file = new File("best-times.properties");
    private final Properties props = new Properties();

    public Store() {
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (Exception ignored) {
            }
        }
    }

    public String get(String key) {
        return props.getProperty(key);
    }

    public void set(String key, String value) {
        props.setProperty(key, value);
        try (FileOutputStream out = new FileOutputStream(file)) {
            props.store(out, "Minesweeper best times");
        } catch (Exception ignored) {
        }
    }

    public int getInt(String key) {
        String v = get(key);
        if (v == null || v.isEmpty()) return 0;
        try {
            return (int) Double.parseDouble(v);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
