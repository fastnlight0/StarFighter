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
        // add code to draw the ammo
        // ammo will be a yellow square
        // use window.setColor(COLOR) to set the color
        // if you don't set a color, your ammo will be black and you will not see it
        // use window.fillRect(x,y,w,h); to make a rectangle/square
        window.setColor(Color.YELLOW);
        window.fillRect(getX(), getY(), getWidth(), getHeight());
    }

    public void move(String direction) {
        // add code to move the ammo
        // ship ammo should only move up
        setY(getY() - speed);
    }

    public String toString() {
        return super.toString() + getSpeed();
    }
}