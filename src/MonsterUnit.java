import java.awt.Image;
import java.awt.Rectangle;

public class MonsterUnit extends Entity {
    private int hitW, hitH;
    private int velocity;
    private String text;

    public MonsterUnit(Image img, int x, int y,
                       int hitW, int hitH,
                       int velocity, String text) {
        super(img, x, y);
        this.hitW = hitW;
        this.hitH = hitH;
        this.velocity = velocity;
        this.text = text;
    }

    @Override
    public Rectangle getHitbox() {
        return new Rectangle(getX(), getY(), hitW, hitH);
    }

    public void move() { setX(getX() + velocity); }

    public void setVelocity(int v) { this.velocity = v; }

    public int getVelocity() { return velocity; }

    public boolean isFinished() { return text.isEmpty(); }

    public String getText() { return text; }

    public boolean consumeIfMatch(char c) {
        if (!text.isEmpty() && text.charAt(0) == c) {
            text = text.substring(1);
            return true;
        }
        return false;
    }
}
