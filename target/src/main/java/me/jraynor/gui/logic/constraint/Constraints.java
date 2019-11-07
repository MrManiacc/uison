package me.jraynor.gui.logic.constraint;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.gui.logic.UIComponent;

import java.util.Optional;

public class Constraints {
    interface Constraint {
    }

    public static class AspectConstraint implements Constraint {
        private float aspect;

        public AspectConstraint(float aspect) {
            this.aspect = aspect;
        }

        public float getAspect() {
            return aspect;
        }
    }


    public static class StickyConstraint implements Constraint {
        public enum FACE {
            LEFT, RIGHT, TOP, BOTTOM;
        }

        FACE face;
        private Optional<Constraint> pixelConstraint;
        private Optional<UIConstraint> relativeConstraint;

        public StickyConstraint(FACE face) {
            this.face = face;
            this.pixelConstraint = Optional.empty();
            this.relativeConstraint = Optional.empty();
        }

        public StickyConstraint(FACE face, Constraint pixelConstraint) {
            this.face = face;
            this.pixelConstraint = Optional.of(pixelConstraint);
            this.relativeConstraint = Optional.empty();
        }


        public StickyConstraint setRelativeConstraint(UIComponent relative) {
            if (relative instanceof UIConstraint) {
                this.relativeConstraint = Optional.of((UIConstraint) relative);
            } else {
                if (relative.hasComponent(UIConstraint.class)) {
                    this.relativeConstraint = Optional.of((UIConstraint) relative.getComponent(UIConstraint.class));
                }
            }
            return this;
        }

        public boolean hasRelativeConstraint() {
            return relativeConstraint.isPresent();
        }

        public UIConstraint getRelativeConstraint() {
            return relativeConstraint.get();
        }

        /**
         * checks to see if the pixel constraint is present or not
         *
         * @return the PixelConstraint's status
         */
        public boolean hasPixelConstraint() {
            return pixelConstraint.isPresent();
        }

        /**
         * If the pixel constraint is present, it will be used as an offset
         * for the sticky face. For example if the FACE was TOP, and the
         * pixel constraint was 20 pixels, then the current UIComponent's
         * Y position would the the parent's Y position plus 20 pixels.
         *
         * @return the pixel constraint
         */
        public Constraint getPixelConstraint() {
            return pixelConstraint.get();
        }

        /**
         * The face that the constraint should stick too.
         * if the parent's Y position was 80px, and the
         * FACE was selected as top, then the current UIComponent's
         * top position would be 80px as well
         *
         * @return the face to stick too
         */
        public FACE getFace() {
            return face;
        }
    }

    public static class ManualConstraint implements Constraint {
        @Getter
        Optional<Float> xOverride = Optional.empty();
        @Getter
        Optional<Float> xOffOverride = Optional.empty();
        @Getter
        Optional<Float> yOverride = Optional.empty();
        @Getter
        Optional<Float> yOffOverride = Optional.empty();
        @Getter
        Optional<Float> wOverride = Optional.empty();
        @Getter
        Optional<Float> wOffOverride = Optional.empty();
        @Getter
        Optional<Float> hOverride = Optional.empty();
        @Getter
        Optional<Float> hOffOverride = Optional.empty();

        public static int NO_OVERRIDE = -69;

        public ManualConstraint(float x, float y, float w, float h) {
            if (x != NO_OVERRIDE)
                xOverride = Optional.of(x);
            if (y != NO_OVERRIDE)
                yOverride = Optional.of(y);
            if (w != NO_OVERRIDE)
                wOverride = Optional.of(w);
            if (h != NO_OVERRIDE)
                hOverride = Optional.of(h);
        }

        public ManualConstraint(float x, float y, float w, float h, float xOff, float yOff, float wOff, float hOff) {
            this(x, y, w, h);
            if (xOff != NO_OVERRIDE)
                xOffOverride = Optional.of(xOff);
            if (yOff != NO_OVERRIDE)
                yOffOverride = Optional.of(yOff);
            if (wOff != NO_OVERRIDE)
                wOffOverride = Optional.of(wOff);
            if (hOff != NO_OVERRIDE)
                hOffOverride = Optional.of(hOff);
        }

