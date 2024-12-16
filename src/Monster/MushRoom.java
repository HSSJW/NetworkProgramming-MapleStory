package Monster;

import javax.swing.*;
import java.awt.*;

public class GreenSlime extends Monster {
    private static final Image IDLE_RIGHT = new ImageIcon("images/monster/mushroom/mushroom_moving_right.gif").getImage();
    private static final Image IDLE_LEFT = new ImageIcon("images/monster/mushroom/mushroom_moving_right.gif").getImage();
    private static final Image MOVE_RIGHT = new ImageIcon("images/monster/mushroom/mushroom_moving_right.gif").getImage();
    private static final Image MOVE_LEFT = new ImageIcon("images/monster/mushroom/mushroom_moving_right.gif").getImage();
    private static final Image HIT_RIGHT = new ImageIcon("images/monster/mushroom/mushroom_moving_right.gif").getImage();
    private static final Image HIT_LEFT = new ImageIcon("images/monster/mushroom/mushroom_moving_right.gif").getImage();
    private static final Image DEAD_RIGHT = new ImageIcon("images/monster/mushroom/mushroom_moving_right.gif").getImage();
    private static final Image DEAD_LEFT = new ImageIcon("images/monster/mushroom/mushroom_moving_right.gif").getImage();

    private static final int MOVE_SPEED = 2;
    private static final int PATROL_RANGE = 200;
    private int initialX;
    private int moveTimer = 0;
    private static final int MOVE_DURATION = 100;
    private static final int IDLE_DURATION = 50;

    @Override
    public void update() {

    }

    public GreenSlime(int startX, int startY) {
        super(startX, startY);
        this.width = 50;
        this.height = 50;
        this.hp = 100;
        this.maxHp = 100;
        this.initialX = startX;
        this.hitbox = new Rectangle(x, y, width, height);
    }

    @Override
    protected Image getMonsterImage(State state, Direction direction) {
        switch (state) {
            case IDLE:
                return direction == Direction.RIGHT ? IDLE_RIGHT : IDLE_LEFT;
            case MOVING:
                return direction == Direction.RIGHT ? MOVE_RIGHT : MOVE_LEFT;
            case HIT:
                return direction == Direction.RIGHT ? HIT_RIGHT : HIT_LEFT;
            case DEAD:
                return direction == Direction.RIGHT ? DEAD_RIGHT : DEAD_LEFT;
            default:
                return IDLE_RIGHT;
        }
    }

    @Override
    protected void updateState() {
        if (!isAlive) return;

        if (currentState == State.HIT) {
            moveTimer++;
            if (moveTimer >= 20) { // 피격 상태 지속 시간
                moveTimer = 0;
                setState(State.IDLE, currentDirection);
            }
            return;
        }

        moveTimer++;

        switch (currentState) {
            case IDLE:
                if (moveTimer >= IDLE_DURATION) {
                    moveTimer = 0;
                    setState(State.MOVING, currentDirection);
                }
                break;

            case MOVING:
                if (moveTimer >= MOVE_DURATION) {
                    moveTimer = 0;
                    setState(State.IDLE, currentDirection);
                } else {
                    // 이동 처리
                    if (currentDirection == Direction.RIGHT) {
                        if (x < initialX + PATROL_RANGE) {
                            x += MOVE_SPEED;
                        } else {
                            currentDirection = Direction.LEFT;
                            setState(State.MOVING, Direction.LEFT);
                        }
                    } else {
                        if (x > initialX - PATROL_RANGE) {
                            x -= MOVE_SPEED;
                        } else {
                            currentDirection = Direction.RIGHT;
                            setState(State.MOVING, Direction.RIGHT);
                        }
                    }
                    hitbox.setLocation(x, y);
                }
                break;
        }
    }
}