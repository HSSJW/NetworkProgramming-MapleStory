package Player.Skills.Player2_Skill;

import Player.Skills.AbstractSkill.WSkill;
import Player.Player;

import javax.swing.*;

// Player2 W스킬
public class Player2WSkill extends WSkill {
    public Player2WSkill(Player owner) {
        super(owner);
    }

    @Override
    protected void loadSkillImages() {
        gifRight = new ImageIcon("images/skills/player2/w_right.gif");
        gifLeft = new ImageIcon("images/skills/player2/w_left.gif");
    }
}
