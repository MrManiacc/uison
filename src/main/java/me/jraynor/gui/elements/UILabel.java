package me.jraynor.gui.elements;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.color.UIColor;
import me.jraynor.gui.logic.constraint.Constraints;
import me.jraynor.gui.logic.constraint.UIConstraint;
import me.jraynor.gui.misc.UIRenderable;

import static me.jraynor.gui.logic.constraint.Constraints.*;
import static me.jraynor.gui.logic.constraint.Constraints.ManualConstraint.NO_OVERRIDE;
import static org.lwjgl.nanovg.NanoVG.*;

public class UILabel extends UIComponent implements UIRenderable {
    @Getter
    @Setter
    private String text;
    private UIConstraint constraints;
    private int fontSize = 18;
    @Setter
    private UIColor color = UIColor.BLACK;
    @Setter
    private UIColor backgroundColor = UIColor.TRANSPARENT;
    private String fontFamily;
    private float[] textBounds = new float[4];
    private UIConstraint parentConstraint;
    private boolean fill = false;
    private float topLeft = 0, topRight = 0, bottomLeft = 0, bottomRight = 0;
    private boolean rounded = false, padding = false;
    @Getter
    private float padLeft, padTop, padRight, padBottom = 0;

    public UILabel(String o) {
        this.text = o;
        this.fontFamily = "regular";
    }

    public UILabel(String text, String fontFamily) {
        this.text = text;
        this.fontFamily = fontFamily;
    }

    public UILabel(String text, UIColor color) {
        this.text = text;
        this.color = color;
        this.fontFamily = "regular";
    }


    public UILabel(String text, String fontFamily, UIColor color) {
        this.text = text;
        this.color = color;
        this.fontFamily = fontFamily;
    }


    public UILabel(String text, String fontFamily, int fontSize, UIColor color) {
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.fontFamily = fontFamily;
    }

    public UILabel(String text, String fontFamily, int fontSize, UIColor color, UIColor backgroundColor) {
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.fontFamily = fontFamily;
        this.backgroundColor = backgroundColor;
        this.fill = true;
    }

    public void setRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
        this.rounded = true;
    }

    public void setRadius(float radius) {
        setRadius(radius, radius, radius, radius);
    }

    public void setPadding(float left, float top, float right, float bottom) {
        this.padLeft = left;
        this.padTop = top;
        this.padRight = right;
        this.padBottom = bottom;
        this.rounded = true;
    }

    public void setPadding(float padding) {
        setPadding(padding, padding, padding, padding);
    }


    @Override
    protected void onUpdate() {
        if (constraints != null && vg != 0) {
            if (getParent() != null) {
                constraints.update();
                nvgSave(vg);
                nvgFontSize(vg, fontSize);
                nvgFontFace(vg, fontFamily);
                calcTextBounds(vg, constraints.x, constraints.y, text, textBounds);
                nvgRestore(vg);
                if (constraints.getOverride() == null) {
                    constraints.setOverride(new ManualConstraint(NO_OVERRIDE, NO_OVERRIDE, textBounds[2] + 4, textBounds[3] - textBounds[1]));
                } else {
                    constraints.getOverride().setW(textBounds[2]);
                    constraints.getOverride().setH(textBounds[3] - textBounds[1]);
                }


                setRender(true);
                if (getParent().hasComponent(UIConstraint.class)) {
                    UIConstraint constraint = (UIConstraint) getParent().getComponent(UIConstraint.class);
                    constraint.setMinWConst(new PixelConstraint(Math.round(textBounds[2] + 5)));
                    parentConstraint = constraint;
                }
            }
        }
    }

    @Override
    public void setParent(UIComponent parent) {
        if (constraints == null) {
            add(new UIConstraint()
                    .setXConst(new Constraints.CenterConstraint())
                    .setYConst(new CenterConstraint())
                    .setWConst(new RelativeConstraint())
                    .setHConst(new RelativeConstraint()));
        }
        super.setParent(parent);
    }

    @Override
    public UIComponent add(UIComponent uiComponent) {
        if (uiComponent instanceof UIConstraint) {
            if (hasComponent(uiComponent.getClass()))
                remove(uiComponent);
            UIConstraint uiConstraint = (UIConstraint) super.add(uiComponent);
            this.constraints = uiConstraint;
            constraints.setPadding(padTop, padRight, padBottom, padLeft);
            return uiConstraint;
        }
        return super.add(uiComponent);
    }


    @Override
    protected void render() {
        if (constraints != null) {
            if (fill) {
                if (rounded) {
                    drawColoredRoundedRect(vg, constraints.x, constraints.y, constraints.w, constraints.h, topLeft, topRight, bottomLeft, bottomRight, backgroundColor);
                } else
                    drawColoredRect(vg, constraints.x, constraints.y, constraints.w, constraints.h, backgroundColor);
            }
            drawText(vg, constraints.x + padLeft, constraints.y + constraints.h / 2, fontSize, text, fontFamily, color);
//            drawColoredRect(vg, constraints.x, constraints.y, constraints.w, constraints.h, UIColor.LIGHT_PURPLE);
        }
    }

    public void setSize(float size) {
        this.fontSize = (int) size;
    }

    public int getFontSize() {
        return fontSize;
    }
}
