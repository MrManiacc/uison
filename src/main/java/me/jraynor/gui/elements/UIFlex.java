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
    private int blockPos;
    private String[] ignoredFlexComponents;

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

    public void setIgnored(String[] ignored) {
        this.ignoredFlexComponents = ignored;
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
        if (block != null) {
            int index = getChildren().indexOf(block);
            if (index != 0)
                getChildren().add(0, getChildren().remove(index));
        }
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
            boolean skip = false;
            if (ignoredFlexComponents != null)
                for (String s : ignoredFlexComponents) {
                    if (constraint.getMappedUIComponent() != null)
                        if (s.equalsIgnoreCase(constraint.getMappedUIComponent().getId())) {
                            skip = true;
                            break;
                        }
                }
            if (skip) continue;
            float size = (constraint.x + constraint.w) - localConstraint.x;
            if (size > maxSize)
                maxSize = size;

        }
        return maxSize;
    }

    public float maxHeight() {
        float maxSize = 0;
        for (UIConstraint constraint : childrenConstraints) {
            boolean skip = false;
            if (ignoredFlexComponents != null)
                for (String s : ignoredFlexComponents) {
                    if (constraint.getMappedUIComponent() != null)
                        if (s.equalsIgnoreCase(constraint.getMappedUIComponent().getId())) {
                        skip = true;
                        break;
                    }
                }
            if (skip) continue;
            float size = (constraint.y + constraint.h) - localConstraint.y;
            if (size > maxSize) {
                maxSize = size;
            }
        }
        return maxSize;
    }


}
