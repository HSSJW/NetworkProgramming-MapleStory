package Map;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MapData {
    private String backgroundImagePath;           // 배경 이미지 경로
    private CopyOnWriteArrayList<Rectangle> terrain; // 지형 데이터
    private CopyOnWriteArrayList<Portal> portals;    // 포탈 데이터

    public MapData(String backgroundImagePath, CopyOnWriteArrayList<Rectangle> terrain, CopyOnWriteArrayList<Portal> portals) {
        this.backgroundImagePath = backgroundImagePath;
        this.terrain = terrain;
        this.portals = portals;
    }

    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    public CopyOnWriteArrayList<Rectangle> getTerrain() {
        return terrain;
    }

    public CopyOnWriteArrayList<Portal> getPortals() {
        return portals;
    }

    // 정적 메서드를 통해 맵 데이터 제공
    public static CopyOnWriteArrayList<MapData> getMaps() {
        CopyOnWriteArrayList<MapData> maps = new CopyOnWriteArrayList<>();

        // 첫 번째 맵의 지형 데이터
        CopyOnWriteArrayList<Rectangle> map1Terrain = new CopyOnWriteArrayList<>();
        map1Terrain.add(new Rectangle(0, 650, 1400, 100)); // 바닥
        map1Terrain.add(new Rectangle(300, 400, 200, 20)); // 플랫폼 1
        map1Terrain.add(new Rectangle(100, 600, 50, 50));  // 첫 번째 계단
        map1Terrain.add(new Rectangle(150, 550, 50, 50)); // 두 번째 계단
        map1Terrain.add(new Rectangle(200, 500, 50, 50)); // 세 번째 계단
        map1Terrain.add(new Rectangle(250, 450, 50, 50)); // 네 번째 계단

        // 첫 번째 맵의 포탈 데이터
        CopyOnWriteArrayList<Portal> map1Portals = new CopyOnWriteArrayList<>();
        map1Portals.add(new Portal(1000, 480)); // 포탈 위치

        maps.add(new MapData("images/map/east_road.png", map1Terrain, map1Portals));

        // 두 번째 맵의 지형 데이터
        CopyOnWriteArrayList<Rectangle> map2Terrain = new CopyOnWriteArrayList<>();
        map2Terrain.add(new Rectangle(0, 500, 800, 100)); // 바닥
        map2Terrain.add(new Rectangle(250, 350, 150, 20)); // 플랫폼 2

        // 두 번째 맵의 포탈 데이터
        CopyOnWriteArrayList<Portal> map2Portals = new CopyOnWriteArrayList<>();
        map2Portals.add(new Portal(700, 400)); // 포탈 위치

        maps.add(new MapData("images/map/lis.gif", map2Terrain, map2Portals));

        return maps;
    }
}
