package me.jraynor.pong;

import me.jraynor.gui.UIMaster;
import me.jraynor.misc.Camera;
import me.jraynor.pong.entities.Player;
import me.jraynor.pong.physics.PhysicalWorld;
import me.jraynor.pong.render.PongManager;
import me.jraynor.pong.render.PongUI;

public class Pong {
    private Player player;
    public static  PongManager pongManager;
    public static PhysicalWorld physicalWorld;
    public static final PongUI pongUI = new PongUI();

    public Pong(Camera camera) {
        this.player = new Player(camera);
        physicalWorld = new PhysicalWorld();
        pongManager = new PongManager(physicalWorld, camera);
    }

    public void init() {
        UIMaster.getRoot().add(pongUI);
        physicalWorld.init();
        pongManager.init();
    }

    public void update(float deltaTime) {
        physicalWorld.update(deltaTime);
        pongManager.update(deltaTime);
    }

    public void render(float deltaTime) {
        player.update(deltaTime);
        pongManager.render();
    }
}
