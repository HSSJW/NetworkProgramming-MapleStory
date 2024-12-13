package Map;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Map1 extends MapData {
    @Override
    protected void initializeMap() {
        this.baseImagePath = "images/map/baseImage/map1_base.png";
        this.backgroundImagePath = "images/map/east_road-Photoroom.png";
        this.backgroundMusicPath = "SoundTrack/henesis_background.wav";
        this.backgroundYOffset = 130;

        // 지형 데이터 초기화
        this.terrain = new CopyOnWriteArrayList<>(
                java.util.List.of(
                        new Rectangle(45, 605, 433, 20), //1-1
                        new Rectangle(475, 640, 130, 20), //1-2
                        new Rectangle(600, 605, 344, 20), //1-3
                        new Rectangle(940, 640, 130, 20), //1-4
                        new Rectangle(1062, 605, 305, 20), //1-5
                        new Rectangle(365, 540, 55, 20), //2-1
                        new Rectangle(1055, 510, 70, 20), //2-2
                        new Rectangle(425, 465, 645, 20), //3층 가로바닥
                        new Rectangle(1092, 420, 80, 20), //4층 공중계단
                        new Rectangle(980, 375, 80, 20), //공중계단 2
                        new Rectangle(835, 340, 130, 20) //공중계단 3

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