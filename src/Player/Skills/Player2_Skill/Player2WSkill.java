package Player.Skills.Player2_Skill;

import Player.Skills.AbstractSkill.WSkill;
import Player.Player;

import javax.swing.*;

// Player2 W스킬
public class Player2WSkill extends WSkill {
    public Player2WSkill(Player owner) {
        super(owner);
        this.yOffset = -80;
    }

    @Override
    protected void loadSkillImages() {

            rightImage = new ImageIcon("images/Player/Player2/skill/paler2_w_right.gif").getImage();
            leftImage = new ImageIcon("images/Player/Player2/skill/paler2_w_left.gif").getImage();

            // 오른쪽 이미지 기준으로 스킬 크기 설정
            updateSkillDimensions(rightImage);


    }
}
