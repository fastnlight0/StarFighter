import java.awt.Color;
import java.awt.Graphics;

public class Ammo extends MovingThing {
    private int speed;

    public Ammo() {
        this(10, 10, 10, 10, 0);
    }

    public Ammo(int x, int y) {
        this(x, y, 10, 10, 0);
    }

    public Ammo(int x, int y, int s) {
        this(x, y, 10, 10, s);
    }

    public Ammo(int x, int y, int w, int h, int s) {
        super(x, y, w, h);
        speed = s;
    }

    public void setSpeed(int s) {
        speed = s;
    }

    public int getSpeed() {
        return speed;
    }

    public void draw(Graphics window) {
        window.setColor(Color.YELLOW);
        window.fillRect(getX(), getY(), getWidth(), getHeight());
    }

    public void move(String direction) {
        setY(getY() - speed);
    }

    public String toString() {
        return super.toString() + getSpeed();
    }
}