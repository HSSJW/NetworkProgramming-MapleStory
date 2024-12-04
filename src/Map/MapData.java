package Map;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MapData {
    private String backgroundImagePath;           // 배경 이미지 경로
    private Color backgroundColor;               // 배경 색상
    private int backgroundYOffset;               // 배경 이미지 Y 오프셋
    private CopyOnWriteArrayList<Rectangle> terrain; // 지형 데이터
    private CopyOnWriteArrayList<Portal> portals;    // 포탈 데이터
    private String backgroundMusicPath;           // 배경음악 파일 경로


    public MapData(String backgroundImagePath, String backgroundMusicPath, Color backgroundColor, int backgroundYOffset,
                   CopyOnWriteArrayList<Rectangle> terrain, CopyOnWriteArrayList<Portal> portals) {
        this.backgroundImagePath = backgroundImagePath;
        this.backgroundMusicPath = backgroundMusicPath;
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

    public String getBackgroundMusicPath() {
        return backgroundMusicPath;
    }

    // 기존 getMaps 메서드에 배경색과 Y 오프셋만 추가
    public static CopyOnWriteArrayList<MapData> getMaps() {
        CopyOnWriteArrayList<MapData> maps = new CopyOnWriteArrayList<>();



        // 첫 번째 맵
        maps.add(new MapData(
                "images/map/east_road-Photoroom.png",          // 배경 이미지
                "SoundTrack/henesis_background.wav",
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
                                new Portal(1250, 460, 1, 700, 400) //커닝시티로 이동하는 포탈
                        )
                )
        ));

        // 두 번째 맵
        maps.add(new MapData(
                "images/map/KerningCity.png",             // 배경 이미지
                "SoundTrack/KerningCity_background.wav",
                new Color(255, 228, 181),           // 밝은 살구색 배경
                -20,                                // Y 오프셋
                new CopyOnWriteArrayList<>(         // 지형 데이터
                        java.util.List.of(
                                new Rectangle(0, 580, 1400, 20),    // 메인 바닥
                                new Rectangle(200, 480, 300, 20),   // 중간 왼쪽 발판
                                new Rectangle(600, 480, 300, 20),   // 중간 오른쪽 발판
                                new Rectangle(400, 380, 200, 20),   // 상단 중앙 발판
                                new Rectangle(100, 430, 150, 20),   // 왼쪽 중간 발판
                                new Rectangle(900, 430, 150, 20) ,   // 오른쪽 중간 발판
        new Rectangle(200, 500, 300, -150),  // 왼쪽에서 오른쪽으로 올라가는 경사,
                                // 왼쪽 계단형 지형
        new Rectangle(100, 530, 200, 20),   // 1단계
                new Rectangle(150, 520, 200, 20),   // 2단계
                new Rectangle(200, 510, 200, 20),   // 3단계
                new Rectangle(250, 500, 200, 20),   // 4단계

                // 중앙 계단형 지형
                new Rectangle(500, 530, 200, 20),   // 1단계
                new Rectangle(550, 480, 200, 20),   // 2단계
                new Rectangle(600, 430, 200, 20),   // 3단계

                // 오른쪽 계단형 지형
                new Rectangle(900, 530, 200, 20),   // 1단계
                new Rectangle(950, 480, 200, 20),   // 2단계
                new Rectangle(1000, 430, 200, 20),  // 3단계
                new Rectangle(1050, 380, 200, 20)   // 4단계
                        )
                ),
                new CopyOnWriteArrayList<>(         // 포탈 데이터
                        java.util.List.of(
                                new Portal(50, 500, 0, 700, 400),    // 왼쪽 포탈 (0번 맵으로)
                                new Portal(1300, 500, 2, 700, 400)   // 오른쪽 포탈 (2번 맵으로)
                        )
                )
        ));

        // 세 번째 맵
        maps.add(new MapData(
                "images/map/Ludibrium.png",             // 배경 이미지
                "SoundTrack/Ludibrium.wav",
                new Color(255, 228, 181),           // 밝은 살구색 배경
                100,                                // Y 오프셋
                new CopyOnWriteArrayList<>(         // 지형 데이터
                        java.util.List.of(
                                new Rectangle(0, 583, 1500, 100), //1층 바닥
                                new Rectangle(250, 350, 150, 20)
                        )
                ),
                new CopyOnWriteArrayList<>(         // 포탈 데이터
                        java.util.List.of(
                                new Portal(700, 400, 0, 700, 400)
                        )
                )
        ));

        System.out.println(maps.size());
        return maps;
    }
}
