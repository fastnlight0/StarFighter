import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Bullets {
    private List<Ammo> ammo;

    public Bullets() {
        ammo = new ArrayList<>();
    }

    public void add(Ammo al) {
        ammo.add(al);
    }

    public void drawEmAll(Graphics window) {
        for (Ammo am : ammo) {
            am.draw(window);
        }
    }

    public void moveEmAll() {
        for (Ammo am : ammo) {
            am.move("Yes");
        }
    }

    public void cleanEmUp() {
        for (int i = ammo.size() - 1; i >= 0; i--) {
            if (ammo.get(i).getY() <= 0) {
                ammo.remove(i);
            }
        }
    }

    public List<Ammo> getList() {
        return ammo;
    }

    public String toString() {
        return "" + ammo;
    }
}