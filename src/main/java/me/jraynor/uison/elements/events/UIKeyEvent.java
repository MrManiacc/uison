package me.jraynor.uison.elements.events;

import me.jraynor.uison.UIMaster;
import me.jraynor.uison.controller.events.EnterEvent;
import me.jraynor.uison.controller.events.FocusEvent;
import me.jraynor.uison.controller.events.KeyEvent;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.color.UIColor;
import me.jraynor.uison.logic.constraint.UIConstraint;
import me.jraynor.uison.misc.Input;
import me.jraynor.uison.misc.UIRenderable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.glfw.GLFW.*;

public class UIKeyEvent extends UIComponent implements UIRenderable {
    private String id;
    private UIConstraint constraint;
    private KeyEvent event;
    private FocusEvent eventFocus;
    private EnterEvent eventEnter;
    private String text = "";
    private int fontSize = 18;
    private long pressTime = 0;
    private long lastRemoveTime = System.currentTimeMillis();
    private UIColor cursor;
    private boolean focused = false;
    private long focusTime = -1;
    private Pattern pattern;

    public UIKeyEvent(String id, int fontSize) {
        this.id = id;
        this.fontSize = fontSize;
        setRender(true);
    }

    public UIKeyEvent(String id) {
        this.id = id;
        setRender(false);
    }

    @Override
    protected void onAdded() {
        this.constraint = getParentConstraint();
        this.event = new KeyEvent(this.id, getParent(), this);
        this.eventFocus = new FocusEvent(this.id, getParent(), this);
        this.eventEnter = new EnterEvent(this.id, getParent(), this);
        getParent().properties.put("lastkey", ' ');

        if (getParent().properties.containsKey("cc")) {
            this.cursor = UIColor.copy((UIColor) getParent().properties.get("cc"));
        } else {
            this.cursor = UIColor.copy(UIColor.WHITE);
        }


        if (!getParent().properties.containsKey("ph")) {
            getParent().properties.put("ph", true);
        }

        if (getParent().properties.containsKey("regx")) {
            this.pattern = Pattern.compile((String) getParent().properties.get("regx"));
        }

    }

    public void unFocus() {
        this.text = "";
        getParent().properties.put("focused", false);
        getParent().properties.put("hovered", false);
        event.setText(text);
        eventFocus.setFocused(false);
        UIMaster.postEvent(id, "focus", eventFocus);
        getParent().properties.put("ph", true);
    }

    private boolean hasFocused = false;

    @Override
    protected void onUpdate() {
        focused = (boolean) getParent().properties.get("focused");
        if (focused) {
            if (this.text.equals(getParent().properties.get("placeholder"))) {
                hasFocused = true;
                text = "";
                event.setText(text);
                event.setKeyTyped('\b');
                UIMaster.postEvent(id, "key_pressed", event);
            }
        }

        if (focused && focusTime == -1)
            focusTime = System.currentTimeMillis();
        else if (!focused)
            focusTime = -1;

        if (this.constraint == null) {
            this.constraint = this.getParentConstraint();
            return;
        }
        if (focused && (Input.keyPressed(GLFW_KEY_ENTER) || Input.keyPressed(GLFW_KEY_END))) {
            getParent().properties.put("focused", false);
            getParent().properties.put("hovered", false);
            eventEnter.setValue(text);
            UIMaster.postEvent(id, "text_enter", eventEnter);
            eventFocus.setFocused(false);
            UIMaster.postEvent(id, "focus", eventFocus);
        } else if (focused && Input.isKeyPressed() && !Input.keyPressed(GLFW_KEY_LEFT_SHIFT)) {
            float mx = (float) Input.mousePosition.x, my = (float) Input.mousePosition.y;
            event.setMouseX(mx);
            event.setMouseY(my);

            char c = Input.getKeyCharacter();

            processText(c);
            getParent().properties.put("ph", false);
        }
        handleBackspace(focused);
        if (!focused) {
            if (text.isEmpty()) {
                if (getParent().properties.containsKey("placeholder")) {
                    this.text = (String) getParent().properties.get("placeholder");
                    getParent().properties.put("ph", true);
                    event.setText(text);
                    UIMaster.postEvent(id, "key_pressed", event);
                }
            }
        }
    }

    private void handleBackspace(boolean focused) {
        if (Input.keyPressed(GLFW_KEY_BACKSPACE) && focused) {
            pressTime = System.currentTimeMillis();
        }
        if (Input.keyReleased(GLFW_KEY_BACKSPACE) && focused) {
            if (System.currentTimeMillis() - pressTime < 300) {
                if (this.text.length() > 0) {
                    this.text = text.substring(0, text.length() - 1);
                    event.setText(text);
                    event.setKeyTyped('\b');
                    UIMaster.postEvent(id, "key_pressed", event);
                }
            }
            pressTime = 0;
        }
        if (pressTime != 0) {
            long time = System.currentTimeMillis();
            long diff = time - pressTime;
            if (diff > 500) {
                if (time - lastRemoveTime > 20) {
                    if (this.text.length() > 0) {
                        this.text = text.substring(0, text.length() - 1);
                        event.setText(text);
                        event.setKeyTyped('\b');
                        UIMaster.postEvent(id, "key_pressed", event);
                    }
                    lastRemoveTime = time;
                }
            }
        }
    }

    private void processText(char c) {
        if (!Input.keyDown(GLFW_KEY_BACKSPACE)) {
            if (pattern != null) {
                Matcher matcher = pattern.matcher(c + "");
                if (matcher.find()) {
                    text = text + c;
                    event.setText(text);
                    event.setKeyTyped(c);
                    UIMaster.postEvent(id, "key_pressed", event);


                }
            } else {
                text = text + c;
                event.setText(text);
                event.setKeyTyped(c);
                UIMaster.postEvent(id, "key_pressed", event);
            }
        }
    }

    private long lastTime = System.currentTimeMillis();
    private final long blinkTime = 500; //one half a second
    private boolean increase = true;
    private int blinkOpacity = 0;
    private long totalTime;

    private void drawCursor() {
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastTime;
        totalTime += timePassed;
        lastTime = currentTime;
        if (totalTime >= blinkTime && totalTime <= blinkTime * 2) {
            UIColor.alpha(cursor, 0);
        } else {
            UIColor.alpha(cursor, 255);
        }
        if (totalTime >= blinkTime * 2)
            totalTime = 0;


        float h = constraint.h / 1.5f;
        float cursorWidth = 1;
        drawColoredRoundedRect(vg, (constraint.x + getSize()), constraint.y + (h / 4.0f), cursorWidth, h, 1, cursor);
    }

    private float[] tBounds = new float[4];

    private float getSizeFromIndex(int index) {
        String sub = text.substring(index);
        calcTextBounds(vg, constraint.x, constraint.y, sub, tBounds, fontSize);
        return tBounds[2] + 7;
    }

    private float getSize() {
        calcTextBounds(vg, constraint.x, constraint.y, text, tBounds, fontSize);
        return tBounds[2] + 7;
    }


    @Override
    protected void render() {
        if (cursor != null && focused) {
            drawCursor();
        }
    }
}
