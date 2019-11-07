package me.jraynor.gui.elements;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.color.UIColor;
import me.jraynor.gui.logic.constraint.UIConstraint;
import me.jraynor.gui.misc.UIRenderable;

import static me.jraynor.gui.logic.constraint.Constraints.ManualConstraint;
import static me.jraynor.gui.logic.constraint.Constraints.ManualConstraint.NO_OVERRIDE;
import static org.lwjgl.nanovg.NanoVG.*;

public class UITextBox extends UIComponent implements UIRenderable {
    @Getter
    @Setter
    private String text;
    private UIConstraint constraints;
    private int fontSize = 18;
    @Setter
    private UIColor color = UIColor.BLACK;
    private UIColor phc = UIColor.TRANSPARENT;
    private UIColor fc = UIColor.TRANSPARENT;
    @Setter
    private String fontFamily;
    private float[] textBounds = new float[4];
    private boolean fill = false;
    @Setter
    private UIColor backgroundColor = UIColor.TRANSPARENT;
    private float topLeft = 0, topRight = 0, bottomLeft = 0, bottomRight = 0;
    private boolean rounded = false;
    @Setter
    private float textIndent;
    private String regx;

    private boolean isEdit = false;

    @Getter
    private float padLeft, padTop, padRight, padBottom = 0;

    public UITextBox(String o) {
        this.text = o;
        this.fontFamily = "regular";
    }

    public UITextBox(String text, String fontFamily) {
        this.text = text;
        this.fontFamily = fontFamily;
        properties.put("placeholder", text);
    }

    public UITextBox(String text, UIColor color) {
        this.text = text;
        this.color = color;
        this.fontFamily = "regular";
        properties.put("placeholder", text);
    }


    public UITextBox(String text, String fontFamily, UIColor color) {
        this.text = text;
        this.color = color;
        this.fontFamily = fontFamily;
        properties.put("placeholder", text);

    }


    public UITextBox(String text, String fontFamily, int fontSize, UIColor color) {
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.fontFamily = fontFamily;
        properties.put("placeholder", text);

    }

    public UITextBox(String text, String fontFamily, int fontSize, UIColor color, UIColor backgroundColor) {
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.fontFamily = fontFamily;
        this.backgroundColor = backgroundColor;
        this.fill = true;
        properties.put("placeholder", text);
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
    protected void onAdded() {
        super.onAdded();
        if (properties.containsKey("phc")) {
            this.phc = (UIColor) properties.get("phc");
        }

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
                    constraints.setOverride(new ManualConstraint(NO_OVERRIDE, NO_OVERRIDE, NO_OVERRIDE, textBounds[3] - textBounds[1]));
                } else {
                    constraints.getOverride().setH(textBounds[3] - textBounds[1]);
                }

                setRender(true);
            }
        }
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
        boolean focus = false;
        if (properties.containsKey("focused"))
            focus = (boolean) properties.get("focused");

        if (constraints != null) {
            if (fill) {
                if (rounded) {
                    drawColoredRoundedRect(vg, constraints.x, constraints.y, constraints.w, constraints.h, topLeft, topRight, bottomLeft, bottomRight, backgroundColor);
                } else
                    drawColoredRect(vg, constraints.x, constraints.y, constraints.w, constraints.h, backgroundColor);
            }
            if (focus) {
                drawText(vg, textIndent + constraints.x + padLeft, constraints.y + constraints.h / 2, fontSize, text, fontFamily, color);
            } else
                drawText(vg, textIndent + constraints.x + padLeft, constraints.y + constraints.h / 2, fontSize, text, fontFamily, phc);

        }
    }

    public int getFontSize() {
        return fontSize;
    }
}