        public ManualConstraint setX(float x) {
            this.xOverride = Optional.of(x);
            if (x == NO_OVERRIDE)
                this.xOverride = Optional.empty();
            return this;
        }

        public ManualConstraint setY(float y) {
            this.yOverride = Optional.of(y);
            if (y == NO_OVERRIDE)
                this.yOverride = Optional.empty();
            return this;
        }

        public ManualConstraint setW(float w) {
            this.wOverride = Optional.of(w);
            if (w == NO_OVERRIDE)
                this.wOverride = Optional.empty();
            return this;
        }

        public ManualConstraint setH(float h) {
            this.hOverride = Optional.of(h);
            if (h == NO_OVERRIDE)
                this.hOverride = Optional.empty();
            return this;
        }

        public ManualConstraint setXOff(float x) {
            this.xOffOverride = Optional.of(x);
            if (x == NO_OVERRIDE)
                this.xOffOverride = Optional.empty();
            return this;
        }

        public ManualConstraint setYOff(float y) {
            this.yOffOverride = Optional.of(y);
            if (y == NO_OVERRIDE)
                this.yOffOverride = Optional.empty();
            return this;
        }

        public ManualConstraint setWOff(float w) {
            this.wOffOverride = Optional.of(w);
            if (w == NO_OVERRIDE)
                this.wOffOverride = Optional.empty();
            return this;
        }

        public ManualConstraint setHOff(float h) {
            this.hOffOverride = Optional.of(h);
            if (h == NO_OVERRIDE)
                this.hOffOverride = Optional.empty();
            return this;
        }

        public String toString(){
            StringBuilder sb = new StringBuilder();
            xOverride.ifPresent(x-> sb.append("X: " + x + ", "));
            yOverride.ifPresent(x-> sb.append("Y: " + x + ", "));
            wOverride.ifPresent(x-> sb.append("W: " + x + ", "));
            hOverride.ifPresent(x-> sb.append("H: " + x + ", "));
            xOffOverride.ifPresent(x-> sb.append("WOff: " + x + ", "));
            yOffOverride.ifPresent(x-> sb.append("YOff: " + x + ", "));
            wOffOverride.ifPresent(x-> sb.append("WOff: " + x + ", "));
            hOffOverride.ifPresent(x-> sb.append("HOff: " + x + ", "));
            return sb.toString().substring(0, sb.toString().length() - 2);
        }

    }

    public static class PixelConstraint implements Constraint {
        @Setter
        float pixel;

        public PixelConstraint(float pixel) {
            this.pixel = pixel;
        }

        /**
         * Determines the pixel position of the constraint,
         * this is still relative to the parent. 20 pixels could mean it's absolute
         * position is actually 120, if the parent has 100px of space from the edge of the root
         *
         * @return the pixel size
         */
        public float getPixel() {
            return pixel;
        }
    }

    public static class RelativeConstraint implements Constraint {
        @Setter
        float percent;
        private Optional<PixelConstraint> offsetConstraint;
        private Optional<PixelConstraint> minSizeConstraint = Optional.empty();
        private Optional<PixelConstraint> maxSizeConstraint = Optional.empty();
        private Optional<UIConstraint> relativeConstraint = Optional.empty();

        @Getter
        boolean center = false;

        public RelativeConstraint(float percent) {
            this.percent = percent;
            this.offsetConstraint = Optional.empty();
            this.minSizeConstraint = Optional.empty();
            this.maxSizeConstraint = Optional.empty();
        }

        public RelativeConstraint(float percent, boolean center) {
            this.percent = percent;
            this.offsetConstraint = Optional.empty();
            this.minSizeConstraint = Optional.empty();
            this.maxSizeConstraint = Optional.empty();
            this.center = center;
        }

        public RelativeConstraint() {
            this(1.0f);//default constraint is 100% of the parent
        }

        public RelativeConstraint(float percent, PixelConstraint offsetConstraint) {
            this.percent = percent;
            this.offsetConstraint = Optional.of(offsetConstraint);
            this.minSizeConstraint = Optional.empty();
            this.maxSizeConstraint = Optional.empty();
        }


