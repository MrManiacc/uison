package me.jraynor.uison.logic.constraint;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.bootstrap.Window;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.misc.Style;
import org.joml.Vector4f;

import static me.jraynor.uison.logic.constraint.Constraints.*;
import static me.jraynor.uison.logic.constraint.Constraints.StickyConstraint.FACE.*;

public class UIConstraint extends UIComponent {
    private Constraint xConst;
    private Constraint yConst;
    private Constraint wConst;
    private Constraint hConst;
    private PixelConstraint minWConst;
    private PixelConstraint minHConst;
    private PixelConstraint maxWConst;
    private PixelConstraint maxHConst;
    @Getter
    @Setter
    private StickyConstraint.FACE snappedTo;
    @Getter
    private ManualConstraint override;

    @Getter
    private Constraint roundConstraint;

    //the actual pixel coordinates of the
    public float x, y, w, h;

    private float radius = -1;
    public float padLeft = -1, padRight = -1, padUp = -1, padDown = -1;


    public UIConstraint setOverride(ManualConstraint override) {
        this.override = override;
        return this;
    }

//    public static UIConstraint fill() {
//        return new UIConstraint()
//                .setYConst(new StickyConstraint(TOP))
//                .setXConst(new StickyConstraint(LEFT))
//                .setHConst(new RelativeConstraint())
//                .setWConst(new RelativeConstraint());
//    }

    public UIConstraint() {
    }

    public UIConstraint(UIConstraint other) {
        this.xConst = other.xConst;
        this.yConst = other.yConst;
        this.wConst = other.wConst;
        this.hConst = other.hConst;
        this.minHConst = other.minHConst;
        this.minWConst = other.minWConst;
        this.maxWConst = other.maxWConst;
        this.maxHConst = other.maxHConst;
        this.override = other.override;
        this.x = other.x;
        this.y = other.y;
        this.w = other.w;
        this.h = other.h;
        this.roundConstraint = other.roundConstraint;
    }

    @Override
    protected void onAdded() {
        super.onAdded();
    }

    @Override
    public void onStyle(Style style) {
        if (props.has("x"))
            this.xConst = props.Constraint("x");
        if (props.has("y"))
            this.yConst = props.Constraint("y");
        if (props.has("w"))
            this.wConst = props.Constraint("w");
        if (props.has("h"))
            this.hConst = props.Constraint("h");
    }

    public void setPadding(float padding) {
        this.padLeft = padding;
        this.padRight = padding;
        this.padUp = padding;
        this.padDown = padding;
    }

    public void setPadding(float up, float right, float down, float left) {
        this.padLeft = left;
        this.padRight = right;
        this.padUp = up;
        this.padDown = down;
    }

    public void setPadding(Vector4f padding) {
        this.padLeft = padding.x;
        this.padRight = padding.y;
        this.padUp = padding.z;
        this.padDown = padding.w;
    }


    public boolean isRounded() {
        return this.radius != -1;
    }

    public UIConstraint setXConst(Constraint xConst) {
        this.xConst = xConst;
        return this;
    }

    public UIConstraint setYConst(Constraint yConst) {
        this.yConst = yConst;
        return this;
    }

    public UIConstraint setMinWConst(PixelConstraint w) {
        this.minWConst = w;
        return this;
    }

    public UIConstraint setMinHConst(PixelConstraint h) {
        this.minHConst = h;
        return this;
    }

    public UIConstraint setMaxWConst(PixelConstraint w) {
        this.maxWConst = w;
        return this;
    }

    public UIConstraint setMaxHConst(PixelConstraint h) {
        this.maxHConst = h;
        return this;
    }

    public float getRound() {
        return radius;
    }

    public UIConstraint setWConst(Constraint wConst) {
        this.wConst = wConst;
        return this;
    }

    public UIConstraint setRound(Constraint round) {
        this.roundConstraint = round;
        return this;
    }


    public Constraint getXConstraint() {
        return xConst;
    }

    public Constraint getYConstraint() {
        return yConst;
    }

