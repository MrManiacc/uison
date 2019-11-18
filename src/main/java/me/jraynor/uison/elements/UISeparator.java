package me.jraynor.uison.elements;

import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.color.UIColor;
import me.jraynor.uison.logic.constraint.UIConstraint;
import me.jraynor.uison.misc.UIRenderable;

public class UISeparator extends UIComponent implements UIRenderable {
    private UIColor color;
    private UIConstraint constraints;

    public UISeparator() {
        setRender(true);
    }

    @Override
    protected void onAdded() {
        if (properties.containsKey("c"))
            this.color = (UIColor) properties.get("c");
        super.onAdded();
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
