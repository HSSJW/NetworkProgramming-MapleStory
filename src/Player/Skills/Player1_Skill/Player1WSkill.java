package Player.Skills.Player1_Skill;

import Player.Skills.AbstractSkill.WSkill;
import Player.Player;

import javax.swing.*;
import java.awt.*;

// Player1 W스킬
public class Player1WSkill extends WSkill {
    public Player1WSkill(Player owner) {
        super(owner);
        this.yOffset = -170;
    }

    @Override
    protected void loadSkillImages() {
            rightImage = new ImageIcon("images/Player/Player1/skill/player1-w-right.gif").getImage();
            leftImage = new ImageIcon("images/Player/Player1/skill/player1-w-left.gif").getImage();

            // 오른쪽 이미지 기준으로 스킬 크기 설정
            updateSkillDimensions(rightImage);


    }
}
