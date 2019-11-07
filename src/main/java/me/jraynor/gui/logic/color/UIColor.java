package me.jraynor.gui.logic.color;

import lombok.Getter;
import me.jraynor.gui.logic.UIComponent;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.nanovg.NVGColor;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class UIColor extends UIComponent {
    @Getter
    private Vector4i color;
    @Getter
    private Vector4f glColor;
    @Getter
    private NVGColor vgColor = NVGColor.create();

    private static final HashMap<Vector4i, UIColor> cachedColors = new HashMap<>();
    public static final UIColor TRANSPARENT = rgba(0, 0, 0, 0);
    public static final UIColor WHITE = rgb(255);
    public static final UIColor BLACK = rgb(0);
    public static final UIColor DARK_GRAY = rgb(64);
    public static final UIColor GRAY = rgb(140);
    public static final UIColor DARK_PURPLE = rgb(121, 66, 150);
    public static final UIColor LIGHT_PURPLE = rgb(206, 112, 224);
    public static final UIColor LIGHT_RED = rgb(227, 95, 113);
    public static final UIColor DARK_RED = rgb(199, 8, 33);
    public final static HashMap<String, UIColor> colors = new HashMap<>();

    static {
        colors.put("WHITE", WHITE);
        colors.put("BLACK", BLACK);
        colors.put("DARK_GRAY", DARK_GRAY);
        colors.put("GRAY", GRAY);
        colors.put("DARK_PURPLE", DARK_PURPLE);
        colors.put("LIGHT_PURPLE", LIGHT_PURPLE);
        colors.put("LIGHT_RED", LIGHT_RED);
        colors.put("DARK_RED", DARK_RED);
        colors.put("TRANSPARENT", TRANSPARENT);
    }

    public static UIColor alpha(UIColor color, int alpha) {
        color.color.w = alpha;
        color.updateNVG();
        return color;
    }


    public static UIColor alpha(UIColor color, float alpha) {
        color.color.w = (int) (255 * alpha);
        color.updateNVG();
        return color;
    }

    public static UIColor copy(UIColor color) {
        Vector4i c = new Vector4i(color.color);
        return rgba(c.x, c.y, c.z, c.w, false);
    }

    private UIColor(Vector4i color) {
        this.color = color;
    }

    public static UIColor hex(String clr) {
        Color color = Color.decode(clr);
        return rgba(color.getRed(), color.getBlue(), color.getGreen(), color.getAlpha());
    }

    public static UIColor random() {
        Random random = new Random();
        return rgba(random.nextInt(255), random.nextInt(255), random.nextInt(255), random.nextInt(255 / 2) + 255 / 2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UIColor) {
            UIColor o = (UIColor) obj;
            return o.color.equals(this.color);
        }
        return super.equals(obj);
    }

    private void updateNVG() {
        vgColor.r(color.x / 255.0f);
        vgColor.g(color.y / 255.0f);
        vgColor.b(color.z / 255.0f);
        vgColor.a(color.w / 255.0f);
    }


    public static UIColor rgb(int rgb) {
        return rgb(rgb, rgb, rgb);
    }

    public static UIColor rgba(int rgba) {
        return rgba(rgba, rgba, rgba, rgba);
    }


    public static UIColor rgb(int r, int g, int b) {
        return rgb(new Vector3i(r, g, b));
    }

    public static UIColor rgba(int r, int g, int b, int a) {
        return rgba(new Vector4i(r, g, b, a));
    }

    public static UIColor rgba(int r, int g, int b, int a, boolean cache) {
        return rgba(new Vector4i(r, g, b, a), cache);
    }

    public static UIColor rgb(Vector3i rgb) {
        return rgba(new Vector4i(rgb, 255));
    }

    public static UIColor rgba(Vector4i rgba) {
        UIColor colors;
        if (cachedColors.containsKey(rgba))
            colors = cachedColors.get(rgba);
        else {
            colors = new UIColor(rgba);
            cachedColors.put(rgba, colors);
        }
        colors.glColor = new Vector4f(rgba.x / 255.0f, rgba.y / 255.0f, rgba.z / 255.0f, rgba.w / 255.0f);

        colors.updateNVG();
        return colors;
    }

    public static UIColor rgba(Vector4i rgba, boolean cache) {
        UIColor colors;
        if (cachedColors.containsKey(rgba) && cache)
            colors = cachedColors.get(rgba);
        else {
            colors = new UIColor(rgba);
            cachedColors.put(rgba, colors);
        }
        colors.glColor = new Vector4f(rgba.x / 255.0f, rgba.y / 255.0f, rgba.z / 255.0f, rgba.w / 255.0f);

        colors.updateNVG();
        return colors;
    }

    @Override
    public String toString() {
        return "{R: " + color.x + ", G: " + color.y + ", B: " + color.z + ", A: " + color.w + "}";
    }
}
