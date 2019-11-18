package me.jraynor.uison.elements;

import me.jraynor.uison.logic.constraint.Constraints;
import me.jraynor.uison.logic.constraint.UIConstraint;

import static me.jraynor.uison.logic.constraint.Constraints.StickyConstraint;
import static me.jraynor.uison.logic.constraint.Constraints.StickyConstraint.FACE.BOTTOM;
import static me.jraynor.uison.logic.constraint.Constraints.StickyConstraint.FACE.TOP;
import static org.lwjgl.nanovg.NanoVG.*;


public class UIVBox extends UIFlex {
    public UIVBox() {
        setRender(true);
    }

    @Override
    public void processLastConstraint(UIConstraint lastConstraint, UIConstraint currentConstraint) {
        float padding = style.getPadding().x;
        if (currentConstraint.getParent() instanceof UIBar)
            padding = 0;
        if (!(currentConstraint.getYConstraint() instanceof StickyConstraint))
            if (lastConstraint != null) {
                if (currentConstraint.getYConstraint() instanceof Constraints.PixelConstraint) {
                    Constraints.PixelConstraint pixelConstraint = (Constraints.PixelConstraint) currentConstraint.getYConstraint();
                    currentConstraint.setYConst(new StickyConstraint(BOTTOM, new Constraints.PixelConstraint(-pixelConstraint.getPixel() - padding)).setRelativeConstraint(lastConstraint));
                } else
                    currentConstraint.setYConst(new StickyConstraint(BOTTOM, new Constraints.PixelConstraint(-padding)).setRelativeConstraint(lastConstraint));
            } else {
                if (currentConstraint.getYConstraint() instanceof Constraints.PixelConstraint) {
                    Constraints.PixelConstraint pixelConstraint = (Constraints.PixelConstraint) currentConstraint.getYConstraint();
                    currentConstraint.setYConst(new StickyConstraint(TOP, new Constraints.PixelConstraint(pixelConstraint.getPixel() + padding)));
                } else {
                    currentConstraint.setYConst(new StickyConstraint(TOP, new Constraints.PixelConstraint(padding)));
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
