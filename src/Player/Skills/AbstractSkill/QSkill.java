package Player.Skills.AbstractSkill;

import Player.Player;
import Player.Skills.Skill;

import javax.swing.*;
import java.awt.*;

public abstract class QSkill extends Skill {
    // 원본 이미지 크기에 맞게 수정
    protected int skillWidth;  // 동적 크기를 위해 변경
    protected int skillHeight; // 동적 크기를 위해 변경
    private static final int SKILL_DURATION = 2000; // 1초로 증가

    public QSkill(Player owner) {
        super("Q_Skill", 30, 2000, SKILL_DURATION, owner); // 쿨다운 2초, 지속시간 1초

        loadSkillImages(); // 명시적으로 이미지 로드 호출
    }

    @Override
    public void activate(boolean facingRight) {
        if (canUse()) {
            this.facingRight = facingRight;
            isActive = true;
            lastUseTime = System.currentTimeMillis();
            // hitbox 초기화 - 실제 이미지 크기 사용
            int hitboxX = facingRight ?
                    owner.getX() + owner.getWidth() :
                    owner.getX() - skillWidth;
            hitbox = new Rectangle(hitboxX, owner.getY(), skillWidth, skillHeight);
        }
    }

    @Override
    public void update() {
        if (isActive) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastUseTime;

            // 스킬 지속 시간 체크
            if (elapsedTime >= duration) {
                isActive = false;
                return;
            }

            // hitbox 업데이트
//            int hitboxX = facingRight ?
//                    owner.getX() + owner.getWidth() :
//                    owner.getX() - SKILL_WIDTH;
//            hitbox.setLocation(hitboxX, owner.getY());
        }
    }

    @Override
    public void draw(Graphics2D g2d, Component observer) {
        if (isActive && hitbox != null) {
            Image currentGif = facingRight ? rightImage : leftImage;
            if (currentGif != null) {
                g2d.drawImage(currentGif,
                        hitbox.x, hitbox.y,
                        skillWidth, skillHeight,  // 동적 크기 사용
                        observer);
            }
        }
    }

    // 이미지 크기를 가져오는 유틸리티 메소드 추가
    protected void updateSkillDimensions(Image image) {
        if (image != null) {
            // 이미지가 완전히 로드될 때까지 대기
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
    
    //gif가 재생중인지 판별
//    protected boolean isGifPlaying() {
//        ImageIcon currentGif = facingRight ? gifRight : gifLeft;
//        return currentGif != null && currentGif.getImageObserver() != null;
//    }

    //이미지 로딩 함수
    @Override
    protected abstract void loadSkillImages();

}