        public RelativeConstraint(float percent, PixelConstraint minSizeConstraint, PixelConstraint maxSizeConstraint) {
            this.percent = percent;
            if (minSizeConstraint != null)
                this.minSizeConstraint = Optional.of(minSizeConstraint);
            if (maxSizeConstraint != null)
                this.maxSizeConstraint = Optional.of(maxSizeConstraint);
            this.offsetConstraint = Optional.empty();
        }

        public RelativeConstraint(float percent, PixelConstraint offsetConstraint, PixelConstraint minSizeConstraint, PixelConstraint maxSizeConstraint) {
            this.percent = percent;
            this.offsetConstraint = Optional.of(offsetConstraint);
            this.minSizeConstraint = Optional.of(minSizeConstraint);
            this.maxSizeConstraint = Optional.of(maxSizeConstraint);
        }

        public RelativeConstraint setParentConstraint(UIComponent parent) {
            if (parent instanceof UIConstraint)
                this.relativeConstraint = Optional.of((UIConstraint) parent);
            else if (parent.hasComponent(UIConstraint.class)) {
                this.relativeConstraint = Optional.of((UIConstraint) parent.getComponent(UIConstraint.class));
            }
            return this;
        }

        public boolean hasParentConstraint() {
            return relativeConstraint.isPresent();
        }

        public UIConstraint getParentConstraint() {
            return relativeConstraint.get();
        }


        /**
         * checks to see if the pixel constraint is present or not
         *
         * @return the PixelConstraint's status
         */
        public boolean hasOffsetConstraint() {
            return offsetConstraint.isPresent();
        }


        /**
         * checks to see if the min size constraint is present or not
         *
         * @return the PixelConstraint's status
         */
        public boolean hasMaxSizeConstraint() {
            return maxSizeConstraint.isPresent();
        }


        /**
         * checks to see if the min size constraint is present or not
         *
         * @return the PixelConstraint's status
         */
        public boolean hasMinSizeConstraint() {
            return minSizeConstraint.isPresent();
        }

        /**
         * Gets the minimum allowed sized for the constraint
         *
         * @return the minium value
         */
        public PixelConstraint getMinSizeConstraint() {
            return minSizeConstraint.get();
        }

        /**
         * Gets the max allowed size for the constraint
         *
         * @return the max value
         */
        public PixelConstraint getMaxSizeConstraint() {
            return maxSizeConstraint.get();
        }


        /**
         * Gets the pixel constraint, this is used as a sort of padding.
         * if the relative constraint is 0.1f, then whatever the
         * actual calculated percent of the parent is added to the PixelConstraint
         * when the value of the pixel constraint is negative, then the size
         * will be subtracted by the value, or added too if it's positive
         *
         * @return the pixel constraint
         */
        public PixelConstraint getOffsetConstraint() {
            return offsetConstraint.get();
        }


        /**
         * The percent of the parent that this should occupy,
         * for a width if the percent value was 0.1f, then
         * the width would be 10% of the parent.
         * Ex. say the parent is the main container and has a width of 1920px
         * then if the percent value were 0.23f, then the width of the UIConstraint
         * would be 442px
         *
         * @return the percent relative to the parent
         */
        public float getPercent() {
            return percent;
        }
    }


    public static class CenterConstraint implements Constraint {
        @Setter
        private int offset;

        private Optional<UIConstraint> parentConstraint = Optional.empty();

        public CenterConstraint(int offset) {
            this.offset = offset;
        }

        public CenterConstraint() {
            this.offset = 0;
        }

        public CenterConstraint setParentConstraint(UIComponent parent) {
            if (parent instanceof UIConstraint)
                this.parentConstraint = Optional.of((UIConstraint) parent);
            else if (parent.hasComponent(UIConstraint.class)) {
                this.parentConstraint = Optional.of((UIConstraint) parent.getComponent(UIConstraint.class));
            }
            return this;
        }

        public boolean hasParentConstraint() {
            return parentConstraint.isPresent();
        }

        public UIConstraint getParentConstraint() {
            return parentConstraint.get();
        }

        /**
         * determines how far either left, right, up or down
         * the constraint will be relative to to the center of the parent
         *
         * @return the offset of the center constraint
         */
        public int getOffset() {
            return offset;
        }
    }

    public static class FlexConstraint implements Constraint {
        @Getter
        private float offset;

        public FlexConstraint(float offset) {
            this.offset = offset;
        }

    }
}
