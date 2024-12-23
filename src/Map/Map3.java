package Map;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Map3 extends MapData {
    @Override
    protected void initializeMap() {
        this.baseImagePath = "images/map/baseImage/map3_base.png";
        this.backgroundImagePath = "images/map/Ludibrium.png";
        this.backgroundMusicPath = "SoundTrack/Ludibrium.wav";
        this.backgroundYOffset = 100;

        // 지형 데이터 초기화
        this.terrain = new CopyOnWriteArrayList<>(
                java.util.List.of(
                        // 메인 바닥
                        new Rectangle(0, 583, 1500, 20),

                        // 블록 타워 (아래에서 위로, 왼쪽에서 오른쪽으로)
                        // 1층 (가장 아래층)
                        new Rectangle(400, 540, 40, 40),
                        new Rectangle(440, 540, 40, 40),
                        new Rectangle(480, 540, 40, 40),

// 2층
                        new Rectangle(420, 500, 40, 40),
                        new Rectangle(460, 500, 40, 40),
                        new Rectangle(500, 500, 40, 40),

// 3층
                        new Rectangle(440, 460, 40, 40),
                        new Rectangle(480, 460, 40, 40),

// 4층
                        new Rectangle(460, 420, 40, 40),
                        new Rectangle(500, 420, 40, 40),

// 5층
                        new Rectangle(480, 380, 40, 40),
                        new Rectangle(520, 380, 40, 40),

// 6층
                        new Rectangle(490, 340, 40, 40),

// 7층 (가장 위층)
                        new Rectangle(500, 300, 40, 40),


                        // 사다리 받침대
                        new Rectangle(50, 150, 50, 10)
                )
        );

        // 포탈 데이터 초기화
        this.portals = new CopyOnWriteArrayList<>(
                java.util.List.of(
                        new Portal(700, 400, 1, 1300, 500)
                )
        );
    }
}