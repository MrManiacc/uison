package me.jraynor.uison.elements.events;

import me.jraynor.uison.UIMaster;
import me.jraynor.uison.controller.events.FocusEvent;
import me.jraynor.uison.controller.events.SliderEvent;
import me.jraynor.uison.elements.UISlider;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.constraint.Constraints;
import me.jraynor.uison.logic.constraint.UIConstraint;
import me.jraynor.uison.misc.Input;

public class UISliderEvent extends UIComponent {
    private String id;
    private SliderEvent event;
    private FocusEvent eventFocus;
    private UIConstraint barConstraint, nobConstraint;
    private float min, max, val;
    private boolean dragging = false;
    private Constraints.PixelConstraint xConstraint;
    private UISlider slider;

    public UISliderEvent(String id) {
        this.id = id;
    }

    @Override
    protected void onAdded() {
        if (getParent() instanceof UISlider) {
            slider = (UISlider) getParent();
            this.nobConstraint = slider.getNobConstraint();
            this.barConstraint = slider.getConstraint();
            this.event = new SliderEvent(id, "slider_move", getParent(), nobConstraint, barConstraint);
            this.eventFocus = new FocusEvent(this.id, getParent(), this);
            if (slider.properties.containsKey("min"))
                this.min = (float) slider.properties.get("min");
            else
                this.min = 0;
            if (slider.properties.containsKey("max"))
                this.max = (float) slider.properties.get("max");
            else
                this.max = 100;
            this.val = min;
            this.nobConstraint.setOverride(new Constraints.ManualConstraint());
            this.event.setMin(min);
            this.event.setVal(val);
            this.event.setMax(max);
            if (nobConstraint.getXConstraint() instanceof Constraints.StickyConstraint) {
                if (((Constraints.StickyConstraint) nobConstraint.getXConstraint()).getPixelConstraint() instanceof Constraints.PixelConstraint)
                    xConstraint = (Constraints.PixelConstraint) ((Constraints.StickyConstraint) nobConstraint.getXConstraint()).getPixelConstraint();
            }
        }


    }


    @Override
    protected void onUpdate() {
        boolean hov = isHovered(nobConstraint);
        if (hov) {
            event.setAction("slider_hover");
            UIMaster.postEvent(id, "slider_hover", event);
            if (Input.mouseDown(0))
                dragging = true;
        }

        if (dragging) {
            float mx = (float) Input.mousePosition.x;
            float w = nobConstraint.w;
            float minX = barConstraint.x + w / 2;
            float maxX = barConstraint.x + barConstraint.w - w / 2;


            float x = mx;
            if (x <= minX)
                x = minX;
            else if (x >= maxX)
                x = maxX;
            x -= w / 2;
            float relX = (x - barConstraint.x);
            xConstraint.setPixel(relX);
            x += w / 2;
            float wRatio = maxX - minX;
            float multiplier = 100.0f / wRatio;

            float xRatio = x - minX;
            float percent = (xRatio * multiplier);

            float min = this.min;
            float max = this.max;

            float val = ((percent * (max - min) / 100) + min);
            this.event.setMin(min);
            this.event.setVal(val);
            this.event.setMax(max);
            slider.setVal(val);
            event.setAction("slider_value");
            UIMaster.postEvent(id, "slider_value", event);
        }


        if (Input.mouseReleased(0))
            dragging = false;


    }

    private boolean isHovered(UIConstraint constraint) {
        float mx = (float) Input.mousePosition.x, my = (float) Input.mousePosition.y;
        float x = constraint.x;
        float y = constraint.y;
        float w = constraint.w;
        float h = constraint.h;
        event.setMx(mx);
        event.setMy(my);
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
