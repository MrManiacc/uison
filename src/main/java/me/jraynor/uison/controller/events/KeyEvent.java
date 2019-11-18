package me.jraynor.uison.controller.events;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.uison.logic.UIComponent;

public class KeyEvent implements UIEvent {
    private String id, action;
    private UIComponent component;
    @Setter
    @Getter
    private double mouseX, mouseY;
    @Getter
    @Setter
    private char keyTyped;
    @Getter
    @Setter
    private String text;
    private UIComponent sender;

    public KeyEvent(String id, UIComponent component, UIComponent sender) {
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
    public UIComponent sender() {
        return sender;
    }
}