package Monster;

import Map.MapData;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MonsterManager {
    private CopyOnWriteArrayList<Monster> monsters;
    private Map<Integer, List<MonsterSpawnData>> mapMonsters;
    private CopyOnWriteArrayList<MapData> maps;  // 맵 데이터 참조 추가


    private static class MonsterSpawnData {
        final Class<? extends Monster> monsterClass;
        final int x;
        final int y;
        final int mapIndex;  // mapIndex 추가

        MonsterSpawnData(Class<? extends Monster> monsterClass, int x, int y, int mapIndex) {
            this.monsterClass = monsterClass;
            this.x = x;
            this.y = y;
            this.mapIndex = mapIndex;
        }
    }

    public MonsterManager() {
        this.monsters = new CopyOnWriteArrayList<>();
        this.mapMonsters = new HashMap<>();
        this.maps = MapData.getMaps();  // 모든 맵 데이터 가져오기
        initializeMapMonsters();
    }



    private void initializeMapMonsters() {
        mapMonsters = new HashMap<>();

        // Map1 (index 0)의 몬스터 스폰 데이터
        List<MonsterSpawnData> map1Monsters = Arrays.asList(
                new MonsterSpawnData(MushRoom.class, 200, 535, 0),
                new MonsterSpawnData(MushRoom.class, 700, 535, 0),
                new MonsterSpawnData(MushRoom.class, 1200, 535, 0),
                new MonsterSpawnData(MushRoom.class, 600, 395, 0)
        );
        mapMonsters.put(0, map1Monsters);

        // Map2 (index 1)의 몬스터 스폰 데이터 - Map2의 지형에 맞게 조정
        List<MonsterSpawnData> map2Monsters = Arrays.asList(
                new MonsterSpawnData(MushRoom.class, 150, 580, 1),    // 1층
                new MonsterSpawnData(MushRoom.class, 500, 580, 1),
                new MonsterSpawnData(MushRoom.class, 1000, 580, 1),
                new MonsterSpawnData(MushRoom.class, 400, 410, 1),    // 2층
                new MonsterSpawnData(MushRoom.class, 900, 410, 1),
                new MonsterSpawnData(MushRoom.class, 300, 230, 1)     // 3층
        );
        mapMonsters.put(1, map2Monsters);

        // Map3 (index 2)의 몬스터 스폰 데이터 - Map3의 지형에 맞게 조정
        List<MonsterSpawnData> map3Monsters = Arrays.asList(
                new MonsterSpawnData(MushRoom.class, 100, 513, 2),    // 바닥
                new MonsterSpawnData(MushRoom.class, 500, 513, 2),
                new MonsterSpawnData(MushRoom.class, 900, 513, 2),
                new MonsterSpawnData(MushRoom.class, 420, 470, 2),    // 블록 타워
                new MonsterSpawnData(MushRoom.class, 440, 390, 2),
                new MonsterSpawnData(MushRoom.class, 480, 310, 2)
        );
        mapMonsters.put(2, map3Monsters);
    }


    public void initializeMonsters(int mapIndex) {


        monsters.clear();  // 기존 몬스터 제거
        List<MonsterSpawnData> spawnDataList = mapMonsters.get(mapIndex);

        if (spawnDataList != null) {


            for (MonsterSpawnData spawnData : spawnDataList) {
                try {
                    Constructor<?> constructor = spawnData.monsterClass.getDeclaredConstructor(
                            int.class, int.class, int.class);
                    Monster monster = (Monster) constructor.newInstance(
                            spawnData.x, spawnData.y, mapIndex);
                    monsters.add(monster);


                } catch (Exception e) {
                    System.err.println("Error spawning monster: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            System.err.println("No spawn data found for map " + mapIndex);
        }
    }

    public void updateMonsters(MapData currentMap) {
        for (Monster monster : monsters) {
            if (monster.isAlive()) {
                // 몬스터의 맵 인덱스에 해당하는 맵 데이터를 가져와서 업데이트
                MapData monsterMap = maps.get(monster.getMapIndex());
                monster.update(monsterMap);  // 각 몬스터는 자신이 속한 맵의 데이터로 업데이트
            }
        }
    }


    public CopyOnWriteArrayList<Monster> getMonsters() {
        return monsters;
    }

    public void respawnMonsters(int mapIndex) {
        initializeMonsters(mapIndex);
    }

    public boolean handleMonsterHit(int monsterId, int damage) {
        if (monsterId >= 0 && monsterId < monsters.size()) {
            Monster monster = monsters.get(monsterId);
            if (monster.isAlive()) {
                monster.takeDamage(damage);
                return true;
            }
        }
        return false;
    }
}