package me.jraynor.gui.logic.constraint;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.gui.logic.UIComponent;

import static me.jraynor.gui.logic.constraint.Constraints.Constraint;
import static me.jraynor.gui.logic.constraint.Constraints.RelativeConstraint;

public class UIScroll extends UIComponent {
    @Setter
    private Constraint breakWidth;
    @Setter
    private Constraint breakHeight;
    private UIConstraint parentConstraint;
    private UIConstraint localConstraint;

    @Getter
    private float maxWidth;
    @Getter
    private float maxHeight;

    private boolean scrollY = false, scrollX;

    @Override
    protected void onUpdate() {
        if (getParent() != null && getParentConstraint() != null)
            localConstraint = getParentConstraint();

        if (getParent() != null && getParent().getParent() != null && parentConstraint == null)
            parentConstraint = getParent().getParentConstraint();
        if (parentConstraint != null && localConstraint != null) {
            if (breakWidth != null) {
                if (breakWidth instanceof RelativeConstraint) {
                    RelativeConstraint constraint = (RelativeConstraint) breakWidth;
                    float parentWidth = parentConstraint.w;
                    this.maxWidth = parentWidth * constraint.getPercent();
                } else if (breakWidth instanceof Constraints.PixelConstraint) {
                    Constraints.PixelConstraint pixelConstraint = (Constraints.PixelConstraint) breakWidth;
                    this.maxWidth = pixelConstraint.getPixel();
                }
            }
            if (breakHeight != null) {
                if (breakHeight instanceof RelativeConstraint) {
                    RelativeConstraint constraint = (RelativeConstraint) breakHeight;
                    float parentHeight = parentConstraint.h;
                    this.maxHeight = parentHeight * constraint.getPercent();
                } else if (breakHeight instanceof Constraints.PixelConstraint) {
                    Constraints.PixelConstraint pixelConstraint = (Constraints.PixelConstraint) breakHeight;
                    this.maxHeight = pixelConstraint.getPixel();
                }
            }
            scrollY = (localConstraint.y + localConstraint.h >= parentConstraint.y + maxHeight);
            scrollX = (localConstraint.x + localConstraint.w >= parentConstraint.w + maxWidth);
        }
    }
}
