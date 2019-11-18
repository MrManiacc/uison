package me.jraynor.uison.logic.color;

import lombok.Getter;
import me.jraynor.bootstrap.Window;
import org.lwjgl.nanovg.NVGPaint;

import java.io.File;

import static org.lwjgl.nanovg.NanoVG.*;

public class UIImage {
    private final String ROOT = "src/main/resources/textures/";
    private int handle;
    private String path;
    private int flags;
    @Getter
    private NVGPaint paint;
    @Getter
    private float width = 0, height = 0;
    @Getter
    private float angle = 0, alpha = 1;
    private long vg;
    private boolean loaded = false;

    public UIImage(String imageName, int flags) {
        this.path = new File(ROOT + imageName).getAbsolutePath();
        this.flags = flags;
    }


    public UIImage(String imageName, float width, float height, int flags) {
        this.path = new File(ROOT + imageName).getAbsolutePath();
        this.width = width;
        this.height = height;
        this.flags = flags;
    }

    public UIImage(String imageName, float width, float height, float angle, int flags) {
        this.path = new File(ROOT + imageName).getAbsolutePath();
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.flags = flags;
    }


    /**
     * Load the image, this should be done after the nvg context is created
     */
    public void load(long vg) {
        loaded = true;
        this.vg = vg;
        nvgSave(vg);
        this.handle = nvgCreateImage(vg, path, flags);
        if (width == 0 || height == 0) {
            int[] width = new int[1];
            int[] height = new int[1];
            nvgImageSize(vg, handle, width, height);
            this.width = width[0];
            this.height = height[0];
        }
        this.paint = NVGPaint.create();
        nvgImagePattern(vg, 0, 0, width, height, angle, handle, alpha, paint);
        nvgRestore(vg);
    }

    public void resize(float width, float height) {
        if (this.width != width && this.height != height) {
            this.width = width;
            this.height = height;
            if (loaded)
                nvgImagePattern(vg, 0, 0, width, height, angle, handle, alpha, paint);
        }
    }

    public void rotate(float angle) {
        this.angle = angle;
        if (loaded)
            nvgImagePattern(vg, 0, 0, width, height, angle, handle, alpha, paint);
    }

    public void setOpacity(float opacity) {
        this.alpha = opacity;
        if (loaded)
            nvgImagePattern(vg, 0, 0, width, height, angle, handle, alpha, paint);
    }


}
