package me.jraynor.uison.elements;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.misc.Input;
import me.jraynor.uison.misc.Style;
import me.jraynor.uison.misc.UIRenderable;

import static me.jraynor.uison.logic.constraint.Constraints.ManualConstraint;
import static me.jraynor.uison.logic.constraint.Constraints.ManualConstraint.NO_OVERRIDE;
import static org.lwjgl.nanovg.NanoVG.*;

public class UITextBox extends UIComponent implements UIRenderable {
    @Getter
    @Setter
    private String text = "";
    @Getter
    private String placeholderText;

    @Setter
    private float[] textBounds = new float[4];
    private boolean hovered = false;

    @Override
    public void onStyle(Style style) {
        text = style.getText();
        placeholderText = style.getText();
    }


    @Override
    protected void onUpdate() {
        localConstraint.setPadding(style.getPadding());
        //System.out.println(style.getPadding());
        if (localConstraint != null && vg != 0) {
            if (getParent() != null) {
                localConstraint.update();
                nvgSave(vg);
                nvgFontSize(vg, style.getFontSize());
                nvgFontFace(vg, style.getTextFamily());
                calcTextBounds(vg, localConstraint.x, localConstraint.y, text, textBounds);
                nvgRestore(vg);
                if (localConstraint.getOverride() == null) {
                    localConstraint.setOverride(new ManualConstraint(NO_OVERRIDE, NO_OVERRIDE, NO_OVERRIDE, textBounds[3] - textBounds[1]));
                } else {
                    localConstraint.getOverride().setH(textBounds[3] - textBounds[1]);
                }

                setRender(true);
            }
        }
    }


    @Override
    protected void render() {
        boolean focus = false;
        if (properties.containsKey("focused"))
            focus = (boolean) properties.get("focused");

        if (localConstraint != null) {
            nvgSave(vg);
            nvgScissor(vg, localConstraint.x, localConstraint.y, localConstraint.w, localConstraint.h);

            if (style.isFilled()) {
                if (style.isRounded()) {
                    drawColoredRoundedRect(vg, localConstraint.x, localConstraint.y, localConstraint.w, localConstraint.h, style.getRadius().x, style.getRadius().y, style.getRadius().z, style.getRadius().w, hovered ? style.getHoverColor() : style.getColor());
                } else
                    drawColoredRect(vg, localConstraint.x, localConstraint.y, localConstraint.w, localConstraint.h, hovered ? style.getHoverColor() : style.getColor());
            }
            if (focus) {
                drawText(vg, style.getFontIndent() + localConstraint.x + style.getPadding().x, localConstraint.y + localConstraint.h / 2, style.getFontSize(), text, style.getTextFamily(), style.getTextColor());
            } else
                drawText(vg, style.getFontIndent() + localConstraint.x + style.getPadding().x, localConstraint.y + localConstraint.h / 2, style.getFontSize(), text, style.getTextFamily(), style.getActiveTextColor());
            nvgResetScissor(vg);
            nvgRestore(vg);
        }
    }

    public int getFontSize() {
        return (int) style.getFontSize();
    }

    public void setHover(boolean b) {
        this.hovered = b;
        if (b) {
            Input.setCursor(Input.TEXT_CURSOR);
        } else
            Input.setCursor(Input.DEFAULT_CURSOR);
    }
}
