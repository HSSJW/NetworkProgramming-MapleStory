package Map;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MapData {
    private String backgroundImagePath;           // 배경 이미지 경로
    private Color backgroundColor;               // 배경 색상
    private int backgroundYOffset;               // 배경 이미지 Y 오프셋
    private CopyOnWriteArrayList<Rectangle> terrain; // 지형 데이터
    private CopyOnWriteArrayList<Portal> portals;    // 포탈 데이터

    public MapData(String backgroundImagePath, Color backgroundColor, int backgroundYOffset,
                   CopyOnWriteArrayList<Rectangle> terrain, CopyOnWriteArrayList<Portal> portals) {
        this.backgroundImagePath = backgroundImagePath;
        this.backgroundColor = backgroundColor;
        this.backgroundYOffset = backgroundYOffset;
        this.terrain = terrain;
        this.portals = portals;
    }

    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public int getBackgroundYOffset() {
        return backgroundYOffset;
    }

    public CopyOnWriteArrayList<Rectangle> getTerrain() {
        return terrain;
    }

    public CopyOnWriteArrayList<Portal> getPortals() {
        return portals;
    }

    // 기존 getMaps 메서드에 배경색과 Y 오프셋만 추가
    public static CopyOnWriteArrayList<MapData> getMaps() {
        CopyOnWriteArrayList<MapData> maps = new CopyOnWriteArrayList<>();

        // 첫 번째 맵
        maps.add(new MapData(
                "images/map/east_road-Photoroom.png",          // 배경 이미지
                new Color(135, 206, 250),           // 하늘색 배경
                130,                                // Y 오프셋
                new CopyOnWriteArrayList<>(         // 지형 데이터
                        java.util.List.of(
                                new Rectangle(50, 610, 435, 20),//1-1
                                new Rectangle(485, 640, 130, 20),//1-2
                                new Rectangle(615, 610, 352, 20),//1-3
                                new Rectangle(962, 640, 130, 20),//1-4
                                new Rectangle(1097, 610, 305, 20),//1-5
                                new Rectangle(380, 550, 50, 20), //2-1
                                new Rectangle(1080, 520, 70, 20),//2-2
                                new Rectangle(430, 480, 650, 20),//3층 가로바닥
                                new Rectangle(1110, 450, 90, 20), //4층 공중계단
                                new Rectangle(1010, 415, 90, 20), //공중계단 2
                                new Rectangle(860, 380, 130, 20) // 공중계단 3
                        )
                ),
                new CopyOnWriteArrayList<>(         // 포탈 데이터
                        java.util.List.of(
                                new Portal(1250, 460, 1, 700, 400)
                        )
                )
        ));

        // 두 번째 맵
        maps.add(new MapData(
                "images/map/lis.gif",               // 배경 이미지
                new Color(255, 228, 181),           // 밝은 살구색 배경
                50,                                // Y 오프셋
                new CopyOnWriteArrayList<>(         // 지형 데이터
                        java.util.List.of(
                                new Rectangle(0, 500, 800, 100),
                                new Rectangle(250, 350, 150, 20)
                        )
                ),
                new CopyOnWriteArrayList<>(         // 포탈 데이터
                        java.util.List.of(
                                new Portal(700, 400, 1, 700, 400)
                        )
                )
        ));

        return maps;
    }
}
