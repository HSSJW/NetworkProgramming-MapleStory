package Map;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Map1 extends MapData {
    @Override
    protected void initializeMap() {
        this.baseImagePath = "images/map/baseImage/map1_base.png";
        this.backgroundImagePath = "images/map/east_road-Photoroom.png";
        this.backgroundMusicPath = "SoundTrack/henesis_background.wav";
        this.backgroundColor = new Color(135, 206, 250);
        this.backgroundYOffset = 130;

        // 지형 데이터 초기화
        this.terrain = new CopyOnWriteArrayList<>(
                java.util.List.of(
                        new Rectangle(45, 605, 435, 20),
                        new Rectangle(475, 640, 130, 20),
                        new Rectangle(600, 605, 352, 20),
                        new Rectangle(940, 640, 130, 20),
                        new Rectangle(1062, 605, 305, 20),
                        new Rectangle(365, 540, 50, 20),
                        new Rectangle(1055, 510, 70, 20),
                        new Rectangle(425, 465, 650, 20),
                        new Rectangle(1100, 420, 80, 20),
                        new Rectangle(980, 375, 80, 20),
                        new Rectangle(840, 340, 130, 20)
                )
        );

        // 포탈 데이터 초기화
        this.portals = new CopyOnWriteArrayList<>(
                java.util.List.of(
                        new Portal(1250, 460, 1, 700, 400)
                )
        );
    }
}