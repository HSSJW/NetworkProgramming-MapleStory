package Player.Player1;

import Player.Player;

import javax.swing.*;

public class Player1 extends Player {

    public Player1(int startX, int startY) {
        super(1, startX, startY);
    }

    @Override
    protected void initializeImages() {
        standRightImage = new ImageIcon("images/player1/player_stand_right.png").getImage();
        standLeftImage = new ImageIcon("images/player1/player_stand_left.png").getImage();
        leftImage = new ImageIcon("images/player1/player_walk_left.gif").getImage();
        rightImage = new ImageIcon("images/player1/player_walk_right.gif").getImage();
        jumpRightImage = new ImageIcon("images/player1/player_jump_right.png").getImage();
        jumpLeftImage = new ImageIcon("images/player1/player_jump_left.png").getImage();
        hitRightImage = new ImageIcon("images/player1/player_hit_right.png").getImage();
        hitLeftImage = new ImageIcon("images/player1/player_hit_left.png").getImage();
    }
}
