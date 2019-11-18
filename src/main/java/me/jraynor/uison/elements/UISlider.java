package me.jraynor.uison.elements;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.color.UIColor;
import me.jraynor.uison.logic.constraint.Constraints;
import me.jraynor.uison.logic.constraint.UIConstraint;
import me.jraynor.uison.misc.UIRenderable;

import static me.jraynor.uison.logic.constraint.Constraints.StickyConstraint.FACE.LEFT;

public class UISlider extends UIComponent implements UIRenderable {
    @Getter
    private UIConstraint constraint, nobConstraint;
    private UIColor lineColor;
    private UIColor nobColor;
    @Setter
    @Getter
    private float min, max, val;

    @Override
    public UIComponent add(UIComponent uiComponent) {
        if (uiComponent instanceof UIConstraint) {
            constraint = (UIConstraint) super.add(uiComponent);
            constraint.update();
            nobConstraint = new UIConstraint();
            nobConstraint.setXConst(new Constraints.StickyConstraint(LEFT, new Constraints.PixelConstraint(0)));
            nobConstraint.setYConst(new Constraints.CenterConstraint());
            nobConstraint.setWConst(new Constraints.PixelConstraint(20));
            nobConstraint.setHConst(new Constraints.PixelConstraint(20));
            constraint.add(nobConstraint);
            setRender(true);
        }
        return super.add(uiComponent);
    }

    @Override
    protected void onAdded() {
        super.onAdded();
        if (properties.containsKey("lc"))
            this.lineColor = (UIColor) properties.get("lc");
        else
            this.lineColor = UIColor.LIGHT_PURPLE;
        if (properties.containsKey("nc"))
            this.nobColor = (UIColor) properties.get("nc");
        else
            this.nobColor = UIColor.DARK_RED;

        if (properties.containsKey("min"))
            this.min = (float) properties.get("min");
        else
            this.min = 0;

        if (properties.containsKey("max"))
            this.max = (float) properties.get("max");
        else
            this.max = 0;

        this.val = min;
    }

    @Override
    protected void render() {
//        drawColoredRect(vg, constraint.x, constraint.y + constraint.h / 2, constraint.w, constraint.h, lineColor);
        drawText(vg, constraint.x, constraint.y - 3, 15, min + "", "regular", UIColor.WHITE);
        drawText(vg, (constraint.x + constraint.w) - nobConstraint.w, constraint.y - 3, 15, max + "", "regular", UIColor.WHITE);
        drawColoredRoundedRect(vg, constraint.x, constraint.y + constraint.h / 2 - 2, constraint.w, 4, 5, lineColor);
        drawColoredRoundedRect(vg, nobConstraint.x, nobConstraint.y, nobConstraint.w, nobConstraint.h, 5, nobColor);
    }
}
