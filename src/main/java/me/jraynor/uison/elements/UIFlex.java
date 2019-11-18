package me.jraynor.uison.elements;

import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.constraint.Constraints;
import me.jraynor.uison.logic.constraint.UIConstraint;
import me.jraynor.uison.misc.UIRenderable;

import java.util.ArrayList;
import java.util.List;

import static me.jraynor.uison.logic.constraint.Constraints.ManualConstraint.NO_OVERRIDE;

public class UIFlex extends UIComponent implements UIRenderable {
    private List<UIConstraint> childrenConstraints;
    private UIConstraint lastConstraint;
    private boolean flexWidth = true, flexHeight = true;
    protected float padding = 0;

    public UIFlex() {
        this.childrenConstraints = new ArrayList<>();
        this.padding = 0;
        setRender(true);
    }


    public UIFlex setFlexWidth(boolean flex) {
        this.flexWidth = flex;
        return this;
    }

    public UIFlex setFlexHeight(boolean flex) {
        this.flexHeight = flex;
        return this;
    }


    @Override
    public UIComponent add(UIComponent uiComponent) {
        if (uiComponent.hasComponent(UIConstraint.class)) {
            UIConstraint uiConstraint = (UIConstraint) uiComponent.getComponent(UIConstraint.class);
            processLastConstraint(lastConstraint, uiConstraint);
            lastConstraint = uiConstraint;
            lastConstraint.update();
            childrenConstraints.add(lastConstraint);
            return super.add(uiComponent);
        }

        return super.add(uiComponent);
    }

    public void processLastConstraint(UIConstraint lastConstraint, UIConstraint currentConstraint) {
    }



    @Override
    protected void onUpdate() {
        super.onUpdate();
        if (flexWidth)
            flexWidth();
        if (flexHeight)
            flexHeight();


    }

    protected void flexWidth() {
        float maxWidth = maxWidth();
        if (localConstraint.getOverride() == null)
            this.localConstraint.setOverride(new Constraints.ManualConstraint(NO_OVERRIDE, NO_OVERRIDE, maxWidth + padding, NO_OVERRIDE));
        else
            this.localConstraint.getOverride().setW(maxWidth + padding);
    }

    protected void flexHeight() {
        float maxHeight = maxHeight();
        if (localConstraint.getOverride() == null)
            this.localConstraint.setOverride(new Constraints.ManualConstraint(NO_OVERRIDE, NO_OVERRIDE, NO_OVERRIDE, maxHeight + padding));
        else
            this.localConstraint.getOverride().setH(maxHeight + padding);
    }


    protected float maxWidth() {
        float maxSize = 0;
        for (UIConstraint constraint : childrenConstraints) {
            float size = (constraint.x + constraint.w) - localConstraint.x;
            if (size > maxSize)
                maxSize = size;

        }
        return maxSize;
    }

    public float maxHeight() {
        float maxSize = 0;
        for (UIConstraint constraint : childrenConstraints) {
            float size = (constraint.y + constraint.h) - localConstraint.y;
            if (size > maxSize)
                maxSize = size;
        }
        return maxSize;
    }

    @Override
    protected void render() {
        if (style.isRounded())
            drawColoredRoundedRect(vg, localConstraint.getxConst(), localConstraint.getyConst(), localConstraint.getwConst(), localConstraint.gethConst(), style.getRadius().x, style.getRadius().y, style.getRadius().z, style.getRadius().w, style.getColor());
        else
            drawColoredRect(vg, localConstraint.x, localConstraint.y, localConstraint.w, localConstraint.h, style.getColor());
    }
}
