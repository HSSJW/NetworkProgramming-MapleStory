package Map;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Map3 extends MapData {
    @Override
    protected void initializeMap() {
        this.baseImagePath = "images/map/baseImage/map3_base.png";
        this.backgroundImagePath = "images/map/Ludibrium.png";
        this.backgroundMusicPath = "SoundTrack/Ludibrium.wav";
        this.backgroundColor = new Color(255, 228, 181);
        this.backgroundYOffset = 100;

        // 지형 데이터 초기화
        this.terrain = new CopyOnWriteArrayList<>(
                java.util.List.of(
                        new Rectangle(0, 583, 1500, 100),
                        new Rectangle(250, 350, 150, 20)
                )
        );

        // 포탈 데이터 초기화
        this.portals = new CopyOnWriteArrayList<>(
                java.util.List.of(
                        new Portal(700, 400, 0, 700, 400)
                )
        );
    }
}