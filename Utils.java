import java.util.Random;

public class Utils {
    private final Random rnd = new Random();

    public int getRandomInt(int min, int max) {
        return (int) Math.floor(rnd.nextDouble() * (max - min)) + min;
    }
}
