package me.jraynor.pong.render;

import lombok.Getter;
import me.jraynor.gui.UIMaster;
import me.jraynor.gui.elements.UIBlock;
import me.jraynor.gui.elements.UIFlex;
import me.jraynor.gui.elements.UISeparator;
import me.jraynor.gui.elements.UIVBox;
import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.color.UIColor;
import me.jraynor.gui.logic.constraint.UIConstraint;
import me.jraynor.gui.logic.storage.LabelData;
import me.jraynor.misc.Input;
import me.jraynor.pong.Pong;
import me.jraynor.pong.entities.Entity;
import me.jraynor.pong.entities.EntityGoal;

import static me.jraynor.gui.logic.constraint.Constraints.*;
import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint.FACE.*;

public class PongUI extends UIBlock {
    private UIComponent cameraInfoTitle, paddleInfoTitle;
    @Getter
    private UIComponent xPos, yPos, zPos;

    @Getter
    private UIComponent deltaXOne, deltaXTwo;

    private UIBlock gameOver;
    @Getter
    private UIComponent finalScore;

    public PongUI() {
        super(UIColor.DARK_GRAY, new UIConstraint()
                .setXConst(new StickyConstraint(LEFT))
                .setYConst(new StickyConstraint(TOP))
                .setWConst(new RelativeConstraint(0.33f, new PixelConstraint(100), new PixelConstraint(350)))
                .setHConst(new PixelConstraint(150)));
    }

    @Override
    protected void onAdded() {
        super.onAdded();
        makeLabelData();
        createInfo();
        createGameOver();
    }

    private void makeLabelData() {
        LabelData.createLabelData("title", "regular", 32, UIColor.rgb(58), UIColor.LIGHT_RED);
        LabelData.createLabelData("info", "regular", 20, UIColor.BLACK, UIColor.LIGHT_PURPLE);
        LabelData.createLabelData("sub", "light", 16, UIColor.LIGHT_RED);

    }

    private void createInfo() {
        UIFlex cameraWindow = createCameraInfo();

    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        double mouseX = Input.mousePosition.x;
        double mouseY = Input.mousePosition.y;
        UIConstraint constraint = (UIConstraint) replay.getComponent(UIConstraint.class);
        if (mouseX >= constraint.x && mouseX <= constraint.x + constraint.w && mouseY >= constraint.y && mouseY <= constraint.y + constraint.h && Input.mousePressed(0)) {
            xPos.setText("red: 0/7");
            yPos.setText("blue: 0/7");
            Class[] classes = Pong.pongManager.loadedEntityTypes;
            this.gameOver.setActive(false);
            for (Class clss : classes) {
                for (Entity entity : Pong.pongManager.entityLists.get(clss))
                    if (entity instanceof EntityGoal)
                        ((EntityGoal) entity).setPoints(0);
            }

        }

    }

    private UISeparator addSeparator(UIComponent relative, UIColor color) {
        UISeparator separator = new UISeparator(color);
        separator.add(new UIConstraint()
                .setYConst(new StickyConstraint(BOTTOM, new PixelConstraint(-3)).setRelativeConstraint(relative))
                .setWConst(new RelativeConstraint(1, new PixelConstraint(-20)))
                .setXConst(new StickyConstraint(LEFT).setRelativeConstraint(relative))
                .setHConst(new PixelConstraint(1)));
        return separator;
    }


    private UISeparator addSeparator(UIColor color, float padding) {
        UISeparator separator = new UISeparator(color);
        separator.add(new UIConstraint()
                .setYConst(new StickyConstraint(BOTTOM))
                .setWConst(new RelativeConstraint(1, new PixelConstraint(-1 * (padding * 2))))
                .setXConst(new StickyConstraint(LEFT, new PixelConstraint(padding)))
                .setHConst(new PixelConstraint(1)));
        return separator;
    }

    private UIConstraint replayConstraint = new UIConstraint()
            .setYConst(new StickyConstraint(TOP, new PixelConstraint(50)))
            .setXConst(new CenterConstraint());
    private UIComponent replay;

    private void createGameOver() {
        gameOver = new UIBlock(UIColor.DARK_GRAY);
        gameOver.add(new UIConstraint().setYConst(new CenterConstraint()).setXConst(new CenterConstraint()).setWConst(new RelativeConstraint(0.20f)).setHConst(new RelativeConstraint(0.08f)));

        UIConstraint constraint = new UIConstraint()
                .setYConst(new StickyConstraint(TOP, new PixelConstraint(10)).setRelativeConstraint(gameOver))
                .setXConst(new CenterConstraint());

        finalScore = LabelData.makeLabel("title", "Good try! you're score was x/7", constraint);

        replay = LabelData.makeLabel("info", "Game over, play again?", replayConstraint);
        UIMaster.getRoot().add(gameOver);
        gameOver.add(finalScore);
        gameOver.add(replay);
        gameOver.setActive(false);
    }


    private UIFlex createCameraInfo() {

        cameraInfoTitle = LabelData.makeLabel("title", "Player Scores",
                new UIConstraint()
                        .setXConst(new StickyConstraint(LEFT, new PixelConstraint(10)))
                        .setYConst(new StickyConstraint(TOP, new PixelConstraint(5)))
                        .setHConst(new PixelConstraint(15)));

        add(cameraInfoTitle);

        UIFlex infoBox = new UIVBox(10).fill(UIColor.DARK_PURPLE);
        infoBox.add(new UIConstraint()
                .setYConst(new StickyConstraint(BOTTOM, new PixelConstraint(-5f)).setRelativeConstraint(cameraInfoTitle))
                .setXConst(new CenterConstraint())
                .setWConst(new RelativeConstraint(1, new PixelConstraint(-20)))
                .setHConst(new PixelConstraint(0)));
        infoBox.setFlexWidth(false);
        infoBox.setFlexHeight(true);
        add(infoBox);

        UIConstraint infoConstraint = new UIConstraint()
                .setXConst(new StickyConstraint(LEFT, new RelativeConstraint(0.1f)))
                .setYConst(new StickyConstraint(TOP, new PixelConstraint(10)));

        xPos = LabelData.makeLabel("info", "red: 0/7", infoConstraint);
        infoBox.add(xPos);
        infoBox.add(addSeparator(UIColor.DARK_GRAY, 30));
        yPos = LabelData.makeLabel("info", "blue: 0/7", infoConstraint);
        infoBox.add(yPos);
        return infoBox;
    }

    public void reset(String score1) {
        this.gameOver.setActive(true);
        this.finalScore.setText(score1);
    }
}




