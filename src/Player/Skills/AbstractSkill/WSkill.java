package Player.Skills.AbstractSkill;

import Player.Player1.Player1;
import Player.Player;
import Player.Skills.Skill;

import javax.swing.*;
import java.awt.*;

public abstract class WSkill extends Skill {
    // 원본 이미지 크기에 맞게 수정
    protected int skillWidth;  // 동적 크기를 위해 변경
    protected int skillHeight; // 동적 크기를 위해 변경
    private static final int SKILL_DURATION = 2000; // 1초로 증가



    public WSkill(Player owner) {
        super("W_Skill", 30, 2000, SKILL_DURATION, owner); // 쿨다운 2초, 지속시간 1초

        loadSkillImages(); // 명시적으로 이미지 로드 호출
    }

    @Override
    public void activate(boolean facingRight) {
        if (canUse()) {
            this.facingRight = facingRight;
            isActive = true;
            lastUseTime = System.currentTimeMillis();

            // hitbox 초기화에 yOffset 적용
            int hitboxX = facingRight ?
                    owner.getX() + owner.getWidth() :
                    owner.getX() - skillWidth;
            int hitboxY = owner.getY() + yOffset;  // yOffset 적용
            hitbox = new Rectangle(hitboxX, hitboxY, skillWidth, skillHeight);
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
        // 객체가 활성화 상태(isActive)이고 히트박스(hitbox)가 존재하는 경우에만 그리기 작업 수행
        if (isActive && hitbox != null) {
            // 캐릭터가 바라보는 방향(facingRight)에 따라 오른쪽 이미지를 사용할지(leftImage)를 결정
            Image currentGif = facingRight ? rightImage : leftImage;

            // 선택된 이미지가 존재할 경우 그래픽 객체(g2d)를 사용하여 이미지 그리기
            if (currentGif != null) {
                g2d.drawImage(currentGif,  // 현재 이미지
                        hitbox.x, hitbox.y,  // 이미지의 위치를 히트박스의 x, y 좌표로 설정
                        skillWidth, skillHeight,  // 스킬 이미지의 동적 크기를 설정
                        observer);  // 이미지를 로드할 관찰자 객체
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
