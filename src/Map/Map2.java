package Map;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Map2 extends MapData {
    @Override
    protected void initializeMap() {
        this.baseImagePath = "images/map/baseImage/map2_base.png";
        this.backgroundImagePath = "images/map/KerningCity.png";
        this.backgroundMusicPath = "SoundTrack/KerningCity_background.wav";

        this.backgroundYOffset = -20;

        // 지형 데이터 초기화
        this.terrain = new CopyOnWriteArrayList<>(
                java.util.List.of(
                        new Rectangle(0, 580, 1400, 20),
                        new Rectangle(200, 480, 300, 20),
                        new Rectangle(600, 480, 300, 20),
                        new Rectangle(400, 380, 200, 20),
                        new Rectangle(100, 430, 150, 20),
                        new Rectangle(900, 430, 150, 20),
                        new Rectangle(200, 500, 300, -150),
                        new Rectangle(100, 530, 200, 20),
                        new Rectangle(150, 520, 200, 20),
                        new Rectangle(200, 510, 200, 20),
                        new Rectangle(250, 500, 200, 20),
                        new Rectangle(500, 530, 200, 20),
                        new Rectangle(550, 480, 200, 20),
                        new Rectangle(600, 430, 200, 20),
                        new Rectangle(900, 530, 200, 20),
                        new Rectangle(950, 480, 200, 20),
                        new Rectangle(1000, 430, 200, 20),
                        new Rectangle(1050, 380, 200, 20)
                )
        );

        // 포탈 데이터 초기화
        this.portals = new CopyOnWriteArrayList<>(
                java.util.List.of(
                        new Portal(50, 500, 0, 700, 400),
                        new Portal(1300, 500, 2, 700, 400)
                )
        );
    }
}