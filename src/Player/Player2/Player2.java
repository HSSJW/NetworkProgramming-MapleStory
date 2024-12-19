package Player.Player2;

import Player.Player;
import Player.Skills.Player2_Skill.Player2ESkill;
import Player.Skills.Player2_Skill.Player2QSkill;
import Player.Skills.Player2_Skill.Player2WSkill;

import javax.swing.*;

public class Player2 extends Player {

    public Player2(int startX, int startY) {
        super(2, startX, startY);
    }

    @Override
    protected void initializeImages() {
        standRightImage = new ImageIcon("images/Player/Player2/player_stand_right.png").getImage();
        standLeftImage = new ImageIcon("images/Player/Player2/player_stand_left.png").getImage();
        leftImage = new ImageIcon("images/Player/Player2/player_walk_left.gif").getImage();
        rightImage = new ImageIcon("images/Player/Player2/player_walk_right.gif").getImage();
        jumpRightImage = new ImageIcon("images/Player/Player2/player_jump_right.png").getImage();
        jumpLeftImage = new ImageIcon("images/Player/Player2/player_jump_left.png").getImage();
        hitRightImage = new ImageIcon("images/Player/Player2/player_hit_right.png").getImage();
        hitLeftImage = new ImageIcon("images/Player/Player2/player_hit_left.png").getImage();

    }

    @Override
    protected void initializeSkills() {
        qSkill = new Player2QSkill(this);
        wSkill = new Player2WSkill(this);
        eSkill = new Player2ESkill(this);
    }
}
