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
            rightImage = new ImageIcon("images/Player/Player1/skill/player1-q-right-ezgif.com-resize.gif").getImage();
            leftImage = new ImageIcon("images/Player/Player1/skill/player1-q-left-ezgif.com-resize.gif").getImage();

            // 오른쪽 이미지 기준으로 스킬 크기 설정
            updateSkillDimensions(rightImage);

        } catch (Exception e) {
            System.out.println("Error loading skill images: " + e.getMessage());
            e.printStackTrace();
        }
    }


}