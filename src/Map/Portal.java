package Map;

import javax.swing.*;
import java.awt.*;

public class Portal {
    private int x, y, width, height; // 포탈 위치와 크기
    private Image image = new ImageIcon("images/map/portal.png").getImage();             // 포탈 이미지

    public Portal(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 150;

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImage() {
        return image;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g, Component observer) {
        g.drawImage(image, x, y, width, height, observer);

    }
}