    public Constraint getWidthConstraint() {
        return wConst;
    }

    public Constraint getHeightConstraint() {
        return hConst;
    }

    public UIConstraint setHConst(Constraint hConst) {
        this.hConst = hConst;
        return this;
    }

    public float getxConst() {
        return x;
    }

    public float getyConst() {
        return y;
    }

    public float getwConst() {
        return w;
    }

    public float gethConst() {
        return h;
    }

    public UIConstraint(Constraint x, Constraint yConst, Constraint wConst, Constraint hConst) {
        this.xConst = x;
        this.yConst = yConst;
        this.wConst = wConst;
        this.hConst = hConst;
    }


    @Override
    protected void onUpdate() {
        if (hasParent()) {
            UIComponent parent = getParent().getParent();
            if (parent.hasComponent(UIConstraint.class)) {
                UIConstraint constraint = (UIConstraint) parent.getComponent(UIConstraint.class);
                if (constraint != null)
                    processConstraints(constraint);
            }
        } else
            processConstraints(Window.INSTANCE);

    }

    private float startingSize = -1;

    /**
     * This method is responsible for calculating the absolute positioning of the UIComponent
     * based upon the parent parentConstraints, and it's own constraints
     *
     * @param parentConstraints the parent's constraints
     */
    private void processConstraints(UIConstraint parentConstraints) {
        processSizeConstraints(parentConstraints.x, parentConstraints.y, parentConstraints.w, parentConstraints.h);
        processPosConstraints(parentConstraints.x, parentConstraints.y, parentConstraints.w, parentConstraints.h);
    }

    /**
     * This method is responsible for calculating the absolute positioning of the UIComponent
     * it's based upon the game window it's self, and should only be used for root elements
     */
    private void processConstraints(Window window) {
        processSizeConstraints(0, 0, window.getWidth(), window.getHeight());
        processPosConstraints(0, 0, window.getWidth(), window.getHeight());
    }

    public String toString() {
        return "X: " + this.x + ", Y: " + this.y + ", W: " + this.w + ", H: " + this.h;
    }

    private float lastSize = 0;

