package Player.Skills.Player1_Skill;

import Player.Skills.AbstractSkill.QSkill;
import Player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player1QSkill extends QSkill {


    public Player1QSkill(Player owner) {
        super(owner);

    }

    @Override
    protected void loadSkillImages() {

        try {



            rightImage = new ImageIcon("images/Player/Player1/skill/player1-q-right.gif").getImage();
            leftImage =  new ImageIcon("images/Player/Player1/skill/player1-q-left.gif").getImage();

            // MediaTracker를 사용하여 이미지 로드 완료 대기
            MediaTracker tracker = new MediaTracker(new Canvas());
            tracker.addImage(rightImage, 0);
            tracker.addImage(leftImage, 1);



            // 이미지 크기 조정
            Image scaledRight = rightImage.getScaledInstance(SKILL_WIDTH, SKILL_HEIGHT, Image.SCALE_SMOOTH);
            Image scaledLeft = leftImage.getScaledInstance(SKILL_WIDTH, SKILL_HEIGHT, Image.SCALE_SMOOTH);



        } catch (Exception e) {
            System.out.println("Error loading skill images: " + e.getMessage());
            e.printStackTrace();

        }
    }


}