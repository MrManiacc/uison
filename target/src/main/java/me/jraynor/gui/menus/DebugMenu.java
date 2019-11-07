package me.jraynor.gui.menus;

import me.jraynor.gui.UIMaster;
import me.jraynor.gui.elements.*;
import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.color.UIColor;
import me.jraynor.gui.logic.constraint.UIConstraint;
import me.jraynor.gui.logic.constraint.UIScroll;

import static me.jraynor.gui.logic.constraint.Constraints.*;
import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint.FACE.*;


public class DebugMenu extends UIComponent {
    private UIConstraint menuConstraint, titleConstraint, horBoxConstraint, vertBoxConstraint, infoLabelConstraint;
    private UIFlex menuBlock;
    private UIBlock titleBlock;
    private UIFlex horzBox;
    private UIFlex vertBox;

    @Override
    protected void onAdded() {
        createConstraints();
        createElements();
    }

    private void createConstraints() {
        menuConstraint = new UIConstraint()
                .setXConst(new StickyConstraint(LEFT, new PixelConstraint(10)))
                .setYConst(new StickyConstraint(TOP, new PixelConstraint(10)))
                .setWConst(new RelativeConstraint(0.25f))
                .setHConst(new RelativeConstraint(0.8f));

        titleConstraint = new UIConstraint()
                .setXConst(new CenterConstraint())
                .setYConst(new StickyConstraint(TOP, new PixelConstraint(10)))
                .setWConst(new RelativeConstraint(0.33f, null, new PixelConstraint(250)))
                .setHConst(new PixelConstraint(25));
        horBoxConstraint = new UIConstraint()
                .setXConst(new CenterConstraint())
                .setYConst(new StickyConstraint(BOTTOM, new PixelConstraint(-10)).setRelativeConstraint(titleConstraint))
                .setWConst(new RelativeConstraint(1))
                .setHConst(new PixelConstraint(200));
//                .setHConst(new FlexConstraint(10));
        vertBoxConstraint = new UIConstraint()
                .setXConst(new StickyConstraint(LEFT, new PixelConstraint(10)))
                .setYConst(new StickyConstraint(TOP, new PixelConstraint(10)))
                .setWConst(new RelativeConstraint(1))
                .setHConst(new RelativeConstraint(0.8f));
        infoLabelConstraint = new UIConstraint()
                .setXConst(new StickyConstraint(LEFT, new RelativeConstraint(0)))
                .setYConst(new StickyConstraint(TOP, new PixelConstraint(10)))
                .setWConst(new RelativeConstraint(0.33f, null, new PixelConstraint(350)))
                .setHConst(new PixelConstraint(15));
    }

    private void createElements() {
        menuBlock = new UIFlex(20).fill(UIColor.DARK_RED);
        menuBlock.add(menuConstraint);

        UIScroll scroll = new UIScroll();
        scroll.setBreakHeight(new RelativeConstraint(0.8f));

        menuBlock.add(scroll);

        UIMaster.getRoot().add(menuBlock);
        createTitle();
        createInfoElements();
    }

    private void createTitle() {
        titleBlock = createLabel(menuBlock, titleConstraint, "RaynEngine 0.0.1", 48, UIColor.BLACK, UIColor.WHITE);
        titleBlock.getConstraints().setXConst(new StickyConstraint(LEFT).setRelativeConstraint(horBoxConstraint));
    }

    private void createInfoElements() {
        UIFlex centeredBlock = new UIFlex(10).fill(UIColor.LIGHT_PURPLE);
        centeredBlock.add(
                new UIConstraint()
                        .setYConst(new StickyConstraint(BOTTOM, new PixelConstraint(-10)).setRelativeConstraint(horBoxConstraint))
//                        .setYConst(new StickyConstraint(TOP))
                        .setXConst(new StickyConstraint(LEFT).setRelativeConstraint(horBoxConstraint))
                        .setWConst(new RelativeConstraint(1f))
                        .setHConst(new RelativeConstraint(1)));
        menuBlock.add(centeredBlock);

        horzBox = new UIHBox(10).fill(UIColor.LIGHT_RED);
        horzBox.add(horBoxConstraint);
        menuBlock.add(horzBox);


        vertBox = new UIVBox(10).fill(UIColor.LIGHT_RED);
        vertBox.add(vertBoxConstraint);
        centeredBlock.add(vertBox);

        UIFlex vertBox2 = new UIVBox(10).fill(UIColor.LIGHT_RED);
        UIConstraint vertBox2Constraint = new UIConstraint(vertBoxConstraint);
        vertBox2Constraint.setXConst(new StickyConstraint(RIGHT, new PixelConstraint(-10)).setRelativeConstraint(vertBox));
        vertBox2.add(vertBox2Constraint);
        centeredBlock.add(vertBox2);


        UIComponent vertBox3 = new UIVBox(10).fill(UIColor.LIGHT_RED);
        UIConstraint vertBox3Constraint = new UIConstraint(vertBoxConstraint);
        vertBox3Constraint.setXConst(new StickyConstraint(RIGHT, new PixelConstraint(-10)).setRelativeConstraint(vertBox2));
        vertBox3.add(vertBox3Constraint);
        centeredBlock.add(vertBox3);


        createLabel(horzBox, infoLabelConstraint, "Testing", 20, UIColor.WHITE, UIColor.BLACK);
        createLabel(horzBox, new UIConstraint(infoLabelConstraint), "Testing2", 20, UIColor.WHITE, UIColor.BLACK);
        createLabel(horzBox, new UIConstraint(infoLabelConstraint), "Testing3", 20, UIColor.WHITE, UIColor.BLACK);
        createLabel(horzBox, new UIConstraint(infoLabelConstraint), "Testing2", 20, UIColor.WHITE, UIColor.BLACK);
        createLabel(horzBox, new UIConstraint(infoLabelConstraint), "Testing3", 20, UIColor.WHITE, UIColor.BLACK);

        for (int i = 0; i < 15; i++)
            createLabel(vertBox, new UIConstraint(infoLabelConstraint), "Label " + i, 20,
                    i % 2 == 0 ? UIColor.BLACK : UIColor.WHITE, i % 2 == 0 ? UIColor.WHITE : UIColor.BLACK);
        for (int i = 0; i < 20; i++)
            createLabel(vertBox2, new UIConstraint(infoLabelConstraint), "Label " + i, 20,
                    i % 2 == 0 ? UIColor.BLACK : UIColor.WHITE, i % 2 == 0 ? UIColor.WHITE : UIColor.BLACK);
        for (int i = 0; i < 10; i++)
            createLabel(vertBox3, new UIConstraint(infoLabelConstraint), "Label " + i, 20,
                    i % 2 == 0 ? UIColor.BLACK : UIColor.WHITE, i % 2 == 0 ? UIColor.WHITE : UIColor.BLACK);
    }

    private UIBlock createLabel(UIComponent parent, UIConstraint constraint, String text, int fontSize, UIColor bgColor, UIColor textColor) {
        UIBlock uiBlock = new UIBlock(bgColor);
        uiBlock.add(constraint);
        UIText label = new UIText(text, "regular", fontSize, textColor).setFill(true);
        parent.add(uiBlock);
        uiBlock.add(label);
        return uiBlock;
    }
}
