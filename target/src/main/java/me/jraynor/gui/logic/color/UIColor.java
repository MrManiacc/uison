package me.jraynor.gui.logic.color;

import lombok.Getter;
import me.jraynor.gui.logic.UIComponent;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.nanovg.NVGColor;

import java.util.HashMap;

public class UIColor extends UIComponent {
    @Getter
    private Vector4i color;
    @Getter
    private Vector4f glColor;
    @Getter
    private NVGColor vgColor = NVGColor.create();

    private static final HashMap<Vector4i, UIColor> cachedColors = new HashMap<>();
    public static final UIColor WHITE = rgb(255);
    public static final UIColor BLACK = rgb(0);
    public static final UIColor DARK_GRAY = rgb(64);
    public static final UIColor GRAY = rgb(140);
    public static final UIColor DARK_PURPLE = rgb(121, 66, 150);
    public static final UIColor LIGHT_PURPLE = rgb(206, 112, 224);
    public static final UIColor LIGHT_RED = rgb(227, 95, 113);
    public static final UIColor DARK_RED = rgb(199, 8, 33);

    private UIColor(Vector4i color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UIColor) {
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


    @Override
    protected void onUpdate() {
    }
}
