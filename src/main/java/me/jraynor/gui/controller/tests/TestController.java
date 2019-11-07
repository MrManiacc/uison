
package me.jraynor.gui.controller.tests;

import me.jraynor.gui.controller.DefaultController;
import me.jraynor.gui.controller.Event;
import me.jraynor.gui.controller.events.MouseEvent;
import me.jraynor.gui.parser.UIType;

public class TestController extends DefaultController {


    @Event(tag = UIType.UIBAR, action = "mouse_enter")
    public void dragStart(MouseEvent e) {
    }

    @Event(group = "drag_bars", action = "mouse_exit")
    public void dragStop(MouseEvent e) {
    }

}
