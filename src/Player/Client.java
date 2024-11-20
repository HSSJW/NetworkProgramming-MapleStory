import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class Client extends JPanel implements ActionListener, KeyListener {

    private Timer timer; // 게임 화면을 주기적으로 갱신할 타이머
    private Image playerImage, opponentImage, missileImage, backgroundImage; // 플레이어, 상대방, 미사일, 배경 이미지
    
    
    public Client(int playerId) throws IOException {

        //사용자 id에 맞게 플레이어 이미지 부여
        if (playerId == 1) {
            playerImage = new ImageIcon("images/spaceship5.png").getImage(); // 1번 플레이어 이미지
            opponentImage = new ImageIcon("images/spaceship3.png").getImage(); // 2번 상대방 이미지
        } else {
            playerImage = new ImageIcon("images/spaceship3.png").getImage(); // 2번 플레이어 이미지
            opponentImage = new ImageIcon("images/spaceship5.png").getImage(); // 1번 상대방 이미지
        }
        
    }

    // 그래픽 요소를 화면에 그리는 메서드
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

    }

    // 액션 이벤트 발생 시 호출되는 메서드 (주기적으로 호출되어 화면 갱신)
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
