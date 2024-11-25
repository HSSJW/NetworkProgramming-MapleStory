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
}

