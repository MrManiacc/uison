package me.jraynor.uison.controller.events;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.uison.logic.UIComponent;

public class EnterEvent implements UIEvent{
    private String id;
    private String action;
    private UIComponent component;
    @Getter
    @Setter
    private String value;
    private UIComponent sender;

    public EnterEvent(String id, UIComponent component, UIComponent sender) {
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
        return action;
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
