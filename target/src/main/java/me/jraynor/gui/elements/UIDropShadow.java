package me.jraynor.gui.elements;

import lombok.Getter;
import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.misc.UIRenderable;
import me.jraynor.gui.logic.color.UIColor;
import org.lwjgl.nanovg.NVGPaint;

public class UIDropShadow extends UIComponent implements UIRenderable {
    @Getter
    private UIColor innerColor, outerColor;
    @Getter
    private float blur, spread, round;
    @Getter
    private NVGPaint vgPaint;
    @Getter
    private float xOffset, yOffset;

    public UIDropShadow(UIColor color, float xOffset, float yOffset, float blur, float spread, float round) {
        this.innerColor = color;
        this.outerColor = color;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.blur = blur;
        this.spread = spread;
        this.round = round;
        this.vgPaint = NVGPaint.create();
    }

    public UIDropShadow(UIColor color1, UIColor color2, float xOffset, float yOffset, float blur, float spread, float round) {
        this.innerColor = color1;
        this.outerColor = color2;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.blur = blur;
        this.spread = spread;
        this.round = round;
        this.vgPaint = NVGPaint.create();
    }
    public UIDropShadow(float xOffset, float yOffset, float blur, float spread, float round) {
        this.innerColor = UIColor.rgba(0,0,0,128);
        this.outerColor = UIColor.rgba(0,0,0,0);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.blur = blur;
        this.spread = spread;
        this.round = round;
        this.vgPaint = NVGPaint.create();
    }
    public UIDropShadow(UIColor color1, float xOffset, float yOffset, float fade, float blur, float spread, float round) {
        this.innerColor = color1;
        this.outerColor = UIColor.rgba(Math.max(0, Math.min(255, Math.round(color1.getColor().x * fade))), Math.max(0, Math.min(255, Math.round(color1.getColor().y * fade))), Math.max(0, Math.min(255, Math.round(color1.getColor().z * fade))), Math.max(0, Math.min(255, Math.round(color1.getColor().w * fade))));
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.blur = blur;
        this.spread = spread;
        this.round = round;
        this.vgPaint = NVGPaint.create();
    }
}
