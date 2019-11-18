package me.jraynor.uison.elements;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.color.UIColor;
import me.jraynor.uison.logic.constraint.UIConstraint;
import me.jraynor.uison.misc.Style;
import me.jraynor.uison.misc.UIRenderable;

import java.util.ArrayList;
import java.util.List;


public class UIBlock extends UIComponent implements UIRenderable {
    @Getter
    @Setter
    private UIColor color;
    private List<UIComponent> addLater = new ArrayList<>();
    @Getter
    @Setter
    private boolean hide = false;
    private boolean styled = false;

    public UIBlock() {
        this.setRender(true);
    }


    /**
     * Updates the color, if the color was replaced,
     * then the local copy of the color is updated, otherwise
     * there's no need for an update
     *
     * @param color the new color
     */
    public void setColor(UIColor color) {
        this.color = color;
    }

    @Override
    protected void onAdded() {
        super.onAdded();
        if (this.localConstraint == null && getParent() != null) {
            if (getParent().hasComponent(UIConstraint.class)) {
                this.localConstraint = (UIConstraint) getParent().getComponent(UIConstraint.class);
                this.localConstraint.update();
            }
        }
    }

    @Override
    public void onStyle(Style style) {
        super.onStyle(style);
        this.color = style.getColor();
        styled = true;
    }

    public UIComponent remove(Class clazz) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getClass().equals(clazz))
                return children.remove(i);
        }
        return null;
    }


    @Override
    public void update() {
        super.update();
    }

    @Override
    protected void render() {
        if (render && !hide && styled) {
            if (localConstraint != null) {
                if (style.isTextured())
                    drawImage(vg, localConstraint.x, localConstraint.y, localConstraint.w, localConstraint.h, style.getImage());
                else {
                    if (style.isRounded())
                        drawColoredRoundedRect(vg, localConstraint.getxConst(), localConstraint.getyConst(), localConstraint.getwConst(), localConstraint.gethConst(), style.getRadius().x, style.getRadius().y, style.getRadius().z, style.getRadius().w, style.getColor());
                    else
                        drawColoredRect(vg, localConstraint.x, localConstraint.y, localConstraint.w, localConstraint.h, style.getColor());
                }
            }
        }


    }

    public void addLater(UIComponent uiComponent) {
        addLater.add(uiComponent);
    }
}
