package me.jraynor.gui.elements.events;

import me.jraynor.gui.UIMaster;
import me.jraynor.gui.controller.events.FocusEvent;
import me.jraynor.gui.controller.events.MouseEvent;
import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.constraint.UIConstraint;
import me.jraynor.misc.Input;

public class UIMouseEvent extends UIComponent {
    private String id;
    private MouseEvent event;
    private FocusEvent eventFocus;
    private UIConstraint constraint;

    public UIMouseEvent(String id) {
        this.id = id;
    }

    @Override
    protected void onAdded() {
        this.event = new MouseEvent(id, getParent());
        this.eventFocus = new FocusEvent(this.id, getParent());
        this.constraint = getParentConstraint();
        getParent().properties.put("hovered", false);
        getParent().properties.put("focused", false);
    }

    private boolean sentHover = false;
    private boolean sentClick = false;

    @Override
    protected void onUpdate() {
        if (this.constraint == null) {
            this.constraint = this.getParentConstraint();
            return;
        }
        boolean hov = isHovered();
        if (hov) {
            if (Input.mouseDown(0) && !sentClick) {
                UIMaster.postEvent(id, "mouse_press", event);
                sentClick = true;
                getParent().properties.put("focused", true);
                eventFocus.setFocused(true);
                UIMaster.postEvent(id, "focus", eventFocus);
            }

            if (!sentHover) {
                UIMaster.postEvent(id, "mouse_enter", event);
                sentHover = true;
                getParent().properties.put("hovered", true);
            }

            if (Input.mouseDown(0)) {
                event.setMouseX(Input.mousePosition.x);
                event.setMouseY(Input.mousePosition.y);
                UIMaster.postEvent(id, "mouse_down", event);
            } else if (Input.mouseReleased(0)) {
                event.setMouseX(Input.mousePosition.x);
                event.setMouseY(Input.mousePosition.y);
                UIMaster.postEvent(id, "mouse_up", event);
            }
        } else {
            if (sentHover) {
                sentHover = false;
                UIMaster.postEvent(id, "mouse_exit", event);
                getParent().properties.put("hovered", false);
            }
            if (eventFocus.isFocused() && Input.mousePressed(0)) {
                getParent().properties.put("focused", false);
                eventFocus.setFocused(false);
                UIMaster.postEvent(id, "focus", eventFocus);
            }
        }

        if (sentClick && Input.mouseReleased(0)) {
            sentClick = false;
        }
    }

    private boolean isHovered() {
        float mx = (float) Input.mousePosition.x, my = (float) Input.mousePosition.y;
        float x = constraint.x;
        float y = constraint.y;
        float w = constraint.w;
        float h = constraint.h;
        event.setMouseX(mx);
        event.setMouseY(my);
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
