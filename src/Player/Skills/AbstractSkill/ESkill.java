package Player.Skills.AbstractSkill;

import Player.Player1.Player1;
import Player.Player;
import Player.Skills.Skill;

import javax.swing.*;
import java.awt.*;

// E 스킬: 전체 공격
public abstract class ESkill extends Skill {
    protected static final int SKILL_WIDTH = 800;
    protected static final int SKILL_HEIGHT = 200;

    public ESkill(Player owner) {
        super("E_Skill", 50, 10000, 1000, owner);
    }

    @Override
    public void update() {
        if (isActive) {
            hitbox = new Rectangle(0, 0, 1400, 800); // 전체 맵 범위

            if (!isGifPlaying()) {
                isActive = false;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2d, Component observer) {
        if (isActive) {
            // E스킬은 방향 상관없이 하나의 gif만 사용
            if (gifRight != null) {
                Image frame = gifRight.getImage();
                g2d.drawImage(frame,
                        (1400 - SKILL_WIDTH) / 2,
                        50,
                        SKILL_WIDTH,
                        SKILL_HEIGHT,
                        observer);
            }
        }
    }

    protected boolean isGifPlaying() {
        return gifRight != null && gifRight.getImageObserver() != null;
    }
}
