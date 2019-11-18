package me.jraynor.uison.elements;

import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.constraint.Constraints;
import me.jraynor.uison.logic.constraint.UIConstraint;

import static me.jraynor.uison.logic.constraint.Constraints.ManualConstraint.NO_OVERRIDE;
import static me.jraynor.uison.logic.constraint.Constraints.StickyConstraint;
import static me.jraynor.uison.logic.constraint.Constraints.StickyConstraint.FACE.LEFT;
import static me.jraynor.uison.logic.constraint.Constraints.StickyConstraint.FACE.RIGHT;
import static org.lwjgl.nanovg.NanoVG.*;


public class UIHBox extends UIFlex {

    public UIHBox() {
        setRender(true);
    }

    @Override
    public UIComponent add(UIComponent uiComponent) {
        UIComponent component = super.add(uiComponent);
        if (component instanceof UIConstraint) {
            if (((UIConstraint) component).getHeightConstraint() != null) {
                if (this.localConstraint.getOverride() != null)
                    this.localConstraint.getOverride().setH(NO_OVERRIDE);
            }
        }
        return component;
    }

    @Override
    public void processLastConstraint(UIConstraint lastConstraint, UIConstraint currentConstraint) {
        float padding = style.getPadding().x;
        if (lastConstraint != null) {
            if (currentConstraint.getXConstraint() instanceof Constraints.PixelConstraint) {
                Constraints.PixelConstraint pixelConstraint = (Constraints.PixelConstraint) currentConstraint.getXConstraint();
                currentConstraint.setXConst(new StickyConstraint(RIGHT, new Constraints.PixelConstraint(-pixelConstraint.getPixel() - padding)).setRelativeConstraint(lastConstraint));
            } else
                currentConstraint.setXConst(new StickyConstraint(RIGHT, new Constraints.PixelConstraint(-padding)).setRelativeConstraint(lastConstraint));
        } else {
            if (currentConstraint.getXConstraint() instanceof Constraints.PixelConstraint) {
                Constraints.PixelConstraint pixelConstraint = (Constraints.PixelConstraint) currentConstraint.getXConstraint();
                currentConstraint.setXConst(new StickyConstraint(LEFT, new Constraints.PixelConstraint(pixelConstraint.getPixel() + padding)));
            } else {
                currentConstraint.setXConst(new StickyConstraint(LEFT, new Constraints.PixelConstraint(padding)));
            }
        }
    }


    @Override
    public void update() {
        nvgSave(vg);
        nvgScissor(vg, localConstraint.x, localConstraint.y, localConstraint.w, localConstraint.h);
        super.update();
        nvgResetScissor(vg);
        nvgRestore(vg);
    }
}
