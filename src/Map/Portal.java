package Map;

import javax.swing.*;
import java.awt.*;

public class Portal {
    private int x, y, width, height; // 포탈 위치와 크기
    private Image image = new ImageIcon("images/map/portal.png").getImage();             // 포탈 이미지
    private int nextMapIndex;
    private int spawnX, spawnY;

    public Portal(int x, int y, int nextMapIndex, int spawnX, int spawnY) {
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 150;
        this.nextMapIndex = nextMapIndex;
        this.spawnX = spawnX;  //다음 맵의 스폰될 포탈의 좌표
        this.spawnY = spawnY;

    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
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

    //포탈로 들어가면 이동할 맵의 Index
    public int getNextMapIndex() {
        return nextMapIndex;
    }

    public void draw(Graphics g, Component observer) {
        g.drawImage(image, x, y, width, height, observer);

    }
}

