import java.net.URL;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.imageio.ImageIO;

public class Powerup extends MovingThing {
    private Image image;
    private int type; // 0: star, 1: rapid fire, 2: 1up
    private int rotation;

    public Powerup(int x, int y, int t) {
        this(x, y, 50, 50, t);
    }

    public Powerup(int x, int y, int w, int h, int t) {
        super(x, y, w, h);
        type = t;
        rotation = 0;
        try {
            String name = "";
            switch (type) {
                case 0:
                    name = "star.jpeg";
                    break;
                case 1:
                    name = "rapidfire.png";
                    break;
                case 2:
                    name = "1up.png";
                    break;
            }
            URL url = getClass().getResource(name);
            image = ImageIO.read(url);
        } catch (Exception e) {
            // feel free to do something here or not
            System.err.println("ur trash at coding lmao: " + e.getStackTrace());
        }
    }

    public boolean hitPlayer(Ship player) {
        // if the player is to the left of the powerup
        if (player.getX() + player.getWidth() < getX()) {
            return false;
        }
        // if the player is to the right of the powerup
        if (player.getX() > getX() + getWidth()) {
            return false;
        }
        // if the player is above the powerup
        if (player.getY() + player.getHeight() < getY()) {
            return false;
        }
        // if the player is below the powerup
        if (player.getY() > getY() + getHeight()) {
            return false;
        }
        return true;
    }

    public void setType(int s) {
        type = s;
    }

    public int getType() {
        return type;
    }

    public void draw(Graphics window) {
        Graphics2D g2d = (Graphics2D) window;
        g2d.rotate(Math.toRadians(rotation), getX() + getWidth() / 2, getY() + getHeight() / 2);
        g2d.drawImage(image, getX(), getY(), getWidth(), getHeight(), null);
    }

    public String toString() {
        return super.toString() + " " + getType();
    }

    public void move(String direction) {
        rotation += 1;
        if (rotation >= 360) {
            rotation = 0;
        }
    }
}
