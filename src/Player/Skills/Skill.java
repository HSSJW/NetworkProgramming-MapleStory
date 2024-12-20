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
    protected int yOffset = 0;  // y축 오프셋 (양수: 아래로, 음수: 위로)   >> 기본값은 0이기 때문에 필요하다면 각각의 Concrete클래스에서 추가해서 사용하면됨
    protected int xOffset = 0;  // x축 오프셋 (양수: 오른쪽, 음수: 왼쪽)   >> 기본값은 0이기 때문에 필요하다면 각각의 Concrete클래스에서 추가해서 사용하면됨

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