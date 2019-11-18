package me.jraynor.uison.elements;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.misc.Style;
import me.jraynor.uison.misc.UIRenderable;

import static me.jraynor.uison.logic.constraint.Constraints.ManualConstraint;
import static me.jraynor.uison.logic.constraint.Constraints.ManualConstraint.NO_OVERRIDE;
import static org.lwjgl.nanovg.NanoVG.*;

public class UILabel extends UIComponent implements UIRenderable {
    @Getter
    @Setter
    private String text;
    private float[] textBounds = new float[4];
    private float textSize;

    @Override
    protected void onAdded() {
        super.onAdded();
        setRender(true);
    }

    @Override
    public void onStyle(Style style) {
        this.text = style.getText();
    }

    @Override
    protected void onUpdate() {
        if (localConstraint != null && vg != 0) {
            if (getParent() != null) {
                localConstraint.update();
                nvgSave(vg);
                nvgFontSize(vg, style.getFontSize());
                nvgFontFace(vg, style.getTextFamily());
                calcTextBounds(vg, localConstraint.x, localConstraint.y, text, textBounds);
                nvgRestore(vg);
                if (localConstraint.getOverride() == null)
                    localConstraint.setOverride(new ManualConstraint(NO_OVERRIDE, NO_OVERRIDE, NO_OVERRIDE, NO_OVERRIDE));
                if (localConstraint.getWidthConstraint() == null)
                    localConstraint.getOverride().setW(textBounds[2]);
                localConstraint.getOverride().setH(textBounds[3] - textBounds[1]);

                textSize = textBounds[2];
            }
        }
    }


    @Override
    protected void render() {
        if (localConstraint != null) {
            if (style.isPadded())
                localConstraint.setPadding(style.getPadding());
            float offset = localConstraint.w - textSize;
            float center = offset / 2;
            center -= 3;
            if (localConstraint.getWidthConstraint() == null)
                center = 0;

            if (style.isFilled()) {
                if (style.isRounded()) {
                    if (offset < 0)
                        drawColoredRoundedRect(vg, localConstraint.x, localConstraint.y, textSize + style.getPadding().y, localConstraint.h, style.getRadius().x, style.getRadius().y, style.getRadius().z, style.getRadius().w, style.getColor());
                    else
                        drawColoredRoundedRect(vg, localConstraint.x, localConstraint.y, localConstraint.w, localConstraint.h, style.getRadius().x, style.getRadius().y, style.getRadius().z, style.getRadius().w, style.getColor());
                } else {
                    if (offset < 0 && style.isPadded())
                        drawColoredRect(vg, localConstraint.x, localConstraint.y, textSize + style.getPadding().y, localConstraint.h, style.getColor());
                    else
                        drawColoredRect(vg, localConstraint.x, localConstraint.y, localConstraint.w, localConstraint.h, style.getColor());
                }
            }
            if (style.isPadded())
                if (offset < 0)
                    drawText(vg, localConstraint.x + style.getPadding().x, localConstraint.y + localConstraint.h / 2, style.getFontSize(), text, style.getTextFamily(), style.getTextColor());
                else
                    drawText(vg, localConstraint.x + style.getPadding().x + center, localConstraint.y + localConstraint.h / 2, style.getFontSize(), text, style.getTextFamily(), style.getTextColor());
            else if (offset < 0)
                drawText(vg, localConstraint.x, localConstraint.y + localConstraint.h / 2, style.getFontSize(), text, style.getTextFamily(), style.getTextColor());
            else
                drawText(vg, localConstraint.x + center, localConstraint.y + localConstraint.h / 2, style.getFontSize(), text, style.getTextFamily(), style.getTextColor());

        }
    }
}
