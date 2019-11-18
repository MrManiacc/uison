package me.jraynor.uison.controller;

import me.jraynor.bootstrap.Window;
import me.jraynor.uison.UIMaster;
import me.jraynor.uison.controller.events.*;
import me.jraynor.uison.elements.*;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.constraint.Constraints;
import me.jraynor.uison.logic.constraint.UIConstraint;
import me.jraynor.uison.parser.UIType;
import org.joml.Vector2f;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static me.jraynor.uison.logic.constraint.Constraints.ManualConstraint.NO_OVERRIDE;
import static me.jraynor.uison.logic.constraint.Constraints.StickyConstraint;
import static me.jraynor.uison.logic.constraint.Constraints.StickyConstraint.FACE;
import static me.jraynor.uison.logic.constraint.Constraints.StickyConstraint.FACE.*;

public class DefaultController extends Controller {
    private Map<String, Vector2f> draggedOffsets = new HashMap<>();
    private Map<String, UIConstraint> leftTopSnappedElements = new HashMap<>();
    private Map<String, UIConstraint> leftBottomSnappedElements = new HashMap<>();
    private Map<String, UIConstraint> rightTopSnappedElements = new HashMap<>();
    private Map<String, UIConstraint> rightBottomSnappedElements = new HashMap<>();
    private Map<String, UIConstraint> nextConstraints = new HashMap<>();
    private DecimalFormat df = new DecimalFormat("0.0");
    private boolean snap = true;
    protected Window window;


    private boolean mainWindow = false;

    public DefaultController(Window window, boolean snap) {
        this.window = window;
        this.snap = snap;
    }


    @Event(tag = UIType.UITEXTBOX, action = "mouse_enter")
    public void mouseEnterTextBox(MouseEvent e) {
        applyHover(true, e);
    }

    @Event(tag = UIType.UITEXTBOX, action = "mouse_exit")
    public void mouseExitTextBox(MouseEvent e) {
        applyHover(false, e);
    }

    @Event(tag = UIType.UITEXTBOX, action = "key_pressed")
    public void keyPressTextBox(KeyEvent e) {
        e.component().setText(e.getText());
    }


    @Event(tag = UIType.UITEXTBOX, action = "focus")
    public void checkTextBox(FocusEvent mouseEvent) {
        UITextBox textBox = (UITextBox) mouseEvent.component();
        if (mouseEvent.isFocused()) {
            if (textBox.getText().equals(textBox.getPlaceholderText()))
                mouseEvent.component().setText("");
        } else {
            if (textBox.getText().isBlank())
                mouseEvent.component().setText(((UITextBox) mouseEvent.component()).getPlaceholderText());
        }
    }

    @Event(tag = UIType.UISLIDER, action = "slider_value")
    public void printTotal(SliderEvent sliderEvent) {
        if (sliderEvent.component() instanceof UISlider) {
            if (sliderEvent.component().pair().isPresent()) {
                UIComponent o = sliderEvent.component().pair().get();
                if (o instanceof UILabel) {
                    UILabel label = (UILabel) o;
                    label.setText(df.format(sliderEvent.getVal()));
                }
            }
        }
    }

    private void clearSnapped(String id) {
        leftTopSnappedElements.remove(id);
        leftBottomSnappedElements.remove(id);
        rightBottomSnappedElements.remove(id);
        rightTopSnappedElements.remove(id);
    }


    @Event(tag = UIType.UIBAR, action = "mouse_press")
    public void barMousePress(MouseEvent e) {
        if (snap) {
            UIBar bar = (UIBar) e.component();
            float xOffset = (float) (e.mx - bar.getLocalConstraint().x);
            float yOffset = (float) (e.my - bar.getLocalConstraint().y);
            if (draggedOffsets.containsKey(e.id()))
                draggedOffsets.get(e.id()).set(xOffset, yOffset);
            else
                draggedOffsets.put(e.id(), new Vector2f(xOffset, yOffset));
            clearSnapped(e.id());
            remove(bar.getParentConstraint());
        }
    }

    private void remove(UIConstraint constraint) {
        if (nextConstraints.containsKey("TOP_LEFT")) {
            if (nextConstraints.get("TOP_LEFT").getIdentifier().equals(constraint.getIdentifier()))
                nextConstraints.remove("TOP_LEFT");
        }
        if (nextConstraints.containsKey("TOP_RIGHT")) {
            if (nextConstraints.get("TOP_RIGHT").getIdentifier().equals(constraint.getIdentifier()))
                nextConstraints.remove("TOP_RIGHT");
        }
        if (nextConstraints.containsKey("BOTTOM_LEFT")) {
            if (nextConstraints.get("BOTTOM_LEFT").getIdentifier().equals(constraint.getIdentifier()))
                nextConstraints.remove("BOTTOM_LEFT");
        }
        if (nextConstraints.containsKey("BOTTOM_RIGHT")) {
            if (nextConstraints.get("BOTTOM_RIGHT").getIdentifier().equals(constraint.getIdentifier()))
                nextConstraints.remove("BOTTOM_RIGHT");
        }
    }

