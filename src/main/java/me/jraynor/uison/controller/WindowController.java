package me.jraynor.uison.controller;

import me.jraynor.bootstrap.Window;
import me.jraynor.uison.controller.events.MouseEvent;
import me.jraynor.uison.elements.UIBar;
import me.jraynor.uison.elements.UILabel;
import org.joml.Vector2d;

public class WindowController extends DefaultController {

    @Component(id = "window_bar")
    UIBar uiBar;
    @Component(id = "window_close")
    UILabel uiClose;
    @Component(id = "window_title")
    UILabel uiTitle;

    private Vector2d startingOffset = new Vector2d();
    private boolean dragging = false;

    public WindowController(Window window) {
        super(window, false);
    }

    @Override
    protected void onReady() {
        if (uiBar != null && window != null) {
            if (uiTitle != null) {
                uiTitle.setText(window.getTitle());
            }
        }

    }

    @Event(id = "window_bar", action = "mouse_up")
    public void windowBarMouseUp(MouseEvent mouseEvent) {
        dragging = false;
    }


    @Event(id = "window_bar", action = "mouse_down")
    public void windowBarMouseDown(MouseEvent mouseEvent) {
        if (dragging) {
            window.setPosition((int) ((int) mouseEvent.globalMx - startingOffset.x), (int) ((int) mouseEvent.globalMy - startingOffset.y));
        }
    }

    @Event(id = "window_bar", action = "mouse_press")
    public void windowBarMousePress(MouseEvent mouseEvent) {
        if (mouseEvent != null) {
            startingOffset.x = mouseEvent.mx;
            startingOffset.y = mouseEvent.my;
            dragging = true;
        }
    }

    @Event(id = "window_close", action = "mouse_press")
    public void windowCloseMousePress(MouseEvent mouseEvent) {
        System.exit(0);
    }


}
