package me.jraynor.gui.controller.events;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.gui.logic.UIComponent;

public class FocusEvent implements UIEvent {
    private String id;
    private String action;
    private UIComponent component;
    @Getter
    @Setter
    private boolean focused;

    public FocusEvent(String id, UIComponent component) {
        this.id = id;
        this.component = component;
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
    public void setComponent(UIComponent component) {
        this.component = component;
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

}
