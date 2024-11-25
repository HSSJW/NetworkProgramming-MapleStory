package Monster;

import java.awt.*;


public abstract class Monster {
    protected int x, y; // 위치
    protected int width, height; // 크기
    protected Image currentImage; // 현재 이미지
    protected boolean isAlive = true; // 생존 상태

    // 상태 정의
    protected enum State {
        IDLE, MOVING, ATTACKING, DEAD, HIT
//      정지 , 이동   ,  공격     ,사망,  피격
    }

    protected State currentState = State.IDLE;

    // 방향 정의
    protected enum Direction {
        LEFT, RIGHT
    }

    protected Direction currentDirection = Direction.RIGHT;

    // 기본 생성자
    public Monster(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    // 상태 및 방향에 따라 이미지를 설정
    protected void setState(State state, Direction direction) {
        currentState = state;
        currentDirection = direction;
        currentImage = getMonsterImage(state, direction); // 하위 클래스에서 구현
    }

    // 상태 및 방향별 이미지를 반환
    protected abstract Image getMonsterImage(State state, Direction direction);

    // 몬스터 상태 업데이트 (추상 메서드)
    public abstract void update();

    // 몬스터 그리기
    public void paintMonster(Graphics g, Component observer) {
        if (currentImage != null) {
            g.drawImage(currentImage, x, y, width, height, observer);
        }
    }
}


