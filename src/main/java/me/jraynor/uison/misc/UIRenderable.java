package me.jraynor.uison.misc;

import me.jraynor.uison.logic.color.UIColor;
import me.jraynor.uison.logic.color.UIImage;
import org.lwjgl.nanovg.NVGPaint;

import static org.lwjgl.nanovg.NanoVG.*;

public interface UIRenderable {

    default void drawRect(long vg, float x, float y, float width, float height) {
        drawColoredRect(vg, x, y, width, height, UIColor.BLACK);
    }

    default void drawColoredRect(long vg, float x, float y, float width, float height, UIColor color) {
        nvgBeginPath(vg);
        nvgRect(vg, x, y, width, height);
        nvgFillColor(vg, color.getVgColor());
        nvgFill(vg);
    }

    default void drawColoredRoundedRect(long vg, float x, float y, float width, float height, float round, UIColor color) {
        drawColoredRoundedRect(vg, x, y, width, height, round, round, round, round, color);
    }

    default void drawColoredRoundedRect(long vg, float x, float y, float width, float height, float roundTopLeft, float roundTopRight, float roundBottomLeft, float roundBottomRight, UIColor color) {
        nvgBeginPath(vg);
        nvgRoundedRectVarying(vg, x, y, width, height, roundTopLeft, roundTopRight, roundBottomRight, roundBottomLeft);
        nvgFillColor(vg, color.getVgColor());
        nvgFill(vg);
    }

    default void drawDropShadow(long vg, float hOffset, float vOffset, float x, float y, float width, float height, float round, float blur, float spread, UIColor colorOne, UIColor colorTwo, NVGPaint paint) {
        nvgBoxGradient(vg, x, y + 2, width, height, round * 2, blur, colorOne.getVgColor(), colorTwo.getVgColor(), paint);
        nvgBeginPath(vg);
        nvgRect(vg, x, y, width + hOffset, height + vOffset);
        nvgRoundedRect(vg, x, y, width, height, round);
        nvgPathWinding(vg, NVG_HOLE);
        nvgFillPaint(vg, paint);
        nvgFill(vg);
    }

    default void drawText(long vg, float x, float y, float fontSize, String text, String fontFamily, UIColor color) {
        nvgSave(vg);
        nvgFontSize(vg, fontSize);
        nvgFontFace(vg, fontFamily);
        nvgFillColor(vg, color.getVgColor());
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        nvgText(vg, x, y, text);
        nvgRestore(vg);
    }

    default void drawImage(long vg, float x, float y, float width, float height, UIImage image) {
        image.resize(width, height);
        nvgSave(vg);
        nvgTranslate(vg, x, y);
        nvgBeginPath(vg);
        nvgRect(vg, 0, 0, image.getWidth(), image.getHeight());
        nvgFillPaint(vg, image.getPaint());
        nvgFill(vg);
        nvgRestore(vg);
    }

    default float[] calcTextBounds(long vg, float x, float y, String text, float[] textBounds, float fontSize) {
        nvgSave(vg);
        nvgFontSize(vg, fontSize);
        nvgTextBounds(vg, 0, 0, text, textBounds);
        nvgRestore(vg);
        return textBounds;
    }


    default float[] calcTextBounds(long vg, float x, float y, String text, float[] textBounds) {
        nvgTextBounds(vg, 0, 0, text, textBounds);
        return textBounds;
    }

}