    private void processSizeConstraints(float parentX, float parentY, float parentWidth, float parentHeight) {
        if (roundConstraint != null) {
            if (roundConstraint instanceof PixelConstraint) {
                PixelConstraint pixelConst = (PixelConstraint) roundConstraint;
                this.radius = pixelConst.getPixel();
            }
        }
        if (wConst instanceof FlexConstraint) {
            FlexConstraint flexConstraint = (FlexConstraint) wConst;
            if (getParent() != null) {
                UIConstraint maxConstraint = getParent().getMaxX(this);
                if (maxConstraint != null) {
                    this.w = ((maxConstraint.x + maxConstraint.w) + flexConstraint.getOffset()) - 10;
                }
            }

        } else if (wConst instanceof PixelConstraint) {
            PixelConstraint widthConst = (PixelConstraint) wConst;
            this.w = widthConst.getPixel();
        } else if (wConst instanceof AspectConstraint) {
            AspectConstraint aspectConstraint = (AspectConstraint) wConst;
            //this will simply keep the height the size of the width * the aspect
            this.w = this.h * aspectConstraint.getAspect();
        } else if (wConst instanceof RelativeConstraint) {
            RelativeConstraint widthConst = (RelativeConstraint) wConst;
            //The width is relative to the parent,
            // so if the parent is the window, and the width is 1920
            // and the percent is 10%, then the absWidth would be 192
            if (!widthConst.hasRelativeConstraint())
                this.w = parentWidth * widthConst.getPercent();
            else
                this.w = widthConst.getRelativeConstraint().w * widthConst.getPercent();
            if (widthConst.hasOffsetConstraint())
                this.w += widthConst.getOffsetConstraint().getPixel();

            if (widthConst.hasMinSizeConstraint())
                if (this.w <= widthConst.getMinSizeConstraint().getPixel())
                    this.w = widthConst.getMinSizeConstraint().getPixel();

            if (widthConst.hasMaxSizeConstraint())
                if (this.w >= widthConst.getMaxSizeConstraint().getPixel())
                    this.w = widthConst.getMaxSizeConstraint().getPixel();
        }

        if (hConst instanceof PixelConstraint) {
            PixelConstraint heightConst = (PixelConstraint) hConst;
            this.h = heightConst.getPixel();
        } else if (hConst instanceof AspectConstraint) {
            AspectConstraint aspectConstraint = (AspectConstraint) hConst;
            //this will simply keep the height the size of the width * the aspect
            this.h = this.w * aspectConstraint.getAspect();
        } else if (hConst instanceof RelativeConstraint) {
            RelativeConstraint heightConst = (RelativeConstraint) hConst;
            //The width is relative to the parent,
            // so if the parent is the window, and the width is 1080
            // and the percent is 10%, then the absWidth would be 108

            if (yConst != null && yConst instanceof StickyConstraint) {
                StickyConstraint yConstraint = (StickyConstraint) yConst;
                if (yConstraint.hasRelativeConstraint()) {
                    parentHeight -= yConstraint.getRelativeConstraint().y + yConstraint.getRelativeConstraint().h;
                }
            }


            if (!heightConst.hasRelativeConstraint())
                this.h = parentHeight * heightConst.getPercent();
            else
                this.h = heightConst.getRelativeConstraint().h * heightConst.getPercent();
            if (heightConst.hasOffsetConstraint())
                this.h += heightConst.getOffsetConstraint().getPixel();

            if (heightConst.hasMinSizeConstraint())
                if (this.h <= heightConst.getMinSizeConstraint().getPixel())
                    this.h = heightConst.getMinSizeConstraint().getPixel();

            if (heightConst.hasMaxSizeConstraint())
                if (this.w >= heightConst.getMaxSizeConstraint().getPixel())
                    this.w = heightConst.getMaxSizeConstraint().getPixel();


        }

        if (minWConst != null && minWConst.pixel != -1) {
            if (this.w <= minWConst.getPixel())
                this.w = minWConst.getPixel();
        }
        if (minHConst != null && minHConst.pixel != -1) {
            if (this.h <= minHConst.getPixel())
                this.h = minHConst.getPixel();
        }

        if (maxWConst != null && maxWConst.pixel != -1) {
            if (this.w >= maxWConst.getPixel())
                this.w = maxWConst.getPixel();
        }
        if (maxHConst != null && maxHConst.pixel != -1) {
            if (this.h >= maxHConst.getPixel())
                this.h = maxHConst.getPixel();
        }

        if (override != null) {

            override.wOverride.ifPresent(wOver -> this.w = wOver);
            override.hOverride.ifPresent(hOver -> this.h = hOver);
            if ((override.wOffOverride.isPresent() || override.hOffOverride.isPresent()) && !offsetAdded) {
                override.wOffOverride.ifPresent(wOffOver -> this.w += wOffOver);
                override.hOffOverride.ifPresent(hOffOver -> this.h += hOffOver);
                offsetAdded = true;
            }
        }

        if (padLeft != -1) {
            this.w += padLeft;
        }
        if (padUp != -1) {
            this.h += padUp;
        }
        if (padRight != -1) {
            this.w += padRight;
        }
        if (padDown != -1)
            this.h += padDown;
    }

    private boolean offsetAdded = false;

