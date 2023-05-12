import java.net.URL;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;

public class Ship extends MovingThing {
    private int speed;
    private Image image;

    public Ship() {
        this(0, 0, 50, 50, 0);
    }

    public Ship(int x, int y) {
        this(x, y, 50, 50, 0);
    }

    public Ship(int x, int y, int s) {
        this(x, y, 50, 50, s);
    }

    public Ship(int x, int y, int w, int h, int s) {
        super(x, y, w, h);
        speed = s;
        try {
            URL url = getClass().getResource("ship.jpg");
            image = ImageIO.read(url);
        } catch (Exception e) {
            System.err.println("Error while trying to load ship image: " + e.getStackTrace());
        }
    }

    public void setSpeed(int s) {
        speed = s;
    }

    public int getSpeed() {
        return speed;
    }

    public void move(String direction) {
        if (direction.equals("LEFT") && getX() >= 0) {
            setX(getX() - speed);
        } else if (direction.equals("RIGHT") && getX() <= 750) {
            setX(getX() + speed);
        } else if (direction.equals("UP") && getY() >= 0) {
            setY(getY() - speed);
        } else if (direction.equals("DOWN") && getY() <= 520) {
            setY(getY() + speed);
        }
    }

    public void draw(Graphics window) {
        window.drawImage(image, getX(), getY(), getWidth(), getHeight(), null);
    }

    public String toString() {
        return super.toString() + " " + getSpeed();
    }
}
