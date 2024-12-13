package Map;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class MapData {
    protected String baseImagePath;  // 배경이미지
    protected String backgroundImagePath; // 지형이미지
    protected String backgroundMusicPath; //배경음악
    protected int backgroundYOffset;
    protected CopyOnWriteArrayList<Rectangle> terrain;
    protected CopyOnWriteArrayList<Portal> portals;

    public MapData() {
        initializeMap();
    }

    // 각 맵 클래스에서 구현할 추상 메서드
    protected abstract void initializeMap();

    // Getter 메서드들
    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    public String getBackgroundMusicPath() {
        return backgroundMusicPath;
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

    public String getBaseImagePath() {  // 배경이미지
        return baseImagePath;
    }

    // 모든 맵 데이터를 반환하는 정적 메서드
    public static CopyOnWriteArrayList<MapData> getMaps() {
        CopyOnWriteArrayList<MapData> maps = new CopyOnWriteArrayList<>();
        maps.add(new Map1());
        maps.add(new Map2());
        maps.add(new Map3());
        return maps;
    }
}
