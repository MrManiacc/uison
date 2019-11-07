package me.jraynor.gui.elements;

import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.constraint.Constraints;
import me.jraynor.gui.logic.constraint.UIConstraint;

import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint;
import static me.jraynor.gui.logic.constraint.Constraints.StickyConstraint.FACE.BOTTOM;
import static org.lwjgl.nanovg.NanoVG.*;


public class UIVBox extends UIFlex {
    public UIVBox(float padding) {
        super(padding);
    }

    @Override
    public void processLastConstraint(UIConstraint lastConstraint, UIConstraint currentConstraint) {
        if (lastConstraint != null)
            currentConstraint.setYConst(new StickyConstraint(BOTTOM, new Constraints.PixelConstraint(-padding)).setRelativeConstraint(lastConstraint));
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
