package Monster;

import javax.swing.*;
import java.awt.*;

public class MushRoom extends Monster {
    public MushRoom(int startX, int startY, int mapIndex) {
        super(startX, startY, mapIndex);
        this.hp = 100;
        this.maxHp = 100;
        this.width = 70;
        this.height = 70;
    }

    @Override
    protected void initializeImages() {
        // 기본 상태 이미지
        idleRightImage = new ImageIcon("images/monster/mushroom/mushroom_idle_right.gif").getImage();
        idleLeftImage = new ImageIcon("images/monster/mushroom/mushroom_idle_left.gif").getImage();

// 이동 상태 이미지
        moveRightImage = new ImageIcon("images/monster/mushroom/mushroom_moving_right.gif").getImage();
        moveLeftImage = new ImageIcon("images/monster/mushroom/mushroom_moving_left.gif").getImage();

// 피격 상태 이미지
        hitRightImage = new ImageIcon("images/monster/mushroom/mushroom_hit_right.gif").getImage();
        hitLeftImage = new ImageIcon("images/monster/mushroom/mushroom_hit_left.gif").getImage();

// 사망 상태 이미지
        deadRightImage = new ImageIcon("images/monster/mushroom/mushroom_dead_right.gif").getImage();
        deadLeftImage = new ImageIcon("images/monster/mushroom/mushroom_dead_left.gif").getImage();
    }
}