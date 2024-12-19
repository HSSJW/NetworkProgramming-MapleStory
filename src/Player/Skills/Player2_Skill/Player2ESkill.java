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
        // E스킬은 방향 구분 없이 하나의 이미지만 사용
        gifRight = new ImageIcon("images/skills/player2/e_skill.gif");
    }
}
