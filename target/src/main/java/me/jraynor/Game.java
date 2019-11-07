package me.jraynor;

import lombok.Getter;
import me.jraynor.bootstrap.IEngine;
import me.jraynor.bootstrap.Window;
import me.jraynor.gui.UIMaster;
import me.jraynor.misc.Camera;
import me.jraynor.pong.Pong;

public class Game implements IEngine {
    @Getter
    private static final Window win = new Window(1920, 1080, false, true, false, "UIBuilder");
    public static void main(String[] args) {
        win.start(new Game());
    }
    private static final boolean showUI = true;
    ///////////////////////////
    private static final boolean showPONG = true;
    private Pong pong;
    private Camera camera;


    @Override
    public void preInit() {
        if(showPONG){
            camera = new Camera(80, 0.1f, 10000);
            pong = new Pong(camera);
        }
    }

    @Override
    public void postInit() {
        if (showUI) {
            UIMaster.createUIMaster(win);
        }
        if(showPONG){
            pong.init();
        }
    }

    @Override
    public void render(double delta) {
        if(showPONG)
            pong.render((float) delta);
    }

    public void renderUI(double delta) {
        if (showUI)
            UIMaster.update(win);
    }

    @Override
    public void update(double tick) {
        if(showPONG)
            pong.update((float) tick);
    }
}
