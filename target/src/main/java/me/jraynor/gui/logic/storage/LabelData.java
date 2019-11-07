package me.jraynor.gui.logic.storage;

import lombok.Getter;
import me.jraynor.gui.elements.UIBlock;
import me.jraynor.gui.elements.UIText;
import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.color.UIColor;
import me.jraynor.gui.logic.constraint.Constraints;
import me.jraynor.gui.logic.constraint.UIConstraint;

import java.util.HashMap;
import java.util.Map;

import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint.FACE.LEFT;
import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint.FACE.TOP;

public class LabelData {
    @Getter
    private String labelID;
    @Getter
    private String fontFamily;
    @Getter
    private int fontSize;
    @Getter
    private UIColor foreground;
    @Getter
    private UIColor background;
    @Getter
    private boolean fill;
    private static final Map<String, LabelData> labelDataCache = new HashMap<>();

    private LabelData(String labelID, String fontFamily, int fontSize, UIColor foreground, UIColor background) {
        this.labelID = labelID;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.foreground = foreground;
        this.background = background;
        this.fill = true;
    }

    private LabelData(String labelID, String fontFamily, int fontSize, UIColor foreground) {
        this.labelID = labelID;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.foreground = foreground;
        this.fill = false;
    }

    public static String createLabelData(String labelID, String fontFamily, int fontSize, UIColor foreground, UIColor background) {
        labelDataCache.remove(labelID);
        LabelData labelData = new LabelData(labelID, fontFamily, fontSize, foreground, background);
        labelDataCache.put(labelID, labelData);
        return labelID;
    }

    public static String createLabelData(String labelID, String fontFamily, int fontSize, UIColor foreground) {
        labelDataCache.remove(labelID);
        LabelData labelData = new LabelData(labelID, fontFamily, fontSize, foreground);
        labelDataCache.put(labelID, labelData);
        return labelID;
    }

    public static LabelData getLabelData(String labelID) {
        return labelDataCache.get(labelID);
    }

    public static UIComponent makeLabel(String labelID, String text, UIConstraint constraint) {
        UIConstraint constraint1 = new UIConstraint(constraint);
        if (labelDataCache.containsKey(labelID)) {
            LabelData labelData = labelDataCache.get(labelID);
            if (labelData.fill) {
                UIBlock uiBlock = new UIBlock(labelData.background);
                uiBlock.add(constraint1);
                UIText label = new UIText(text, labelData.getFontFamily(), labelData.getFontSize(), labelData.foreground).setFill(true);

                label.add(new UIConstraint().setXConst(new Constraints.StickyConstraint(LEFT)).setYConst(new Constraints.StickyConstraint(TOP)));
                uiBlock.addLater(label);
                return uiBlock;
            } else {
                UIText label = new UIText(text, labelData.getFontFamily(), labelData.getFontSize(), labelData.foreground).setFill(false);
                label.add(constraint1);
                return label;
            }
        } else
            return null;
    }


}
