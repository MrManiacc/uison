package me.jraynor.uison.controller.events;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.constraint.UIConstraint;

public class SliderEvent implements UIEvent {
    private String id;
    @Setter
    private String action;
    private UIComponent component;
    private UIConstraint nobConstraint;
    private UIConstraint barConstraint;
    @Getter
    @Setter
    private float min, max, val;
    @Getter
    @Setter
    private float mx, my;

    public SliderEvent(String id, String action, UIComponent component, UIConstraint nobConstraint, UIConstraint barConstraint) {
        this.id = id;
        this.action = action;
        this.component = component;
        this.nobConstraint = nobConstraint;
        this.barConstraint = barConstraint;
    }

    @Override

    public void setID(String id) {
        this.id = id;
    }

    @Override
    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String action() {
        return action;
    }

    @Override
    public UIComponent component() {
        return component;
    }

    @Override
    public UIComponent sender() {
        return null;
    }
}
