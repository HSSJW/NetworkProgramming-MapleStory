package Player;

import Map.MapData;

import javax.swing.*;
import java.awt.*;

public class Player {
    private int id; // 플레이어 ID (1 또는 2)
    private int x, y; // 플레이어 위치
    private int width, height; // 플레이어 크기
    private int verticalSpeed = 0; // 수직 속도
    private final int GRAVITY = 2; // 중력
    private final int JUMP_STRENGTH = -20; // 점프 힘
    private boolean onGround = false; // 바닥 여부
    private boolean facingRight = true; // 방향 (true: 오른쪽, false: 왼쪽)
    private boolean moving = false; // 이동 중 여부
    private String currentState = "idle"; // 현재 상태 ("idle", "left", "right", "jump", "hit")

    // 상태별 이미지
    private Image standRightImage, standLeftImage;
    private Image leftImage, rightImage;
    private Image jumpRightImage, jumpLeftImage;
    private Image hitRightImage, hitLeftImage;
    private Image currentImage; // 현재 상태의 이미지

    public Player(int playerId, int startX, int startY) {
        // 사용자 id에 맞게 플레이어 이미지 부여
        if (playerId == 1) {
            standRightImage = new ImageIcon("images/player/player_stand_right.png").getImage();
            standLeftImage = new ImageIcon("images/player/player_stand_left.png").getImage();
            leftImage = new ImageIcon("images/player/player_walk_left.gif").getImage();
            rightImage = new ImageIcon("images/player/player_walk_right.gif").getImage();
            jumpRightImage = new ImageIcon("images/player/player_jump_right.png").getImage();
            jumpLeftImage = new ImageIcon("images/player/player_jump_left.png").getImage();
            hitRightImage = new ImageIcon("images/player/player_hit_right.png").getImage();
            hitLeftImage = new ImageIcon("images/player/player_hit_left.png").getImage();
        } else {
            standRightImage = new ImageIcon("images/player/player_stand_right.png").getImage();
            standLeftImage = new ImageIcon("images/player/player_stand_left.png").getImage();
            leftImage = new ImageIcon("images/player/player_walk_left.gif").getImage();
            rightImage = new ImageIcon("images/player/player_walk_right.gif").getImage();
            jumpRightImage = new ImageIcon("images/player/player_jump_right.png").getImage();
            jumpLeftImage = new ImageIcon("images/player/player_jump_left.png").getImage();
            hitRightImage = new ImageIcon("images/player/player_hit_right.png").getImage();
            hitLeftImage = new ImageIcon("images/player/player_hit_left.png").getImage();

        }

        // 기본 이미지를 오른쪽 서 있는 이미지로 설정
        currentImage = standRightImage;

        if (playerId != 1 && playerId != 2) {
            throw new IllegalArgumentException("Invalid playerId. Must be 1 or 2.");
        }
        this.id = playerId;
        this.x = startX;
        this.y = startY;
        this.width = currentImage.getWidth(null);
        this.height = currentImage.getHeight(null);
    }

    // 플레이어 그리기
    public void draw(Graphics g, Component observer) {
        g.drawImage(currentImage, x, y, width, height, observer);
    }

    // 플레이어 이동
    public void moveLeft(int speed) {
        x -= speed;
        facingRight = false; // 왼쪽 방향 설정
        moving = true; // 이동 중
        if (!currentState.equals("jump")) { // 점프 중이 아니면 이동 이미지
            currentState = "left";
            currentImage = leftImage;
        }
    }

    public void moveRight(int speed) {
        x += speed;
        facingRight = true; // 오른쪽 방향 설정
        moving = true; // 이동 중
        if (!currentState.equals("jump")) { // 점프 중이 아니면 이동 이미지
            currentState = "right";
            currentImage = rightImage;
        }
    }

    public void jump() {
        if (onGround) { // 바닥에서만 점프 가능
            verticalSpeed = JUMP_STRENGTH; // 점프 힘 설정
            onGround = false; // 점프 상태로 변경
            currentState = "jump"; // 상태 변경
            currentImage = facingRight ? jumpRightImage : jumpLeftImage; // 방향에 따른 점프 이미지
        }
    }

    public void takeHit() {
        currentState = "hit"; // 상태 변경
        currentImage = facingRight ? hitRightImage : hitLeftImage; // 방향에 따른 피격 이미지
    }

    public void stopMoving() {
//        if (onGround && !moving && !currentState.equals("jump")) { // 점프 중이 아니면 idle 상태로 전환
            currentState = "idle";
            currentImage = facingRight ? standRightImage : standLeftImage; // 방향에 따른 서 있는 이미지
//        }
    }

    // 플레이어 상태 업데이트
    public void update(MapData currentMap, int mapWidth, int mapHeight) {
        // 현재 맵의 지형 가져오기
        Rectangle[] ground = currentMap.getTerrain().toArray(new Rectangle[0]);

        // 중력 항상 적용
        verticalSpeed += GRAVITY;

        // 위치 업데이트
        y += verticalSpeed;

        // 바닥 충돌 감지
        boolean wasOnGround = onGround; // 이전에 바닥에 있었는지 기록
        onGround = false; // 기본적으로 공중에 있다고 가정

        // 가장 가까운 지형을 추적
        Rectangle closestGround = null;

        for (Rectangle rect : ground) {



            // 여백 보정을 위한 오프셋 추가
            int bottomOffset = 0; // 이미지 아래쪽 판정 확장값

            if (x + width - 20 > rect.x && x + 20 < rect.x + rect.width && // 이미지 좌우 여백 보정
                    y + height + bottomOffset >= rect.y && y + height - verticalSpeed + bottomOffset <= rect.y) {
                // 가장 가까운 지형 선택
                if (closestGround == null || rect.y < closestGround.y) {
                    closestGround = rect; // 더 위에 있는 지형 선택
                }
            }

        }

        // 가장 가까운 지형과 충돌 처리
        if (closestGround != null) {
            onGround = true;
            y = closestGround.y - height; // 지형 위에 위치 고정
            verticalSpeed = 0;            // 수직 속도 초기화

            // 점프 상태에서 착지 상태로 전환
            if (currentState.equals("jump")) {
                stopMoving(); // 점프가 끝난 경우 idle 상태로 전환
            }
        }

        // 착지 시 idle 상태로 전환
        if (onGround && !wasOnGround && !moving) {
            stopMoving();
        }

        // 맵 경계 내로 제한
        x = Math.max(0, Math.min(mapWidth - width, x));
        y = Math.min(mapHeight - height, y);

        // 이동 상태가 없으면 idle로 복구
        if (!moving && onGround) {
            stopMoving();
        }

        // 이동 상태 초기화 (프레임마다)
        moving = false;
    }




    // Getters and Setters
    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setState(String state) {
        currentState = state;
        switch (state) {
            case "left":
                currentImage = leftImage;
                break;
            case "right":
                currentImage = rightImage;
                break;
            case "jump":
                currentImage = facingRight ? jumpRightImage : jumpLeftImage;
                break;
            case "hit":
                currentImage = facingRight ? hitRightImage : hitLeftImage;
                break;
            default: // "idle"
                currentImage = facingRight ? standRightImage : standLeftImage;
                break;
        }
    }
    public String getCurrentState() {
        return currentState;
    }


    public boolean isOnGround() {
        return onGround;
    }

    public void setImage(Image image) {
        this.currentImage = image;
    }
}
