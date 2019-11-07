package me.jraynor.gui;

import lombok.Getter;
import me.jraynor.bootstrap.Window;
import me.jraynor.gui.controller.Controller;
import me.jraynor.gui.controller.events.UIEvent;
import me.jraynor.gui.elements.UIBlock;
import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.color.UIColor;
import me.jraynor.gui.logic.constraint.UIConstraint;
import me.jraynor.gui.parser.MappedImport;
import me.jraynor.gui.parser.MappedUIComponent;
import me.jraynor.gui.parser.UIParser;
import me.jraynor.gui.parser.UIType;
import me.jraynor.misc.Input;

import java.util.*;

import static me.jraynor.gui.logic.constraint.Constraints.PixelConstraint;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;


public class UIMaster {
    @Getter
    private static final UIComponent root = new UIComponent();
    private static final UIConstraint rootConstraints = new UIConstraint();
    private static Controller controller;
    private static final UIParser uiparser = new UIParser();
    private static final HashMap<String, MappedImport> imports = new HashMap<>();
    private static final HashMap<String, List<MappedUIComponent>> componentGroups = new HashMap<>();
    private static final HashMap<String, MappedUIComponent> components = new HashMap<>();
    private static final HashMap<UIType, List<MappedUIComponent>> componentTagGroups = new HashMap<>();
    private static final UIBlock displayBlock = new UIBlock(UIColor.TRANSPARENT);

    public static void createUIMaster(Window window, Controller controller) {
        UIMaster.controller = controller;
        root.setVg(window.getVg());
        rootConstraints.setXConst(new PixelConstraint(0));
        rootConstraints.setYConst(new PixelConstraint(0));
        rootConstraints.setWConst(new PixelConstraint(window.getWidth()));
        rootConstraints.setHConst(new PixelConstraint(window.getHeight()));
        root.add(rootConstraints);
        root.add(displayBlock);
        uiparser.reloadUI();
    }

    public static void showDisplayBlock(UIConstraint constraint) {
        displayBlock.remove(UIConstraint.class);
        displayBlock.add(constraint);
        displayBlock.setColor(UIColor.rgba(45, 87, 145, 150));
    }

    public static void hideDisplayBlock() {
        displayBlock.setColor(UIColor.TRANSPARENT);
    }


    public static void registerMappedImport(MappedImport mappedImport) {
        imports.put(mappedImport.getId(), mappedImport);
    }

    /**
     * Adds a component to a group
     *
     * @param groupName
     * @param component
     * @return
     */
    public static int registerComponentToGroup(String groupName, MappedUIComponent component) {
        if (componentGroups.containsKey(groupName))
            componentGroups.get(groupName).add(component);
        else {
            List<MappedUIComponent> group = new ArrayList<>();
            group.add(component);
            componentGroups.put(groupName, group);
        }
        return componentGroups.get(groupName).size();
    }

    public static boolean hasComponent(String id) {
        return components.containsKey(id);
    }

    public static boolean hasGroup(String group) {
        return componentGroups.containsKey(group);
    }

    public static boolean hasTag(UIType type) {
        return componentTagGroups.containsKey(type);
    }

    /**
     * Register a component globally
     */
    public static void registerComponent(String id, MappedUIComponent component, String... groups) {
        components.put(id, component);
        if (componentTagGroups.containsKey(component.getType()))
            componentTagGroups.get(component.getType()).add(component);
        else {
            List<MappedUIComponent> components = new ArrayList<>();
            components.add(component);
            componentTagGroups.put(component.getType(), components);
        }
        for (String group : groups) {
            registerComponentToGroup(group, component);
        }
    }

    /**
     * Attempting to get a component by id
     */
    public static Optional<MappedUIComponent> component(String id) {
        if (components.containsKey(id))
            return Optional.of(components.get(id));
        return Optional.empty();
    }

    /**
     * Get the group with the specified name
     */
    public static Optional<List<MappedUIComponent>> group(String groupName) {
        if (!componentGroups.containsKey(groupName))
            return Optional.empty();
        return Optional.of(componentGroups.get(groupName));
    }

    /**
     * Get the group with the specified name
     */
    public static Optional<List<MappedUIComponent>> tag(UIType type) {
        if (!componentTagGroups.containsKey(type))
            return Optional.empty();
        return Optional.of(componentTagGroups.get(type));
    }

    public static Object prop(String importName, String propName) {
        return imports.get(importName).prop(propName);
    }

    public static void copyProps(String importName, Map<String, Object> properties) {
        imports.get(importName).copyProps(properties);
    }


    public static void postEvent(String id, String action, UIEvent event) {
        String[] groups = components.get(id).getGroups();
        controller.postIDEvent(id, action, event);
        controller.postTagEvent(event.component().getMappedUIComponent().getType(), action, event);
        if (groups != null)
            if (groups.length > 0)
                for (String group : groups) {
                    postGroupEvent(group, action, event);
                }
    }

    public static void postGroupEvent(String group, String action, UIEvent event) {
        controller.postGroupEvent(group, action, event);
    }


    public static void postComponent(String id, UIComponent component) {
        controller.postComponent(id, component);
    }


    public static void addEvents() {
        controller.addEvents();
    }


    /**
     * Called every frame. It will update the root constraints based upon
     * when the window is resized from inside the window class
     *
     * @param window the main window
     */
    public static void update(Window window) {
        controller.update();
        root.update();
        if (window.isResized()) {
            PixelConstraint widthConstraint = (PixelConstraint) rootConstraints.getWidthConstraint();
            PixelConstraint heightConstraint = (PixelConstraint) rootConstraints.getHeightConstraint();
            widthConstraint.setPixel(window.getWidth());
            heightConstraint.setPixel(window.getHeight());
            window.setResized(false);
        }

        if (Input.keyDown(GLFW_KEY_LEFT_CONTROL) && Input.keyDown(GLFW_KEY_R)) {
            imports.clear();
            components.clear();
            componentGroups.clear();
            root.clear();
            controller.clear();
            root.add(rootConstraints);
            uiparser.reloadUI();
            root.add(displayBlock);
        }
    }
}