    @Event(tag = UIType.UIBAR, action = "mouse_down")
    public void barMouseDown(MouseEvent e) {
        if (snap) {
            Vector2f offset = draggedOffsets.get(e.id());
            UIBar bar = (UIBar) e.component();
            UIConstraint parentConstraint = bar.getParentConstraint();

            if (parentConstraint.getOverride() == null)
                parentConstraint.setOverride(new Constraints.ManualConstraint(0, 0, 0, 0));
            parentConstraint.getOverride().setX((float) e.mx - offset.x);
            parentConstraint.getOverride().setY((float) e.my - offset.y);


            FACE[] faces = getSnappers(parentConstraint);
            switch (faces[0]) {
                case LEFT:
                    UIConstraint constraint = new UIConstraint();

                    constraint.setOverride(new Constraints.ManualConstraint(NO_OVERRIDE, NO_OVERRIDE, NO_OVERRIDE, parentConstraint.h));
                    constraint.setXConst(new Constraints.StickyConstraint(LEFT));
                    switch (faces[1]) {
                        case TOP:
                            if (nextConstraints.containsKey("TOP_LEFT")) {
                                constraint.setYConst(new Constraints.StickyConstraint(BOTTOM).setRelativeConstraint(nextConstraints.get("TOP_LEFT")));
                            } else
                                constraint.setYConst(new Constraints.StickyConstraint(faces[1]));
                            break;
                        case BOTTOM:
                            if (nextConstraints.containsKey("BOTTOM_LEFT")) {
                                constraint.update();
                                constraint.setYConst(new Constraints.StickyConstraint(TOP, new Constraints.PixelConstraint(-constraint.h)).setRelativeConstraint(nextConstraints.get("BOTTOM_LEFT")));
                            } else
                                constraint.setYConst(new Constraints.StickyConstraint(faces[1]));
                            break;
                    }
                    constraint.setWConst(new Constraints.RelativeConstraint(0.20f));
                    UIMaster.showDisplayBlock(constraint);
                    break;
                case RIGHT:
                    constraint = new UIConstraint();
                    constraint.setOverride(new Constraints.ManualConstraint(NO_OVERRIDE, NO_OVERRIDE, NO_OVERRIDE, parentConstraint.h));
                    constraint.setXConst(new Constraints.StickyConstraint(RIGHT));
                    switch (faces[1]) {
                        case TOP:
                            if (nextConstraints.containsKey("TOP_RIGHT"))
                                constraint.setYConst(new Constraints.StickyConstraint(BOTTOM).setRelativeConstraint(nextConstraints.get("TOP_RIGHT")));
                            else
                                constraint.setYConst(new Constraints.StickyConstraint(faces[1]));
                            break;
                        case BOTTOM:
                            if (nextConstraints.containsKey("BOTTOM_RIGHT")) {
                                constraint.update();
                                constraint.setYConst(new Constraints.StickyConstraint(TOP, new Constraints.PixelConstraint(-constraint.h)).setRelativeConstraint(nextConstraints.get("BOTTOM_RIGHT")));
                            } else
                                constraint.setYConst(new Constraints.StickyConstraint(faces[1]));
                            break;
                    }
                    constraint.setWConst(new Constraints.RelativeConstraint(0.20f));
                    UIMaster.showDisplayBlock(constraint);
                    break;
                default:
                    UIMaster.hideDisplayBlock();
                    break;
            }
        }
    }

    @Event(tag = UIType.UIBAR, action = "mouse_up")
    public void barMouseUP(MouseEvent e) {
        if (snap) {
            UIBar bar = (UIBar) e.component();
            UIConstraint parentConstraint = bar.getParentConstraint();
            checkSnap(e.id(), parentConstraint);
        }
    }

    private FACE[] getSnappers(UIConstraint parent) {
        FACE snapTo = NONE;
        FACE topBottomSnap = TOP;
        if (parent.x <= 0)
            snapTo = LEFT;
        else if (parent.x + parent.w >= Window.INSTANCE.getWidth())
            snapTo = RIGHT;
        else if (parent.y <= 0)
            snapTo = TOP;
        else if (parent.y + parent.h <= Window.INSTANCE.getHeight())
            snapTo = BOTTOM;
        if (parent.y + (parent.h / 2.0f) >= Window.INSTANCE.getHeight() / 2.0f)
            topBottomSnap = BOTTOM;

        return new FACE[]{snapTo, topBottomSnap};
    }


