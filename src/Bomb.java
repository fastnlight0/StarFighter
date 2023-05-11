import java.awt.Color;
import java.awt.Graphics;

public class Bomb extends MovingThing {
    private int speed;

    public Bomb() {
        this(10, 10, 10, 10, 0);
    }

    public Bomb(int x, int y) {
        this(x, y, 10, 10, 0);
    }

    public Bomb(int x, int y, int s) {
        this(x, y, 10, 10, s);
    }

    public Bomb(int x, int y, int w, int h, int s) {
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
        window.setColor(Color.RED);
        window.fillRect(getX(), getY(), getWidth(), getHeight());
    }

    public void move(String direction) {
        setY(getY() + speed);
    }

    public boolean isTouchingShip(Ship ship) {
        return getX() >= ship.getX() && getX() <= ship.getX() + ship.getWidth() && getY() >= ship.getY()
                && getY() <= ship.getY() + ship.getHeight();
    }

    public String toString() {
        return super.toString() + getSpeed();
    }
}