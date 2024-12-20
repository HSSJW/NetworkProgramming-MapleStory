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
        super("Q_Skill", 5, 2000, SKILL_DURATION, owner); // 쿨다운 2초, 지속시간 1초

        loadSkillImages(); // 명시적으로 이미지 로드 호출
    }

    @Override
    public void activate(boolean facingRight) {
        if (canUse()) {
            this.facingRight = facingRight;
            isActive = true;
            lastUseTime = System.currentTimeMillis();

            // hitbox 위치 계산 수정
            int hitboxX;
            if (facingRight) {
                hitboxX = owner.getX() + owner.getWidth() - 20; // 약간의 여백
            } else {
                hitboxX = owner.getX() - skillWidth + 20; // 약간의 여백
            }

            // y 위치 조정 (캐릭터 중앙 높이에 맞춤)
            int hitboxY = owner.getY() + (owner.getHeight() / 2) - (skillHeight / 2) + yOffset;

            // hitbox 생성
            hitbox = new Rectangle(hitboxX, hitboxY, skillWidth - 20, skillHeight - 20); // 약간 작게
        }
    }

    @Override
    public void update() {
        if (isActive) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastUseTime;

            if (elapsedTime >= duration) {
                isActive = false;
                hitbox = null; // hitbox 제거
                return;
            }

            // hitbox 위치 업데이트
            if (hitbox != null) {
                int hitboxX;
                if (facingRight) {
                    hitboxX = owner.getX() + owner.getWidth() - 20;
                } else {
                    hitboxX = owner.getX() - skillWidth + 20;
                }
                int hitboxY = owner.getY() + (owner.getHeight() / 2) - (skillHeight / 2) + yOffset;
                hitbox.setLocation(hitboxX, hitboxY);
            }
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
    

    //이미지 로딩 함수
    @Override
    protected abstract void loadSkillImages();

}