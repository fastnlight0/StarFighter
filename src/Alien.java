import java.net.URL;

import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;

public class Alien extends MovingThing {
    private int speed;
    private Image image;

    private Bomb bomb;

    public Alien() {
        this(0, 0, 30, 30, 0);
    }

    public Alien(int x, int y) {
        this(x, y, 30, 30, 0);
    }

    public Alien(int x, int y, int s) {
        this(x, y, 30, 30, s);
    }

    public Alien(int x, int y, int w, int h, int s) {
        super(x, y, w, h);
        speed = s;
        bomb = null;
        try {
            URL url = getClass().getResource("alien.jpg");
            image = ImageIO.read(url);
        } catch (Exception e) {
            System.err.println("Error while trying to load alien image: " + e.getStackTrace());
        }
    }

    public void setSpeed(int s) {
        speed = s;
    }

    public int getSpeed() {
        return speed;
    }

    public void move(String direction) {
        // moves alien in space invaders pattern
        if (getX() <= 0 || getX() >= 800) {
            setY(getY() + 40);
            speed = -speed;
        }
        setX(getX() + speed);
    }

    public void dropBomb() {
        bomb = new Bomb(getX(), getY(), 3);
    }

    public void drawBomb(Graphics window) {
        if (bomb != null) {
            bomb.draw(window);
        }
    }

    public void moveBomb() {
        if (bomb != null) {
            bomb.move("DOWN");
            if (bomb.getY() >= 600) {
                bomb = null;
            }
        }
    }

    public boolean droppedBomb() {
        return bomb != null;
    }

    public boolean bombHitShip(Ship ship) {
        return bomb != null && bomb.isTouchingShip(ship);
    }

    public void draw(Graphics window) {
        move("DOWN");
        window.drawImage(image, getX(), getY(), getWidth(), getHeight(), null);
    }

    public String toString() {
        return super.toString() + getSpeed();
    }
}