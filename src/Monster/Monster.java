package Monster;

import Map.MapData;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Monster {
    protected int x, y; // 위치
    protected int width, height; // 크기
    protected Image currentImage; // 현재 이미지
    protected boolean isAlive = true; // 생존 상태
    protected int hp;
    protected int maxHp;
    protected boolean onGround = false;
    protected int verticalSpeed = 0;
    protected static final int GRAVITY = 2;
    protected Rectangle hitbox;



    public enum State {
        IDLE, MOVING, ATTACKING, DEAD, HIT
    }

    protected State currentState = State.IDLE;
    protected Direction currentDirection = Direction.RIGHT;

    public enum Direction {
        LEFT, RIGHT
    }

    public Monster(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.hitbox = new Rectangle(x, y, width, height);
    }

    public void update(MapData currentMap) {
        if (!isAlive) return;

        // 중력 적용
        if (!onGround) {
            verticalSpeed += GRAVITY;
            y += verticalSpeed;
        }

        // 지형 충돌 검사
        onGround = false;
        hitbox.setLocation(x, y);

        for (Rectangle terrain : currentMap.getTerrain()) {
            if (hitbox.intersects(terrain)) {
                // 위에서 아래로 떨어지는 중 충돌
                if (verticalSpeed > 0 && hitbox.y + hitbox.height - verticalSpeed <= terrain.y) {
                    y = terrain.y - height;
                    verticalSpeed = 0;
                    onGround = true;
                    break;
                }
            }
        }

        // 현재 서있는 지형 찾기
        if (onGround) {
            boolean hasGroundAhead = false;
            Rectangle currentTerrain = null;

            // 현재 서있는 지형 찾기
            for (Rectangle terrain : currentMap.getTerrain()) {
                if (y + height == terrain.y &&
                        x + width > terrain.x &&
                        x < terrain.x + terrain.width) {
                    currentTerrain = terrain;
                    break;
                }
            }

            if (currentTerrain != null) {
                // 진행 방향의 지형 끝 감지
                if (currentDirection == Direction.RIGHT) {
                    // 오른쪽으로 이동 중일 때
                    if (x + width + 5 >= currentTerrain.x + currentTerrain.width) {
                        // 지형 끝에 도달하면 방향 전환
                        currentDirection = Direction.LEFT;
                        setState(currentState, Direction.LEFT);
                    }
                } else {
                    // 왼쪽으로 이동 중일 때
                    if (x - 5 <= currentTerrain.x) {
                        // 지형 끝에 도달하면 방향 전환
                        currentDirection = Direction.RIGHT;
                        setState(currentState, Direction.RIGHT);
                    }
                }
            }
        }

        updateState();
    }

    protected abstract void updateState();
    protected abstract Image getMonsterImage(State state, Direction direction);

    public void takeDamage(int damage) {
        hp -= damage;
        setState(State.HIT, currentDirection);
        if (hp <= 0) {
            isAlive = false;
            setState(State.DEAD, currentDirection);
        }
    }

    public void setState(State state, Direction direction) {
        currentState = state;
        currentDirection = direction;
        currentImage = getMonsterImage(state, direction);
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
    public State getCurrentState() { return currentState; }
    public Direction getCurrentDirection() { return currentDirection; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        hitbox.setLocation(x, y);
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}