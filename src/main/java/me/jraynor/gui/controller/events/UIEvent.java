package me.jraynor.gui.controller.events;

import me.jraynor.gui.logic.UIComponent;

public interface UIEvent {
    void setID(String id);
    void setAction(String action);
    void setComponent(UIComponent component);

    String id();
    String action();
    UIComponent component();
}
