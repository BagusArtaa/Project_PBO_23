import java.awt.Image;
import java.awt.Rectangle;

public abstract class Entity {
    private Image image;
    private int x, y;

    public Entity(Image image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }

    public abstract Rectangle getHitbox();

    public Image getImage() { return image; }
    public int getX() { return x; }
    public int getY() { return y; }
    protected void setX(int x) { this.x = x; }
    protected void setY(int y) { this.y = y; }

}
