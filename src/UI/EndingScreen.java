package UI;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class EndingScreen {
    private final Image endingImage;
    private final int referenceWidth;
    private final int referenceHeight;
    private long endingStartTime;
    private boolean isActive = false;

    public EndingScreen(int referenceWidth, int referenceHeight) {
        this.referenceWidth = referenceWidth;
        this.referenceHeight = referenceHeight;
        // 원본 이미지만 로드
        this.endingImage = new ImageIcon("images/ui/Ending.png").getImage();
    }

    public void activate() {
        isActive = true;
        endingStartTime = System.currentTimeMillis();
    }

    public void deactivate() {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public void draw(Graphics2D g2d, double scaleX, double scaleY) {
        AffineTransform originalTransform = g2d.getTransform();
        g2d.scale(scaleX, scaleY);

        // 반투명 검은 배경
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, referenceWidth, referenceHeight);

        // 현재 화면 크기에 맞게 이미지 크기 계산
        double imageWidth = referenceWidth * 0.8;  // 화면 너비의 80%
        double imageHeight = referenceHeight * 0.6; // 화면 높이의 60%

        // 이미지 중앙 정렬
        int imageX = (int)((referenceWidth - imageWidth) / 2);
        int imageY = (int)((referenceHeight - imageHeight) / 3);

        // 크기가 조정된 이미지 그리기
        g2d.drawImage(endingImage, imageX, imageY,
                (int)imageWidth, (int)imageHeight, null);

        // 텍스트 설정
        drawEndingText(g2d, (int)(imageY + imageHeight));

        g2d.setTransform(originalTransform);
    }

    private void drawEndingText(Graphics2D g2d, int baseY) {
        // 안티앨리어싱 적용
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Game Clear 메시지
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics fm = g2d.getFontMetrics();
        String clearMessage = "Game Clear!";
        int textX = (referenceWidth - fm.stringWidth(clearMessage)) / 2;
        int textY = baseY + 80;

        // 그림자 효과
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(clearMessage, textX + 2, textY + 2);
        g2d.setColor(Color.WHITE);
        g2d.drawString(clearMessage, textX, textY);

        // 재시작 안내 메시지
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        fm = g2d.getFontMetrics();
        String restartMessage = "Press 'ESC' to EXIT";
        textX = (referenceWidth - fm.stringWidth(restartMessage)) / 2;
        textY += 50;

        // 그림자 효과
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(restartMessage, textX + 2, textY + 2);
        g2d.setColor(Color.WHITE);
        g2d.drawString(restartMessage, textX, textY);
    }
}