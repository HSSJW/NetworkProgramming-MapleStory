package Monster;

import Map.MapData;
import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;


public abstract class Monster {
    protected int x, y;
    protected int width, height;
    protected Image currentImage;
    protected boolean isAlive = true;
    protected int hp;
    protected int maxHp;
    protected boolean onGround = false;
    protected int verticalSpeed = 0;
    protected static final int GRAVITY = 2;
    protected Rectangle hitbox;
    private int mapIndex;  // 몬스터가 속한 맵의 인덱스


    // enum 대신 String으로 상태 관리
    protected String currentState = "idle";
    protected boolean facingRight = true;
    protected boolean moving = false;

    // 이미지 필드 추가
    protected Image idleRightImage, idleLeftImage;
    protected Image moveRightImage, moveLeftImage;
    protected Image hitRightImage, hitLeftImage;
    protected Image deadRightImage, deadLeftImage;

    public Monster(int startX, int startY, int mapIndex) {
        this.x = startX;
        this.y = startY;
        this.mapIndex = mapIndex;
        this.hitbox = new Rectangle(x, y, width, height);
        initializeImages();
    }

    public int getMapIndex() {
        return mapIndex;
    }

    protected abstract void initializeImages();


    public void update(MapData currentMap) {
        if (!isAlive) return;


        // 중력 적용
        if (!onGround) {
            if (verticalSpeed <= 25) {
                verticalSpeed += GRAVITY;
            }
        }

        // 이전 위치 저장
        int prevY = y;

        // 위치 업데이트
        y += verticalSpeed;

        // 이전 바닥 상태 저장
        boolean wasOnGround = onGround;
        onGround = false;

        // 몬스터가 속한 맵의 지형 가져오기
        CopyOnWriteArrayList<Rectangle> mapTerrain = currentMap.getTerrain();
        Rectangle currentPlatform = null;
        double closestDistance = Double.MAX_VALUE;

        // 해당 맵의 지형과 충돌 체크
        for (Rectangle rect : mapTerrain) {
            // 몬스터가 지형 위에 있고 좌우로 겹치는지 확인
            if (x + width - 20 > rect.x && x + 20 < rect.x + rect.width) {
                // 몬스터의 발 위치와 지형의 높이 차이 계산
                int heightDifference = (y + height) - rect.y;

                // 적절한 높이 범위 내에 있는 경우에만 처리
                if (heightDifference >= 0 && heightDifference <= 30) {
                    double distance = Math.abs(heightDifference);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        currentPlatform = rect;
                    }
                }
            }
        }

        // 지형과의 충돌 처리
        if (currentPlatform != null) {
            onGround = true;
            y = currentPlatform.y - height;
            verticalSpeed = 0;

            // 지형 위에서의 좌우 이동
            int moveSpeed = 2;
            if (facingRight) {
                // 오른쪽 끝 도달 확인
                if (x + width + moveSpeed >= currentPlatform.x + currentPlatform.width - 20) {
                    facingRight = false;
                    setState("move");
                } else {
                    x += moveSpeed;
                }
            } else {
                // 왼쪽 끝 도달 확인
                if (x - moveSpeed <= currentPlatform.x + 20) {
                    facingRight = true;
                    setState("move");
                } else {
                    x -= moveSpeed;
                }
            }
            moving = true;
        } else {
            moving = false;
        }

        // 상태 업데이트
        setState(moving ? "move" : "idle");

        // 히트박스 업데이트
        hitbox.setLocation(x, y);
    }

    public void setState(String state) {
        currentState = state;
        updateImage();
    }

    protected void updateImage() {
        switch (currentState) {
            case "idle":
                currentImage = facingRight ? idleRightImage : idleLeftImage;
                break;
            case "move":
                currentImage = facingRight ? moveRightImage : moveLeftImage;
                break;
            case "hit":
                currentImage = facingRight ? hitRightImage : hitLeftImage;
                break;
            case "dead":
                currentImage = facingRight ? deadRightImage : deadLeftImage;
                break;
        }
    }

    public void takeDamage(int damage) {
        hp -= damage;
        setState("hit");
        if (hp <= 0) {
            isAlive = false;
            setState("dead");
        }
    }

    public void paintMonster(Graphics g, Component observer) {
        if (currentImage != null) {
            g.drawImage(currentImage, x, y, width, height, observer);

            // HP 바 그리기
            if (isAlive && hp < maxHp) {
                int hpBarWidth = width;
                int hpBarHeight = 5;
                int hpBarY = y - hpBarHeight - 2;

                // HP 바 배경
                g.setColor(Color.RED);
                g.fillRect(x, hpBarY, hpBarWidth, hpBarHeight);

                // 현재 HP
                g.setColor(Color.GREEN);
                int currentHpWidth = (int)((double)hp / maxHp * hpBarWidth);
                g.fillRect(x, hpBarY, currentHpWidth, hpBarHeight);
            }
        }
    }

    // Getters and Setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getHp() { return hp; }
    public boolean isAlive() { return isAlive; }
    public String getCurrentState() { return currentState; }
    public boolean isFacingRight() { return facingRight; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        hitbox.setLocation(x, y);
    }
    public void setHp(int hp) {
        this.hp = hp;
        if (this.hp <= 0) {
            this.hp = 0;
            isAlive = false;
            setState("dead");
        }
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
        updateImage(); // 방향이 바뀌면 이미지도 업데이트
    }

    public void setAlive(boolean alive) {
        this.isAlive = alive;
        if (!alive) {
            setState("dead");
        }
    }
}