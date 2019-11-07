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
import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint.FACE.LEFT;
import static org.lwjgl.nanovg.NanoVG.*;

public class UIText extends UIComponent implements UIRenderable {
    @Getter
    @Setter
    private String text;
    private UIConstraint constraints;
    private int fontSize = 18;
    private UIColor color = UIColor.BLACK;
    private String fontFamily;
    private float[] textBounds = new float[4];
    private UIConstraint parentConstraint;
    private boolean fill = false;

    public UIText(String o) {
        this.text = text;
        this.fontFamily = "regular";
    }

    public UIText(String text, String fontFamily) {
        this.text = text;
        this.fontFamily = fontFamily;
    }

    public UIText(String text, UIColor color) {
        this.text = text;
        this.color = color;
        this.fontFamily = "regular";
    }

    public UIText(String text, String fontFamily, UIColor color) {
        this.text = text;
        this.color = color;
        this.fontFamily = fontFamily;
    }


    public UIText(String text, String fontFamily, int fontSize, UIColor color) {
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.fontFamily = fontFamily;
    }

    public UIText setFill(boolean fill) {
        this.fill = fill;
        return this;
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
            if (fill) {
                if (parentConstraint.getOverride() == null)
                    parentConstraint.setOverride(new ManualConstraint(NO_OVERRIDE, NO_OVERRIDE, NO_OVERRIDE, NO_OVERRIDE));
                parentConstraint.getOverride().setW(constraints.w);
                parentConstraint.getOverride().setH(constraints.h);
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
            return uiConstraint;
        }
        return super.add(uiComponent);
    }


    @Override
    protected void render() {
        if (constraints != null) {
            drawText(vg, constraints.x, constraints.y + constraints.h / 2, fontSize, text, fontFamily, color);
//            drawColoredRect(vg, constraints.x, constraints.y, constraints.w, constraints.h, UIColor.LIGHT_PURPLE);
        }
    }
}
