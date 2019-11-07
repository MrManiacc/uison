package me.jraynor.gui.elements;

import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.color.UIColor;
import me.jraynor.gui.logic.constraint.Constraints;
import me.jraynor.gui.logic.constraint.UIConstraint;

import java.util.ArrayList;
import java.util.List;

import static me.jraynor.gui.logic.constraint.Constraints.ManualConstraint.NO_OVERRIDE;

public class UIFlex extends UIComponent {
    private List<UIConstraint> childrenConstraints;
    protected UIConstraint localConstraint;
    private UIConstraint lastConstraint;
    private boolean flexWidth = true, flexHeight = true;
    protected float padding;
    private UIBlock block;

    public UIFlex() {
        this.childrenConstraints = new ArrayList<>();
        this.padding = 0;
    }

    public UIFlex(float padding) {
        this.childrenConstraints = new ArrayList<>();
        this.padding = padding;
    }

    public UIFlex setPadding(float padding) {
        this.padding = padding;
        return this;
    }

    public UIFlex fill(UIColor color) {
        this.block = new UIBlock(color);
        return this;
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
    protected void onAdded() {
        if (block != null)
            add(block);
    }

    @Override
    public UIComponent add(UIComponent uiComponent) {
        if (uiComponent instanceof UIConstraint) {
            this.localConstraint = (UIConstraint) super.add(uiComponent);
            localConstraint.update();
            return this.localConstraint;
        } else if (uiComponent.hasComponent(UIConstraint.class)) {
            UIConstraint uiConstraint = (UIConstraint) uiComponent.getComponent(UIConstraint.class);
            processLastConstraint(lastConstraint, uiConstraint);
            lastConstraint = uiConstraint;
            lastConstraint.update();
            childrenConstraints.add(lastConstraint);
            return super.add(uiComponent);
        }
        if (uiComponent instanceof UIBlock) {
            UIBlock block = (UIBlock) super.add(uiComponent);
            if (!block.hasComponent(UIConstraint.class))
                block.add(UIConstraint.fill());
            return block;
        }
        return super.add(uiComponent);
    }

    public void processLastConstraint(UIConstraint lastConstraint, UIConstraint currentConstraint) {
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
//        System.out.println(flexWidth);
        if(flexWidth)
            flexWidth();
        if(flexHeight)
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

//    @Override
//    protected void onUpdate() {
//        super.onUpdate();
//        float maxHeight = maxHeight();
//        if (localConstraint.getOverride() == null)
//            this.localConstraint.setOverride(new Constraints.ManualConstraint(NO_OVERRIDE, NO_OVERRIDE, NO_OVERRIDE, maxHeight + padding));
//        else
//            this.localConstraint.getOverride().setH(maxHeight + padding);
//        if (fill) {
//            float maxWidth = maxWidth();
//            this.localConstraint.getOverride().setW(maxWidth + padding);
//        }
//    }

    protected float maxWidth() {
        float maxSize = 0;
        for (UIConstraint constraint : childrenConstraints) {
            float size = (constraint.x + constraint.w) - localConstraint.x;
            if (size > maxSize)
                maxSize = size;
        }
        return maxSize;
    }

    protected float maxHeight() {
        float maxSize = 0;
        for (UIConstraint constraint : childrenConstraints) {
            float size = (constraint.y + constraint.h) - localConstraint.y;
            if (size > maxSize)
                maxSize = size;
        }
        return maxSize;
    }


}
