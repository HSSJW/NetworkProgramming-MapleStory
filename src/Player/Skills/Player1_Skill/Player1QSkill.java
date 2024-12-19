package Player.Skills.Player1_Skill;

import Player.Skills.AbstractSkill.QSkill;
import Player.Player;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player1QSkill extends QSkill {


    public Player1QSkill(Player owner) {
        super(owner);
        System.out.println("Player1QSkill constructor called");
    }

    @Override
    protected void loadSkillImages() {
        System.out.println("Loading skill images for Player1QSkill");
        try {



            rightImage = new ImageIcon("images/Player/Player1/skill/player1_q_right.gif").getImage();
            leftImage =  new ImageIcon("images/Player/Player1/skill/player1_q_left.gif").getImage();

            // MediaTracker를 사용하여 이미지 로드 완료 대기
            MediaTracker tracker = new MediaTracker(new Canvas());
            tracker.addImage(rightImage, 0);
            tracker.addImage(leftImage, 1);



            // 이미지 크기 조정
            Image scaledRight = rightImage.getScaledInstance(SKILL_WIDTH, SKILL_HEIGHT, Image.SCALE_SMOOTH);
            Image scaledLeft = leftImage.getScaledInstance(SKILL_WIDTH, SKILL_HEIGHT, Image.SCALE_SMOOTH);


//            gifRight = new ImageIcon(scaledRight);
//            gifLeft = new ImageIcon(scaledLeft);

        } catch (Exception e) {
            System.out.println("Error loading skill images: " + e.getMessage());
            e.printStackTrace();
            createDebugImages();
        }
    }

    private void createDebugImages() {
        System.out.println("Creating debug images");
        BufferedImage debugImage = new BufferedImage(SKILL_WIDTH, SKILL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = debugImage.createGraphics();

        // 더 눈에 띄는 디버그 이미지
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 그라데이션 배경
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(255, 0, 0, 180),
                SKILL_WIDTH, SKILL_HEIGHT, new Color(0, 0, 255, 180)
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, SKILL_WIDTH, SKILL_HEIGHT);

        // 흰색 테두리
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawRect(1, 1, SKILL_WIDTH-3, SKILL_HEIGHT-3);

        // 텍스트
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        String text = "Q SKILL";
        FontMetrics metrics = g.getFontMetrics();
        int x = (SKILL_WIDTH - metrics.stringWidth(text)) / 2;
        int y = ((SKILL_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(text, x, y);

        g.dispose();

        gifRight = new ImageIcon(debugImage);
        gifLeft = new ImageIcon(debugImage);
        System.out.println("Debug images created with size: " + SKILL_WIDTH + "x" + SKILL_HEIGHT);
    }
}