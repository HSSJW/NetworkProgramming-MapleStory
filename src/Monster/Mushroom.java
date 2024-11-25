package Monster;

import javax.swing.*;
import java.awt.*;


public class Mushroom extends Monster {
    private Image idleImageLeft, idleImageRight;
    private Image movingImageLeft, movingImageRight;
    private Image attackImageLeft, attackImageRight;
    private Image deadImageLeft, deadImageRight;
    private Image hitImageLeft, hitImageRight;

    public Mushroom(int startX, int startY) {
        super(startX, startY);
        this.width = 50;
        this.height = 50;

        // 이미지 로드
        idleImageLeft = new ImageIcon("images/mushroom_moving_left.gif").getImage();
        idleImageRight = new ImageIcon("images/mushroom_moving_right.gif").getImage();
        movingImageLeft = new ImageIcon("images/mushroom_moving_left.gif").getImage();
        movingImageRight = new ImageIcon("images/mushroom_moving_right.gif").getImage();

        //주황버섯은 공격 없음
        //attackImageLeft = new ImageIcon("images/mushroom_attacking_left.png").getImage();
        //attackImageRight = new ImageIcon("images/mushroom_attacking_right.png").getImage();
        deadImageLeft = new ImageIcon("images/mushroom_dead_left.png").getImage();
        deadImageRight = new ImageIcon("images/mushroom_dead)right.png").getImage();

        hitImageLeft = new ImageIcon("images/mushroom_hit_left.png").getImage();
        hitImageRight = new ImageIcon("images/mushroom_hit_right.png").getImage();


        // 초기 상태 설정
        setState(State.IDLE, Direction.RIGHT);
    }

    @Override
    protected Image getMonsterImage(State state, Direction direction) {
        switch (state) {
            case IDLE:
                return direction == Direction.LEFT ? idleImageLeft : idleImageRight;
            case MOVING:
                return direction == Direction.LEFT ? movingImageLeft : movingImageRight;
            case ATTACKING:
                return direction == Direction.LEFT ? attackImageLeft : attackImageRight;
            case HIT:
                return direction == Direction.LEFT ? hitImageLeft : hitImageRight;
            case DEAD:
                return direction == Direction.LEFT ? deadImageLeft : deadImageRight;
            default:
                return null;
        }
    }

    @Override
    public void update() {
        if (!isAlive) {
            setState(State.DEAD, currentDirection);
            return;
        }

        // 간단한 이동 로직 (예: 화면 좌우로 이동)
        if (currentState == State.MOVING) {
            if (currentDirection == Direction.RIGHT) {
                x += 2;
                if (x > 400) { // 화면 끝에 도달하면 방향 전환
                    setState(State.MOVING, Direction.LEFT);
                }
            } else {
                x -= 2;
                if (x < 0) { // 화면 왼쪽 끝에 도달하면 방향 전환
                    setState(State.MOVING, Direction.RIGHT);
                }
            }
        }
    }
}