    private void checkSnap(String id, UIConstraint parent) {
        FACE[] snappers = getSnappers(parent);

        switch (snappers[0]) {
            case LEFT:
                parent.setOverride(null);
                parent.setXConst(new Constraints.StickyConstraint(LEFT));
                if (snappers[1].equals(TOP)) {
                    if (nextConstraints.containsKey("TOP_LEFT")) {
                        parent.setYConst(new Constraints.StickyConstraint(BOTTOM).setRelativeConstraint(nextConstraints.get("TOP_LEFT")));
                    } else
                        parent.setYConst(new Constraints.StickyConstraint(TOP));
                } else if (snappers[1].equals(BOTTOM)) {
                    if (nextConstraints.containsKey("BOTTOM_LEFT")) {
                        float y = parent.h;
                        if (parent.getOverride() != null)
                            if (parent.getParent() instanceof UIFlex) {
                                y = parent.getOverride().getHOverride().get();
                            }
                        parent.setYConst(new Constraints.StickyConstraint(TOP, new Constraints.PixelConstraint(-y)).setRelativeConstraint(nextConstraints.get("BOTTOM_LEFT")));
                    } else
                        parent.setYConst(new Constraints.StickyConstraint(BOTTOM));
                }
                if (snappers[1].equals(TOP))
                    leftTopSnappedElements.put(id, parent);
                else if (snappers[1].equals(BOTTOM))
                    leftBottomSnappedElements.put(id, parent);
                break;
            case RIGHT:
                parent.getOverride().setXOverride(Optional.empty());
                parent.getOverride().setYOverride(Optional.empty());
                parent.setXConst(new Constraints.StickyConstraint(RIGHT));
                if (snappers[1].equals(TOP)) {
                    if (nextConstraints.containsKey("TOP_RIGHT")) {
                        parent.setYConst(new Constraints.StickyConstraint(BOTTOM).setRelativeConstraint(nextConstraints.get("TOP_RIGHT")));
                    } else
                        parent.setYConst(new Constraints.StickyConstraint(TOP));
                } else if (snappers[1].equals(BOTTOM)) {
                    if (nextConstraints.containsKey("BOTTOM_RIGHT")) {
                        parent.setYConst(new Constraints.StickyConstraint(TOP, new Constraints.PixelConstraint(-parent.h)).setRelativeConstraint(nextConstraints.get("BOTTOM_RIGHT")));
                    } else
                        parent.setYConst(new Constraints.StickyConstraint(BOTTOM));
                }
                if (snappers[1].equals(TOP))
                    rightTopSnappedElements.put(id, parent);
                else if (snappers[1].equals(BOTTOM))
                    rightBottomSnappedElements.put(id, parent);
                break;
        }
        UIMaster.hideDisplayBlock();
    }


    @Override
    protected void onReady() {
    }

    @Override
    public void update() {
        UIConstraint nextTopLeft = getNextConstraint(leftTopSnappedElements);
        if (nextTopLeft != null)
            nextConstraints.put("TOP_LEFT", nextTopLeft);
        UIConstraint nextBottomLeft = getNextConstraint(leftBottomSnappedElements);
        if (nextBottomLeft != null)
            nextConstraints.put("BOTTOM_LEFT", nextBottomLeft);
        UIConstraint nextTopRight = getNextConstraint(rightTopSnappedElements);
        if (nextTopRight != null)
            nextConstraints.put("TOP_RIGHT", nextTopRight);
        UIConstraint nextBottomRight = getNextConstraint(rightBottomSnappedElements);
        if (nextBottomRight != null)
            nextConstraints.put("BOTTOM_RIGHT", nextBottomRight);
    }


    private UIConstraint getNextConstraint(Map<String, UIConstraint> list) {
        float y = -1;
        float y2 = Window.INSTANCE.getHeight();

        String yID = "";
        for (String id : list.keySet()) {
            UIConstraint constraint = list.get(id);
            StickyConstraint stickyY = (StickyConstraint) constraint.getYConstraint();
            if (stickyY.getFace().equals(TOP)) {
                if (constraint.y + constraint.h > y) {
                    y = constraint.y + constraint.h;
                    yID = id;
                }
            } else if (stickyY.getFace().equals(BOTTOM)) {
                if (constraint.y < y2) {
                    y = constraint.y;
                    yID = id;
                }
            }
        }
        if (y != -1 && !yID.isEmpty())
            return list.get(yID);
        return null;
    }

    private void applyHover(boolean enter, UIEvent event) {
        if (event.component() instanceof UITextBox) {
            UITextBox textBox = (UITextBox) event.component();
            if (enter) {
                textBox.setHover(true);
            } else {
                textBox.setHover(false);
            }
        }
    }
}
