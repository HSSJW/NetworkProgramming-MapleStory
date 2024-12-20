package Player.Skills.Player2_Skill;

import Player.Skills.AbstractSkill.ESkill;
import Player.Player;

import javax.swing.*;

// Player2 E스킬
public class Player2ESkill extends ESkill {
    public Player2ESkill(Player owner) {
        super(owner);
    }

    @Override
    protected void loadSkillImages() {

        // E스킬은 방향 구분 없이 하나의 이미지만 사용하므로 rightImage에만 할당
        rightImage = new ImageIcon("images/Player/Player2/skill/player2_e.gif").getImage();
        // 스킬 크기 설정
        updateSkillDimensions(rightImage);
    }
}
