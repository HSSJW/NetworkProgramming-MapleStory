package Player.Skills.AbstractSkill;

import Player.Player1.Player1;
import Player.Player;
import Player.Skills.Skill;

import javax.swing.*;
import java.awt.*;

public abstract class WSkill extends Skill {
    protected static final int SKILL_WIDTH = 200;
    protected static final int SKILL_HEIGHT = 100;

    public WSkill(Player owner) {
        super("W_Skill", 40, 5000, 700, owner);
    }

    @Override
    public void update() {
        if (isActive) {
            int hitboxX = facingRight ?
                    owner.getX() + owner.getWidth() :
                    owner.getX() - SKILL_WIDTH;
            hitbox = new Rectangle(hitboxX, owner.getY() - 20, SKILL_WIDTH, SKILL_HEIGHT);

            if (!isGifPlaying()) {
                isActive = false;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2d, Component observer) {
        if (isActive) {
            ImageIcon currentGif = facingRight ? gifRight : gifLeft;
            if (currentGif != null) {
                Image frame = currentGif.getImage();
                int x = hitbox.x;
                int y = hitbox.y;

                g2d.drawImage(frame, x, y, SKILL_WIDTH, SKILL_HEIGHT, observer);
            }
        }
    }

    protected boolean isGifPlaying() {
        ImageIcon currentGif = facingRight ? gifRight : gifLeft;
        return currentGif != null && currentGif.getImageObserver() != null;
    }
}
