package me.jraynor.pong.entities;

import lombok.Getter;
import me.jraynor.Game;
import me.jraynor.misc.Camera;
import me.jraynor.misc.Input;
import me.jraynor.pong.Pong;
import me.jraynor.pong.audio.AudioObserver;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static me.jraynor.pong.Pong.pongUI;
import static org.lwjgl.glfw.GLFW.*;

public class Player {

    @Getter
    private Camera camera;
    private double savedX = -1, savedY = -1;
    @Getter
    private Vector3f location = new Vector3f(0, 0, 0);
    @Getter
    private Quaternionf rotation = new Quaternionf();
    @Getter
    private Vector3f dir = new Vector3f();
    private Vector3f right = new Vector3f();
    private Vector3f up = new Vector3f();
    private float sensitivity = 5;
    private final float speed = 0.5f;
    @Getter
    private AudioObserver audioObserver;

    @Getter
    private boolean generateNew = true;

    public Player(Camera camera) {
        this.camera = camera;

    }

    public void init() {
        this.audioObserver = new AudioObserver();
        Pong.audioMaster.setListener(audioObserver);
    }

    public void update(float deltaTime) {
        move(deltaTime);
        camera.getViewMatrix().identity().rotateX(rotation.x).rotateY(rotation.y).translate(-location.x, -location.y, -location.z);
        if (pongUI.getXPos() != null && pongUI.getYPos() != null && pongUI.getZPos() != null) {
            pongUI.getXPos().setText("X: " + String.format("%.2f", location.x));
            pongUI.getYPos().setText("Y: " + String.format("%.2f", location.y));
            pongUI.getZPos().setText("Z: " + String.format("%.2f", location.z));

        }

        // Update camera matrix with camera data
        audioObserver.setPosition(location);
        Vector3f at = new Vector3f();
        camera.getViewMatrix().positiveZ(at).negate();
        Vector3f up = new Vector3f();
        camera.getViewMatrix().positiveY(up);
        audioObserver.setOrientation(at, up);
    }

    /**
     * Moves the players with delta
     *
     * @param deltaTime
     */
    private void move(float deltaTime) {

        if (Input.mouseDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            if (!Input.isMouseGrabbed()) {
                Input.setMouseGrabbed(true);
                if (savedX != -1 && savedY != -1) {
                    Input.setMouse(savedX, savedY);
                    Input.mousePos.invoke(Input.window, savedX, savedY);
                }
            }
            updateDir(deltaTime);
            updatePosition();
            updateRotation();

        } else {
            if (Input.isMouseGrabbed()) {
                savedX = Input.mousePosition.x;
                savedY = Input.mousePosition.y;
                Input.setMouseGrabbed(false);
                Input.setMouse(Game.getWin().getWidth() / 2, Game.getWin().getHeight() / 2);
            }
        }
    }

    /**
     * Updates player rotation
     */
    private void updateRotation() {
        rotation.x = (float) (Input.mousePosition.y / Game.getWin().getWidth()) * sensitivity;
        rotation.y = (float) (Input.mousePosition.x / Game.getWin().getHeight()) * sensitivity;
    }

    /**
     * Updates player position
     */
    private void updatePosition() {
        if (Input.keyDown(GLFW_KEY_W))
            location.add(dir);

        if (Input.keyDown(GLFW_KEY_S))
            location.sub(dir);

        if (Input.keyDown(GLFW_KEY_A))
            location.sub(right);

        if (Input.keyDown(GLFW_KEY_D)) {
            location.add(right);
        }

    }

    public Vector3f getRight() {
        right.x = camera.getViewMatrix().m00();
        right.y = camera.getViewMatrix().m10();
        right.z = camera.getViewMatrix().m20();
        return right;
    }

    public Vector3f getUp() {
        up.x = camera.getViewMatrix().m01();
        up.y = camera.getViewMatrix().m11();
        up.z = camera.getViewMatrix().m21();
        return up;
    }

    /**
     * Updates direction
     *
     * @param deltaTime
     */
    private void updateDir(float deltaTime) {

        camera.getViewMatrix().positiveZ(dir).negate().mul(speed * deltaTime);
        camera.getViewMatrix().positiveX(right).mul(speed * deltaTime);
        camera.getViewMatrix().positiveY(up).mul(speed * deltaTime);

    }


}

