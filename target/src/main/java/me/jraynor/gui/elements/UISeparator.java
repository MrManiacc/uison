package me.jraynor.gui.elements;

import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.color.UIColor;
import me.jraynor.gui.logic.constraint.UIConstraint;
import me.jraynor.gui.misc.UIRenderable;
import static me.jraynor.gui.logic.constraint.Constraints.*;
public class UISeparator extends UIComponent implements UIRenderable {
    private UIColor color;
    private UIConstraint constraints;

    public UISeparator(UIColor color) {
        this.color = color;
        setRender(true);
    }

    @Override
    public UIComponent add(UIComponent uiComponent) {
        if (uiComponent instanceof UIConstraint) {
            if (hasComponent(uiComponent.getClass()))
                remove(uiComponent);
            UIConstraint uiConstraint = (UIConstraint) super.add(uiComponent);
            this.constraints = uiConstraint;
            this.constraints.update();
            return uiConstraint;
        }
        return super.add(uiComponent);
    }

    @Override
    protected void render() {
        drawColoredRect(vg, constraints.x, constraints.y, constraints.w, constraints.h, color);
    }
}
