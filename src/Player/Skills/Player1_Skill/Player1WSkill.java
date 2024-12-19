package Player.Skills.Player1_Skill;

import Player.Skills.AbstractSkill.WSkill;
import Player.Player;

import javax.swing.*;
import java.awt.*;

// Player1 W스킬
public class Player1WSkill extends WSkill {
    public Player1WSkill(Player owner) {
        super(owner);
    }

    @Override
    protected void loadSkillImages() {
        gifRight = new ImageIcon("images/skills/player1/w_right.gif");
        gifLeft = new ImageIcon("images/skills/player1/w_left.gif");
    }
}
