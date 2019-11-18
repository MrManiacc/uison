package me.jraynor.uison.controller.events;

import me.jraynor.uison.logic.UIComponent;

public interface UIEvent {
    void setID(String id);

    void setAction(String action);

    String id();

    String action();

    UIComponent component();

    UIComponent sender();
}
