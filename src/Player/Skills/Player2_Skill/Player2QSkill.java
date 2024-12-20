package Player.Skills.Player2_Skill;

import Player.Skills.AbstractSkill.QSkill;
import Player.Player;
import javax.swing.*;

// Player2 Q스킬
public class Player2QSkill extends QSkill {
    public Player2QSkill(Player owner) {
        super(owner);

        this.yOffset = -200;
    }

    @Override
    protected void loadSkillImages() {
        try {
            rightImage = new ImageIcon("images/Player/Player2/skill/player2_q-ezgif.com-resize.gif").getImage();
            leftImage = new ImageIcon("images/Player/Player2/skill/player2_q-ezgif.com-resize.gif").getImage();

            // 오른쪽 이미지 기준으로 스킬 크기 설정
            updateSkillDimensions(rightImage);

        } catch (Exception e) {
            System.out.println("Error loading skill images: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
