// --------------------------------------- ESkill.java ---------------------------------------
package Player.Skills.AbstractSkill;

import Player.Player;
import Player.Skills.Skill;
import java.awt.*;

public abstract class ESkill extends Skill {
    protected int skillWidth;
    protected int skillHeight;
    private static final int SKILL_DURATION = 3000;
    private static final int MAP_WIDTH = 1400;  // 맵 너비
    private static final int MAP_HEIGHT = 800;  // 맵 높이

    public ESkill(Player owner) {
        super("E_Skill", 50, 10000, SKILL_DURATION, owner);
        loadSkillImages();
    }

    @Override
    public void activate(boolean facingRight) {
        if (canUse()) {
            this.facingRight = facingRight;
            isActive = true;
            lastUseTime = System.currentTimeMillis();

            // 맵 중앙에 스킬 위치 설정
            int centerX = (MAP_WIDTH - skillWidth) / 2;
            int centerY = (MAP_HEIGHT - skillHeight) / 2;

            // 히트박스도 맵 전체에 적용
            hitbox = new Rectangle(0, 0, MAP_WIDTH, MAP_HEIGHT);  // 공격 판정용 히트박스

            // 시각적 위치는 별도로 저장 (draw에서 사용)
            visualX = centerX;
            visualY = centerY;
        }
    }

    private int visualX, visualY;  // 시각적 표현을 위한 좌표

    @Override
    public void update() {
        if (isActive) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastUseTime;

            if (elapsedTime >= duration) {
                isActive = false;
                return;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2d, Component observer) {
        if (isActive && rightImage != null) {
            // visualX, visualY 위치에 이미지 그리기
            g2d.drawImage(rightImage,
                    visualX,
                    visualY,
                    skillWidth,
                    skillHeight,
                    observer);
        }
    }

    protected void updateSkillDimensions(Image image) {
        if (image != null) {
            MediaTracker tracker = new MediaTracker(new Container());
            tracker.addImage(image, 0);
            try {
                tracker.waitForID(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            skillWidth = image.getWidth(null);
            skillHeight = image.getHeight(null);
        }
    }

    @Override
    protected abstract void loadSkillImages();
}