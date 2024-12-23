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

        List<MonsterSpawnData> map1Monsters = Arrays.asList(
                new MonsterSpawnData(MushRoom.class, 200, 535, 0),
                new MonsterSpawnData(MushRoom.class, 700, 535, 0),
                new MonsterSpawnData(MushRoom.class, 1200, 535, 0),
                new MonsterSpawnData(MushRoom.class, 600, 395, 0)
        );
        mapMonsters.put(0, map1Monsters);


        List<MonsterSpawnData> map2Monsters = Arrays.asList(

                new MonsterSpawnData(Wolf.class, 550, 450, 1),
                new MonsterSpawnData(Wolf.class, 50, 450, 1),
                new MonsterSpawnData(Wolf.class, 400, 200, 1),
                new MonsterSpawnData(Wolf.class, 400, 50, 1)
        );
        mapMonsters.put(1, map2Monsters);


        List<MonsterSpawnData> map3Monsters = Arrays.asList(
                new MonsterSpawnData(Robot.class, 100, 300, 2),
                new MonsterSpawnData(Robot.class, 700, 300, 2),
                new MonsterSpawnData(Robot.class, 900, 300, 2),
                new MonsterSpawnData(Robot.class, 1000, 300, 2)

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

                }
            }
        }
    }



    public CopyOnWriteArrayList<Monster> getMonsters() {
        return monsters;
    }



    public boolean areAllMapMonstersDead(int mapIndex) {

        int aliveCount = 0;
        int deadCount = 0;

        for (Monster monster : monsters) {
            if (monster.getMapIndex() == mapIndex) {
                if (monster.isAlive()) {
                    aliveCount++;
                } else {
                    deadCount++;
                }
            }
        }

        // 해당 맵의 몬스터들이 모두 죽었는지 확인
        return aliveCount == 0 && deadCount > 0;
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