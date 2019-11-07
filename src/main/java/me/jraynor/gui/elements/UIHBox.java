package me.jraynor.gui.elements;

import me.jraynor.gui.logic.constraint.Constraints;
import me.jraynor.gui.logic.constraint.UIConstraint;

import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint;
import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint.FACE.LEFT;
import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint.FACE.RIGHT;
import static org.lwjgl.nanovg.NanoVG.*;


public class UIHBox extends UIFlex {
    private float padLeft;
    private float padRight;
    private float padTop;
    private float padBottom;

    public UIHBox(float padTop) {
        this(padTop, padTop, padTop, padTop);
    }

    public UIHBox(float padTop, float padRight, float padBottom, float padLeft) {
        super(padTop);
        this.padTop = padTop;
        this.padRight = padRight;
        this.padBottom = padBottom;
        this.padLeft = padLeft;
    }

    @Override
    public void processLastConstraint(UIConstraint lastConstraint, UIConstraint currentConstraint) {
        if (lastConstraint != null) {
            if (currentConstraint.getXConstraint() instanceof Constraints.PixelConstraint) {
                Constraints.PixelConstraint pixelConstraint = (Constraints.PixelConstraint) currentConstraint.getXConstraint();
                currentConstraint.setXConst(new StickyConstraint(RIGHT, new Constraints.PixelConstraint(-pixelConstraint.getPixel() - padLeft)).setRelativeConstraint(lastConstraint));
            } else
                currentConstraint.setXConst(new StickyConstraint(RIGHT, new Constraints.PixelConstraint(-padLeft)).setRelativeConstraint(lastConstraint));
        } else {
            if (currentConstraint.getXConstraint() instanceof Constraints.PixelConstraint) {
                Constraints.PixelConstraint pixelConstraint = (Constraints.PixelConstraint) currentConstraint.getXConstraint();
                currentConstraint.setXConst(new StickyConstraint(LEFT, new Constraints.PixelConstraint(pixelConstraint.getPixel() + padLeft)));
            } else {
                currentConstraint.setXConst(new StickyConstraint(LEFT, new Constraints.PixelConstraint(padLeft)));
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
