package me.jraynor.gui.elements;

import me.jraynor.gui.logic.constraint.Constraints;
import me.jraynor.gui.logic.constraint.UIConstraint;

import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint;
import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint.FACE.BOTTOM;
import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint.FACE.TOP;
import static org.lwjgl.nanovg.NanoVG.*;


public class UIVBox extends UIFlex {
    private float padLeft;
    private float padRight;
    private float padTop;
    private float padBottom;

    public UIVBox(float padTop) {
        this(padTop, padTop, padTop, padTop);
    }

    public UIVBox(float padTop, float padRight, float padBottom, float padLeft) {
        super(padTop);
        this.padTop = padTop;
        this.padRight = padRight;
        this.padBottom = padBottom;
        this.padLeft = padLeft;
    }

    @Override
    public void processLastConstraint(UIConstraint lastConstraint, UIConstraint currentConstraint) {
        float padding = padTop;
        if (currentConstraint.getParent() instanceof UIBar)
            padding = 0;
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
