package Player.Player1;

import Player.Player;
import Player.Skills.Player1_Skill.Player1ESkill;
import Player.Skills.Player1_Skill.Player1QSkill;
import Player.Skills.Player1_Skill.Player1WSkill;

import javax.swing.*;

public class Player1 extends Player {

    public Player1(int startX, int startY) {
        super(1, startX, startY);
    }

    @Override
    protected void initializeImages() {
        standRightImage = new ImageIcon("images/Player/Player1/player_stand_right.png").getImage();
        standLeftImage = new ImageIcon("images/Player/Player1/player_stand_left.png").getImage();
        leftImage = new ImageIcon("images/Player/Player1/player_walk_left.gif").getImage();
        rightImage = new ImageIcon("images/Player/Player1/player_walk_right.gif").getImage();
        jumpRightImage = new ImageIcon("images/Player/Player1/player_jump_right.png").getImage();
        jumpLeftImage = new ImageIcon("images/Player/Player1/player_jump_left.png").getImage();
        hitRightImage = new ImageIcon("images/Player/Player1/player_hit_right.png").getImage();
        hitLeftImage = new ImageIcon("images/Player/Player1/player_hit_left.png").getImage();

    }

    @Override
    protected void initializeSkills() {
        System.out.println("Initializing Player1 skills"); // 디버깅용
        qSkill = new Player1QSkill(this);
        wSkill = new Player1WSkill(this);
        eSkill = new Player1ESkill(this);

        // 스킬이 제대로 초기화되었는지 확인
        if (qSkill != null) {
            System.out.println("Q skill initialized successfully"); // 디버깅용
        }
    }
}
