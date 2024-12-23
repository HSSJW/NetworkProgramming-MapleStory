package Monster;

import Map.MapData;

import javax.swing.*;
import java.awt.*;

public class Robot extends Monster{
    public Robot(int startX, int startY, int mapIndex) {
        super(startX, startY, mapIndex);
        this.width = 90;
        this.height = 100;
        this.hp = 100;
        this.maxHp = 100;

        // hitbox를 몬스터 크기에 맞게 초기화
        this.hitbox = new Rectangle(x + 10, y + 10, width - 20, height - 20);

        // 이미지 로드
        initializeImages();
    }

    @Override
    public void update(MapData currentMap) {
        super.update(currentMap);
        // hitbox 위치 업데이트
        if (hitbox != null) {
            hitbox.setLocation(x + 10, y + 10);
            hitbox.setSize(width - 20, height - 20);
        }
    }

    @Override
    protected void initializeImages() {
        // 기본 상태 이미지
        idleRightImage = new ImageIcon("images/monster/robot/robot_idle_right.gif").getImage();
        idleLeftImage = new ImageIcon("images/monster/robot/robot_idle_left.gif").getImage();

        // 이동 상태 이미지
        moveRightImage = new ImageIcon("images/monster/robot/robot_walk_right.gif").getImage();
        moveLeftImage = new ImageIcon("images/monster/robot/robot_walk_left.gif").getImage();

        // 피격 상태 이미지
        hitRightImage = new ImageIcon("images/monster/robot/robot_hit_right.gif").getImage();
        hitLeftImage = new ImageIcon("images/monster/robot/robot_hit_left.gif").getImage();

        // 사망 상태 이미지
        deadRightImage = new ImageIcon("images/monster/robot/robot_die_right.gif").getImage();
        deadLeftImage = new ImageIcon("images/monster/robot/robot_die_left.gif").getImage();

        // 현재 이미지 초기화
        currentImage = idleRightImage;
    }
}
