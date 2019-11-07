package me.jraynor.gui;

import lombok.Getter;
import me.jraynor.bootstrap.Window;
import me.jraynor.gui.logic.UIComponent;
import static me.jraynor.gui.logic.constraint.Constraints.*;

import me.jraynor.gui.logic.constraint.Constraints;
import me.jraynor.gui.logic.constraint.UIConstraint;


public class UIMaster {
    @Getter
    private static final UIComponent root = new UIComponent();
    private static final UIConstraint rootConstraints = new UIConstraint();

    public static void createUIMaster(Window window) {
        root.setVg(window.getVg());
        rootConstraints.setXConst(new PixelConstraint(0));
        rootConstraints.setYConst(new PixelConstraint(0));
        rootConstraints.setWConst(new PixelConstraint(window.getWidth()));
        rootConstraints.setHConst(new PixelConstraint(window.getHeight()));
        root.add(rootConstraints);
    }

    /**
     * Called every frame. It will update the root constraints based upon
     * when the window is resized from inside the window class
     *
     * @param window the main window
     */
    public static void update(Window window) {
        root.update();
        if (window.isResized()) {
            PixelConstraint widthConstraint = (PixelConstraint) rootConstraints.getWidthConstraint();
            PixelConstraint heightConstraint = (PixelConstraint) rootConstraints.getHeightConstraint();
            widthConstraint.setPixel(window.getWidth());
            heightConstraint.setPixel(window.getHeight());
            window.setResized(false);
        }
    }


}
