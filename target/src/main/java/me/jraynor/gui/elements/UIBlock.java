package me.jraynor.gui.elements;

import lombok.Getter;
import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.color.UIColor;
import me.jraynor.gui.logic.constraint.UIConstraint;
import me.jraynor.gui.misc.UIRenderable;

import java.util.ArrayList;
import java.util.List;


public class UIBlock extends UIComponent implements UIRenderable {
    @Getter
    private UIColor color;
    @Getter
    private UIConstraint constraints;
    @Getter
    private UIDropShadow dropShadow;
    private List<UIComponent> addLater = new ArrayList<>();

    public UIBlock(UIColor color, UIConstraint constraint) {
        this.color = (UIColor) add(color);
        this.constraints = (UIConstraint) add(constraint);
        this.setRender(true);
        setRender(true);
    }

    public UIBlock(UIColor color) {
        this.color = (UIColor) add(color);
        this.setRender(true);
        setRender(true);
    }

    /**
     * Updates the color, if the color was replaced,
     * then the local copy of the color is updated, otherwise
     * there's no need for an update
     *
     * @param color the new color
     */
    public void setColor(UIColor color) {
        replace(color);
        this.color = color;
    }

    @Override
    protected void onAdded() {
        if (this.constraints == null && getParent() != null) {
            if (getParent().hasComponent(UIConstraint.class)) {
                this.constraints = (UIConstraint) getParent().getComponent(UIConstraint.class);
                this.constraints.update();
            }
        }

        for (UIComponent component : addLater) {
            add(component);
        }
    }

    @Override
    public UIComponent add(UIComponent uiComponent) {
        if (uiComponent instanceof UIConstraint) {
            if (hasComponent(uiComponent.getClass()))
                remove(uiComponent);
            UIConstraint uiConstraint = (UIConstraint) super.add(uiComponent);
            this.constraints = uiConstraint;
            constraints.update();
            return uiConstraint;
        } else if (uiComponent instanceof UIDropShadow) {
            if (hasComponent(uiComponent.getClass()))
                remove(uiComponent);
            UIDropShadow uiDropShadow = (UIDropShadow) super.add(uiComponent);
            this.dropShadow = uiDropShadow;
            return uiDropShadow;
        }
        return super.add(uiComponent);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    protected void render() {
        if (constraints != null) {
            if (!constraints.isRounded()) {
                drawColoredRect(vg, constraints.getxConst() - 1, constraints.getyConst() - 1, constraints.getwConst() + 2, constraints.gethConst() + 2, color);
            } else
                drawColoredRoundedRect(vg, constraints.getxConst() - 1, constraints.getyConst() - 1, constraints.getwConst() + 2, constraints.gethConst() + 2, constraints.getRound(), constraints.getRound(), constraints.getRound(), constraints.getRound(), color);
            if (dropShadow != null) {
                drawDropShadow(vg, dropShadow.getXOffset(), dropShadow.getYOffset(), constraints.getxConst(), constraints.getyConst(), constraints.getwConst(), constraints.gethConst(), dropShadow.getRound(), dropShadow.getBlur(), dropShadow.getSpread(), dropShadow.getInnerColor(), dropShadow.getOuterColor(), dropShadow.getVgPaint());
            }
        }
    }

    public void addLater(UIComponent uiComponent) {
        addLater.add(uiComponent);
    }
}
