package me.jraynor.uison.misc;


import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.*;

import java.text.DecimalFormat;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private static final int KEYBOARD_SIZE = 512;
    private static final int MOUSE_SIZE = 16;
    public static long window;
    private static int[] keyStates = new int[KEYBOARD_SIZE];
    private static boolean[] activeKeys = new boolean[KEYBOARD_SIZE];
    private static int[] mouseButtonStates = new int[MOUSE_SIZE];
    private static boolean[] activeMouseButtons = new boolean[MOUSE_SIZE];
    private static long lastMouseNS = 0;
    private static long mouseDoubleClickPeriodNS = 1000000000 / 5; //5th of a second for double click.
    public static final Vector2d mousePosition = new Vector2d(0, 0);
    private static final Vector2d globalMouse = new Vector2d(0, 0);
    public static final Vector2d mouseDelta = new Vector2d(0, 0);
    private static int NO_STATE = -1;
    private static boolean firstMouse = true;
    private static float mouseWheelVelocity = 0;
    private static char currentCharacter = 'a';
    private static boolean keyDown = false;
    private static double[] globalMX = new double[1];
    private static double[] globalMY = new double[1];

    public static long DEFAULT_CURSOR = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    public static long TEXT_CURSOR = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);

    public static boolean keyDown(int key) {
        return activeKeys[key];
    }

    public static void setCursor(long cursor) {
        glfwSetCursor(window, cursor);
    }

    public static boolean keyPressed(int key) {
        return keyStates[key] == GLFW_PRESS;
    }

    public static boolean isKeyPressed() {
        for (int keyState : keyStates)
            if (keyState == GLFW_PRESS)
                return true;
        return false;
    }

    public static String getKeysDownString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean active = false;
        for (int i = 0; i < activeKeys.length; i++) {
            if (activeKeys[i]) {
                active = true;
                sb.append("(").append((char) i);
                sb.append(", ").append(i).append("), ");
            }
        }
        if (active)
            sb.replace(sb.length() - 2, sb.length(), "");
        sb.append("]");
        if (active)
            return sb.toString();
        return "";
    }


    public static Vector2d globalMouse() {
        glfwGetCursorPos(window, globalMX, globalMY);
        globalMouse.x = globalMX[0];
        globalMouse.y = globalMY[0];
        int[] x = new int[1];
        int[] y = new int[1];
        glfwGetWindowPos(window, x, y);
        globalMouse.x += x[0];
        globalMouse.y += y[0];
        return globalMouse;
    }

    public static boolean keyReleased(int key) {
        return keyStates[key] == GLFW_RELEASE;
    }

    public static boolean mouseDown(int button) {
        return activeMouseButtons[button];
    }

    public static boolean mousePressed(int button) {
        return mouseButtonStates[button] == GLFW_RELEASE;
    }

    public static float getScrollVelocity() {
        return mouseWheelVelocity;
    }

    public static void setMouseGrabbed(boolean grabbed) {
        glfwSetInputMode(window, GLFW_CURSOR, grabbed ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }


    public static void setMousePosition(double x, double y) {
        glfwSetCursorPos(window, x, y);
    }

    public static boolean isMouseGrabbed() {
        return glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED;
    }

    public static boolean mouseReleased(int button) {
        boolean flag = mouseButtonStates[button] == GLFW_RELEASE;

        if (flag)
            lastMouseNS = System.nanoTime();

        return flag;
    }

    public static char getKeyCharacter() {
        return currentCharacter;
    }

    public static boolean isKeyDown() {
        return false;
    }

    public static void setMouseState(boolean on) {
        GLFW.glfwSetInputMode(window, GLFW_CURSOR, on ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    public static boolean mouseDoubleClicked(int button) {
        long last = lastMouseNS;
        boolean flag = mouseReleased(button);

        long now = System.nanoTime();

        if (flag && now - last < mouseDoubleClickPeriodNS) {
            lastMouseNS = 0;
            return true;
        }

        return false;
    }

    private static GLFWKeyCallback keyboard = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            activeKeys[key] = action != GLFW_RELEASE;
            keyStates[key] = action;
        }
    };


    private static GLFWScrollCallbackI scroll = (GLFWScrollCallbackI) (window, x, y) -> {
        mouseWheelVelocity = (float) y;
    };

    public static void setMouse(double x, double y) {
        glfwSetCursorPos(window, x, y);
    }


    private static GLFWMouseButtonCallback mouse = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {

            activeMouseButtons[button] = action != GLFW_RELEASE;
            mouseButtonStates[button] = action;

            switch (button) {
                case 1:
                    button = 2;
                    break;
                case 2:
                    button = 1;
                    break;
            }
        }
    };

    public static GLFWCursorPosCallback mousePos = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (firstMouse) {
                firstMouse = false;
            } else {

                mouseDelta.x = x - mousePosition.x;
                mouseDelta.y = y - mousePosition.y;
            }
            mousePosition.x = x;
            mousePosition.y = y;

        }
    };


    private static GLFWCharCallback charCallback = new GLFWCharCallback() {
        @Override
        public void invoke(long window, int codepoint) {
            currentCharacter = (char) codepoint;
            keyDown = true;
        }

    };

    public static boolean isCharDown() {
        return keyDown;
    }

    public static void init(long window) {
        Input.window = window;
        glfwSetKeyCallback(window, keyboard);
        glfwSetMouseButtonCallback(window, mouse);
        glfwSetCharCallback(window, charCallback);
        glfwSetCursorPosCallback(window, mousePos);
        glfwSetScrollCallback(window, scroll);
        resetKeyboard();
        resetMouse();
    }

    public static void update() {
        resetKeyboard();
        resetMouse();
    }

    private static void resetKeyboard() {
        Arrays.fill(keyStates, NO_STATE);
    }

    private static void resetMouse() {
        Arrays.fill(mouseButtonStates, NO_STATE);

        long now = System.nanoTime();

        if (now - lastMouseNS > mouseDoubleClickPeriodNS) {
//            mouseDelta.x = 0;
//            mouseDelta.y = 0;
            lastMouseNS = 0;
//            mouseWheelVelocity = 0;
//            keyDown = false;
        }
    }
}
