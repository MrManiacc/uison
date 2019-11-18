package me.jraynor.uison.elements;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.color.UIColor;
import me.jraynor.uison.logic.constraint.Constraints;
import me.jraynor.uison.logic.constraint.UIConstraint;
import me.jraynor.uison.misc.Style;
import me.jraynor.uison.misc.UIRenderable;

public class UIBar extends UIComponent implements UIRenderable {
    public UIBar() {
        setRender(true);
    }

    @Override
    public UIComponent add(UIComponent uiComponent) {
        if (uiComponent instanceof UIConstraint) {
            this.localConstraint = (UIConstraint) super.add(uiComponent);
            this.localConstraint.update();
            this.localConstraint.setYConst(new Constraints.StickyConstraint(Constraints.StickyConstraint.FACE.TOP));
            this.localConstraint.setXConst(new Constraints.StickyConstraint(Constraints.StickyConstraint.FACE.LEFT));
            this.localConstraint.setWConst(new Constraints.RelativeConstraint(1));
        }
        return super.add(uiComponent);
    }




    @Override
    protected void render() {
        drawColoredRect(vg, localConstraint.x, localConstraint.y, localConstraint.w, localConstraint.h, style.getColor());
    }
}
