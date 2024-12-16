package Monster;

import Map.MapData;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MonsterManager {
    private CopyOnWriteArrayList<Monster> monsters;
    private Map<Integer, List<MonsterSpawnData>> mapMonsters;

    private static class MonsterSpawnData {
        final Class<? extends Monster> monsterClass;
        final int x;
        final int y;

        MonsterSpawnData(Class<? extends Monster> monsterClass, int x, int y) {
            this.monsterClass = monsterClass;
            this.x = x;
            this.y = y;
        }
    }

    public MonsterManager() {
        this.monsters = new CopyOnWriteArrayList<>();
        this.mapMonsters = new HashMap<>();
        initializeMapMonsters();
    }

    private void initializeMapMonsters() {
        // y 좌표를 몬스터 크기만큼 위로 조정
        int spawnYOffset = 70; // 몬스터 height만큼 위로 조정

        // 맵 1의 몬스터 스폰 위치 설정
        List<MonsterSpawnData> map1Monsters = Arrays.asList(
                new MonsterSpawnData(MushRoom.class, 200, 550 - spawnYOffset),  // 1층 왼쪽
                new MonsterSpawnData(MushRoom.class, 700, 550 - spawnYOffset),  // 1층 중앙
                new MonsterSpawnData(MushRoom.class, 1200, 550 - spawnYOffset), // 1층 오른쪽
                new MonsterSpawnData(MushRoom.class, 600, 410 - spawnYOffset)   // 3층 중앙
        );
        mapMonsters.put(0, map1Monsters);

        // 맵 2의 몬스터 스폰 위치 설정
        List<MonsterSpawnData> map2Monsters = Arrays.asList(
                new MonsterSpawnData(MushRoom.class, 300, 550 - spawnYOffset),
                new MonsterSpawnData(MushRoom.class, 800, 550 - spawnYOffset)
        );
        mapMonsters.put(1, map2Monsters);

        // 맵 3의 몬스터 스폰 위치 설정
        List<MonsterSpawnData> map3Monsters = Arrays.asList(
                new MonsterSpawnData(MushRoom.class, 400, 550 - spawnYOffset),
                new MonsterSpawnData(MushRoom.class, 900, 550 - spawnYOffset)
        );
        mapMonsters.put(2, map3Monsters);
    }

    public void initializeMonsters(int mapIndex) {
        monsters.clear();
        List<MonsterSpawnData> spawnDataList = mapMonsters.get(mapIndex);

        if (spawnDataList != null) {
            for (MonsterSpawnData spawnData : spawnDataList) {
                try {
                    Monster monster = spawnData.monsterClass
                            .getDeclaredConstructor(int.class, int.class)
                            .newInstance(spawnData.x, spawnData.y);
                    monsters.add(monster);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateMonsters(MapData currentMap) {
        for (Monster monster : monsters) {
            if (monster.isAlive()) {
                monster.update(currentMap);
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