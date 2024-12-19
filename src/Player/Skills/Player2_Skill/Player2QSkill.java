package Player.Skills.Player2_Skill;

import Player.Skills.AbstractSkill.QSkill;
import Player.Player;
import Player.Skills.Skill;

import javax.swing.*;
import java.awt.*;

// Player2 Q스킬
public class Player2QSkill extends QSkill {
    public Player2QSkill(Player owner) {
        super(owner);
    }

    @Override
    protected void loadSkillImages() {
        gifRight = new ImageIcon("images/skills/player2/q_right.gif");
        gifLeft = new ImageIcon("images/skills/player2/q_left.gif");
    }
}
