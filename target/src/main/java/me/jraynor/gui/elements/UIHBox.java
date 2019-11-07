package me.jraynor.gui.elements;

import me.jraynor.gui.logic.constraint.Constraints;
import me.jraynor.gui.logic.constraint.UIConstraint;

import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint;
import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint.FACE.LEFT;
import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint.FACE.RIGHT;
import static org.lwjgl.nanovg.NanoVG.*;


public class UIHBox extends UIFlex {
    public UIHBox(float padding) {
        super(padding);
    }

    @Override
    public void processLastConstraint(UIConstraint lastConstraint, UIConstraint currentConstraint) {
        if (lastConstraint != null)
            currentConstraint.setXConst(new StickyConstraint(RIGHT, new Constraints.PixelConstraint(-padding)).setRelativeConstraint(lastConstraint));
        else
            currentConstraint.setXConst(new StickyConstraint(LEFT, new Constraints.PixelConstraint(padding)));
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
