package Player.Skills;

import Player.Player;
import javax.swing.*;
import java.awt.*;

// 기본 스킬 클래스
public abstract class Skill {
    protected String name;
    protected int damage;
    protected long cooldown;
    protected long lastUseTime;
    protected boolean isActive;
    protected int duration;
    protected Player owner;
    protected Rectangle hitbox;
    protected boolean facingRight;
    protected ImageIcon gifRight;
    protected ImageIcon gifLeft;
    protected Image rightImage;
    protected Image leftImage;


    public Skill(String name, int damage, long cooldown, int duration, Player owner) {
        this.name = name;
        this.damage = damage;
        this.cooldown = cooldown;
        this.duration = duration;
        this.owner = owner;
        this.isActive = false;
        this.lastUseTime = 0;
        loadSkillImages();
    }

    protected abstract void loadSkillImages();
    public abstract void update();
    public abstract void draw(Graphics2D g2d, Component observer);

    public boolean canUse() {
        return System.currentTimeMillis() - lastUseTime >= cooldown;
    }

    public void activate(boolean facingRight) {
        if (canUse()) {
            this.facingRight = facingRight;
            isActive = true;
            lastUseTime = System.currentTimeMillis();
        }
    }

    public boolean isActive() { return isActive; }
    public Rectangle getHitbox() { return hitbox; }
}