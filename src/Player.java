import java.awt.Image;
import java.awt.Rectangle;

public class Player extends Entity {
    private final int hitW = 100;
    private final int hitH = 600;

    private int lives = 3;

    public Player(Image img, int x, int y) {
        super(img, x, y);
    }

    @Override
    public Rectangle getHitbox() {
        return new Rectangle(getX(), getY(), hitW, hitH);
    }

    public void takeHit() { lives--; }
    public int getLives() { return lives; }
    public boolean isDead() { return lives <= 0; }

}
