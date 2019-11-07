package me.jraynor.gui.elements;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.color.UIColor;
import me.jraynor.gui.logic.constraint.Constraints;
import me.jraynor.gui.logic.constraint.UIConstraint;
import me.jraynor.gui.misc.UIRenderable;

public class UIBar extends UIComponent implements UIRenderable {
    @Getter
    private UIConstraint constraint;
    @Setter
    private UIColor color = UIColor.WHITE;

    @Override
    public UIComponent add(UIComponent uiComponent) {
        if (uiComponent instanceof UIConstraint) {
            this.constraint = (UIConstraint) super.add(uiComponent);
            this.constraint.update();
            this.constraint.setYConst(new Constraints.StickyConstraint(Constraints.StickyConstraint.FACE.TOP));
            this.constraint.setXConst(new Constraints.StickyConstraint(Constraints.StickyConstraint.FACE.LEFT));
            this.constraint.setWConst(new Constraints.RelativeConstraint(1));
        }
        return super.add(uiComponent);
    }

    @Override
    protected void onAdded() {
        if (properties.containsKey("c")) {
            this.color = (UIColor) properties.get("c");
            setRender(true);
        }
    }

    @Override
    protected void onUpdate() {

    }

    @Override
    protected void render() {
        drawColoredRect(vg, constraint.x, constraint.y, constraint.w, constraint.h, color);
    }
}
