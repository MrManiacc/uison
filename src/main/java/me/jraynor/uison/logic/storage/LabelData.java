package me.jraynor.uison.logic.storage;

import lombok.Getter;
import me.jraynor.uison.logic.color.UIColor;

import java.util.HashMap;
import java.util.Map;

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



}
