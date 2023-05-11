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
            // this sets ship.jpg as the image for your ship
            // for this to work ship.jpg needs to be in the same folder as this .java file
            URL url = getClass().getResource("ship.jpg");
            image = ImageIO.read(url);
        } catch (Exception e) {
            // feel free to do something here or not
            System.err.println("ur trash at coding lmao: " + e.getStackTrace());
        }
    }

    public void setSpeed(int s) {
        speed = s;
    }

    public int getSpeed() {
        return speed;
    }

    public void move(String direction) {
        // add code here
        // think about ALL your global variables and how you can use them to "move"
        // keep your parameter in mind as well
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