    private void processPosConstraints(float parentX, float parentY, float parentWidth, float parentHeight) {
        if (yConst instanceof PixelConstraint) {
            PixelConstraint yConst = (PixelConstraint) this.yConst;
            this.y = yConst.pixel;
        } else if (yConst instanceof CenterConstraint) {
            CenterConstraint yConst = (CenterConstraint) this.yConst;
            // we first get the center of the screen, by dividing the width in 2.
            // Then we subtract half of the size of the UIComponent so it's centered relative to it's own size
            //Finally we offset the new position by the offset, default 0 so it will be centered,
            // but -10 would leave the center -10px away from the center
            if (!yConst.hasParentConstraint())
                this.y = (float) (parentY + (parentHeight / 2.0f) - (h / 2.0f) + yConst.getOffset());
            else
                this.y = (float) (yConst.getParentConstraint().x + ((yConst.getParentConstraint().h / 2.0f) - (h / 2.0f) + yConst.getOffset()));
            this.y = (float) (parentY + (parentHeight / 2.0f) - (h / 2.0f) + yConst.getOffset());
        } else if (yConst instanceof RelativeConstraint) {
            RelativeConstraint yConst = (RelativeConstraint) this.yConst;
            //We set the position of the Y to the percent * the window width is,
            //remembering to add the pixel offset if it's present
            if (!yConst.hasRelativeConstraint()) {
                this.y = parentY + parentHeight * yConst.percent;
            } else {
                this.y = yConst.getRelativeConstraint().y + yConst.getRelativeConstraint().h * yConst.percent;
            }
            if (yConst.hasOffsetConstraint())
                this.y += yConst.getOffsetConstraint().getPixel();
            if (yConst.center)
                this.y -= h / 2;
        } else if (yConst instanceof StickyConstraint) {
            StickyConstraint yConst = (StickyConstraint) this.yConst;
            if (yConst.face == TOP) {
                //if the face is left, we set the absX to 0, because the
                //left side of the of the window is always going to 0

                if (yConst.hasRelativeConstraint()) {
                    this.y = yConst.getRelativeConstraint().y;
                } else {
                    this.y = parentY;
                }

                if (yConst.hasPixelConstraint()) {
                    if (yConst.getPixelConstraint() instanceof PixelConstraint) {
                        PixelConstraint pixelConstraint = (PixelConstraint) yConst.getPixelConstraint();
                        this.y += pixelConstraint.getPixel();
                    } else if (yConst.getPixelConstraint() instanceof RelativeConstraint) {
                        RelativeConstraint relativeConstraint = (RelativeConstraint) yConst.getPixelConstraint();
                        this.y += parentHeight * relativeConstraint.getPercent();
                        if (relativeConstraint.center)
                            this.y -= h / 2;
                    }
                }
            } else if (yConst.face == StickyConstraint.FACE.BOTTOM) {
                //if we want to stick to the right side of the window,
                //then we have to take the width of the window to
                //get the right side, then we must subtract the width
                //of the current UIComponent so that it's the right side
                //sticking to it, no the left or it'd be off screen

                if (yConst.hasRelativeConstraint()) {
                    this.y = yConst.getRelativeConstraint().y + yConst.getRelativeConstraint().h;
                } else {
                    this.y = parentY + (parentHeight - h);
                }
                if (yConst.hasPixelConstraint()) {
                    if (yConst.getPixelConstraint() instanceof PixelConstraint) {
                        PixelConstraint pixelConstraint = (PixelConstraint) yConst.getPixelConstraint();
                        this.y -= pixelConstraint.getPixel();
                    } else if (yConst.getPixelConstraint() instanceof RelativeConstraint) {
                        RelativeConstraint relativeConstraint = (RelativeConstraint) yConst.getPixelConstraint();
                        this.y -= parentHeight * relativeConstraint.getPercent();
                    }
                }
            }


        }
        //Size must be processed first because the center constraint requires knowing the width
        if (xConst instanceof PixelConstraint) {
            PixelConstraint xConst = (PixelConstraint) this.xConst;
            this.x = xConst.pixel;
        } else if (xConst instanceof CenterConstraint) {
            CenterConstraint xConst = (CenterConstraint) this.xConst;
            // we first get the center of the screen, by dividing the width in 2.
            // Then we subtract half of the size of the UIComponent so it's centered relative to it's own size
            //Finally we offset the new position by the offset, default 0 so it will be centered,
            // but -10 would leave the center -10px away from the center
            if (!xConst.hasParentConstraint())
                this.x = (float) (parentX + ((parentWidth / 2.0f) - (w / 2.0f) + xConst.getOffset()));
            else
                this.x = (float) (xConst.getParentConstraint().x + ((xConst.getParentConstraint().w / 2.0f) - (w / 2.0f) + xConst.getOffset()));
        } else if (xConst instanceof RelativeConstraint) {
            RelativeConstraint xConst = (RelativeConstraint) this.xConst;
            //We set the position of the X to the percent * the window width is,
            //remembering to add the pixel offset if it's present

            if (!xConst.hasRelativeConstraint()) {
                this.x = parentY + parentHeight * xConst.percent;
            } else {
                this.x = xConst.getRelativeConstraint().x + xConst.getRelativeConstraint().w * xConst.percent;
            }
            if (xConst.hasOffsetConstraint())
                this.x += xConst.getOffsetConstraint().getPixel();

            if (xConst.center)
                this.x -= w / 2;
        } else if (xConst instanceof StickyConstraint) {
            StickyConstraint xConst = (StickyConstraint) this.xConst;
            if (xConst.face == LEFT) {
                //if the face is left, we set the absX to 0, because the
                //left side of the of the window is always going to 0
                if (xConst.hasRelativeConstraint()) {
                    this.x = xConst.getRelativeConstraint().x - this.w;
                } else {
                    this.x = parentX;
                }
                if (xConst.hasPixelConstraint()) {
                    if (xConst.getPixelConstraint() instanceof PixelConstraint) {
                        PixelConstraint pixelConstraint = (PixelConstraint) xConst.getPixelConstraint();
                        this.x += pixelConstraint.getPixel();
                    } else if (xConst.getPixelConstraint() instanceof RelativeConstraint) {
                        RelativeConstraint relativeConstraint = (RelativeConstraint) xConst.getPixelConstraint();
                        this.x += parentWidth * relativeConstraint.getPercent();
                        if (relativeConstraint.isCenter())
                            this.x -= w / 2;
                    }
                }
            } else if (xConst.face == StickyConstraint.FACE.RIGHT) {
                //if we want to stick to the right side of the window,
                //then we have to take the width of the window to
                //get the right side, then we must subtract the width
                //of the current UIComponent so that it's the right side
                //sticking to it, no the left or it'd be off screen
                if (xConst.hasRelativeConstraint()) {
                    this.x = xConst.getRelativeConstraint().x + xConst.getRelativeConstraint().w;
                } else {
                    this.x = parentX + (parentWidth - w);
                }
                if (xConst.hasPixelConstraint()) {
                    //we subtract the pixel offset if it's present
                    if (xConst.getPixelConstraint() instanceof PixelConstraint) {
                        PixelConstraint pixelConstraint = (PixelConstraint) xConst.getPixelConstraint();
                        this.x -= pixelConstraint.getPixel();
                    } else if (xConst.getPixelConstraint() instanceof RelativeConstraint) {
                        RelativeConstraint relativeConstraint = (Constraints.RelativeConstraint) xConst.getPixelConstraint();
                        this.x -= parentWidth * relativeConstraint.getPercent();
                    }
                }
            }
        }
        if (override != null) {
            override.xOverride.ifPresent(xOver -> this.x = xOver);
            override.yOverride.ifPresent(yOver -> this.y = yOver);
            override.xOffOverride.ifPresent(xOffOver -> this.x += xOffOver);
            override.yOffOverride.ifPresent(yOffOver -> this.y += yOffOver);
        }


        if (wConst instanceof StickyConstraint) {
            StickyConstraint widthConstraint = (StickyConstraint) wConst;
            if (widthConstraint.getFace().equals(RIGHT)) {

                UIConstraint parent = getParentConstraint();
                float startingSize = 0;
                if (parent != null) {
                    if (this.startValue == -1)
                        startValue = this.x;
                    startingSize = Math.abs(parent.x + parent.w);
                    if (startingSize < 0)
                        startingSize *= -1;
                    float leftSide = this.x;
                    this.w = startingSize - leftSide;
                    if (widthConstraint.hasPixelConstraint()) {
                        this.w -= ((PixelConstraint) widthConstraint.getPixelConstraint()).getPixel();
                    }
                }

            }
        }
    }

    private float startValue = -1;

}

