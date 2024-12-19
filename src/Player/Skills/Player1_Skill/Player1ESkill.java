package Player.Skills.Player1_Skill;

import Player.Skills.AbstractSkill.ESkill;
import Player.Player;

import javax.swing.*;

// Player1 E스킬
public class Player1ESkill extends ESkill {
    public Player1ESkill(Player owner) {
        super(owner);
    }

    @Override
    protected void loadSkillImages() {
        // E스킬은 방향 구분 없이 하나의 이미지만 사용
        gifRight = new ImageIcon("images/skills/player1/e_skill.gif");
    }
}
