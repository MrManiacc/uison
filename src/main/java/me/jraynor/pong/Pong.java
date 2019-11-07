package me.jraynor.pong;

import me.jraynor.gui.UIMaster;
import me.jraynor.misc.Camera;
import me.jraynor.pong.audio.AudioMaster;
import me.jraynor.pong.entities.Player;
import me.jraynor.pong.physics.PhysicalWorld;
import me.jraynor.pong.render.PongManager;
import me.jraynor.pong.render.PongUI;

public class Pong {
    public static Player player;
    public static PongManager pongManager;
    public static PhysicalWorld physicalWorld;
    public static AudioMaster audioMaster;
    public static final PongUI pongUI = new PongUI();

    public Pong(Camera camera) {
        player = new Player(camera);
        physicalWorld = new PhysicalWorld();
        pongManager = new PongManager(physicalWorld, camera);
        audioMaster = new AudioMaster();
    }

    public void init() {
        UIMaster.getRoot().add(pongUI);
        try {
            audioMaster.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        physicalWorld.init();
        pongManager.init();
        player.init();
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
