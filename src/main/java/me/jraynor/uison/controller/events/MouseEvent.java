package me.jraynor.uison.controller.events;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.uison.logic.UIComponent;

public class MouseEvent implements UIEvent {
    private String id, action;
    private UIComponent component;
    public double mx, my;
    public double globalMx = 0, globalMy = 0;
    private UIComponent sender;

    public MouseEvent(String id, UIComponent component, UIComponent sender) {
        this.id = id;
        this.component = component;
        this.sender = sender;
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
    public UIComponent sender() {
        return sender;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String action() {
        return "mouse_over";
    }

    @Override
    public UIComponent component() {
        return component;
    }

    @Override
    public String toString() {
        return "MouseEvent{" +
                "my=" + my +
                ", mx=" + mx +
                ", id='" + id + '\'' +
                ", globalMy=" + globalMy +
                ", globalMx=" + globalMx +
                ", action='" + action + '\'' +
                '}';
    }
}
