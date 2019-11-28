package me.jraynor.uison;

import lombok.Getter;
import me.jraynor.bootstrap.Window;
import me.jraynor.uison.controller.Controller;
import me.jraynor.uison.controller.events.UIEvent;
import me.jraynor.uison.elements.UIBlock;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.color.UIColor;
import me.jraynor.uison.logic.color.UIImage;
import me.jraynor.uison.logic.constraint.UIConstraint;
import me.jraynor.uison.misc.Input;
import me.jraynor.uison.parser.MappedImport;
import me.jraynor.uison.parser.MappedUIComponent;
import me.jraynor.uison.parser.UIType;
import me.jraynor.uison.parser.UIWatcher;
import me.jraynor.uison.parser.parser.Parser;
import me.jraynor.uison.parser.parser.ParserJson;

import java.util.*;

import static me.jraynor.uison.logic.constraint.Constraints.PixelConstraint;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;


public class UIMaster {
    @Getter
    private static final UIComponent root = new UIComponent();
    private static final UIConstraint rootConstraints = new UIConstraint();
    private static Controller controller;
    private static Parser PARSER;
    private static final HashMap<String, MappedImport> imports = new HashMap<>();
    private static final HashMap<String, List<MappedUIComponent>> componentGroups = new HashMap<>();
    private static final HashMap<String, MappedUIComponent> components = new HashMap<>();
    private static final HashMap<UIType, List<MappedUIComponent>> componentTagGroups = new HashMap<>();
    private static final UIBlock displayBlock = new UIBlock();
//    private static final UIWatcher watcher = new UIWatcher("src/main/resources/ui");
    private static boolean reloadLater = false;
    private static final HashMap<String, List<String>> fileComponentID = new HashMap<>();
    private static final Map<String, UIImage> loadedImages = new HashMap<>();

    public static void createUIMaster(String parserLocation, Window window, Controller controller) {
        PARSER = new ParserJson(parserLocation);
        UIMaster.controller = controller;
        root.setVg(window.getVg());
        rootConstraints.setXConst(new PixelConstraint(0));
        rootConstraints.setYConst(new PixelConstraint(0));
        rootConstraints.setWConst(new PixelConstraint(window.getWidth()));
        rootConstraints.setHConst(new PixelConstraint(window.getHeight()));
        displayBlock.setColor(UIColor.TRANSPARENT);
        root.add(rootConstraints);
        root.add(displayBlock);
        PARSER.parse();
        loadImages(window.getVg());
//        new Thread(watcher).start();
    }

    public static void showDisplayBlock(UIConstraint constraint) {
        displayBlock.remove(UIConstraint.class);
        displayBlock.add(constraint);
        displayBlock.setColor(UIColor.rgba(45, 87, 145, 150));
    }

    public boolean hasImage(String fileName) {
        return loadedImages.containsKey(fileName);
    }

    public static void registerImage(String fileName, UIImage image) {
        loadedImages.put(fileName, image);
    }

    private static void loadImages(long vg) {
        for (UIImage image : loadedImages.values())
            image.load(vg);
    }

    public static UIImage getImage(String fileName) {
        return loadedImages.get(fileName);
    }

    public static void linkFileComponent(String filePath, String id) {
        if (fileComponentID.containsKey(filePath))
            fileComponentID.get(filePath).add(id);
        else {
            List<String> ids = new ArrayList<>();
            ids.add(id);
            fileComponentID.put(filePath, ids);
        }

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
    public static MappedUIComponent component(String id) {

        return components.get(id);

    }

    /**
     * Get the group with the specified name
     */
    public static List<MappedUIComponent> group(String groupName) {
        return componentGroups.get(groupName);
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
        if (imports.containsKey(importName))
            imports.get(importName).copyProps(properties);
    }

    public static void reloadFile(String filePath) {
        List<String> loadedID = fileComponentID.get(filePath);
        if (loadedID != null) {
            for (String id : loadedID) {
                for (String group : componentGroups.keySet()) {
                    List<MappedUIComponent> components = componentGroups.get(group);
                    for (int i = 0; i < components.size(); i++) {
                        MappedUIComponent comp = components.get(i);
                        if (comp.getId().equals(id))
                            components.remove(comp);
                    }
                    componentGroups.put(group, components);
                }
                components.remove(id);

                for (UIType tag : componentTagGroups.keySet()) {
                    List<MappedUIComponent> components = componentTagGroups.get(tag);
                    for (int i = 0; i < components.size(); i++) {
                        MappedUIComponent comp = components.get(i);
                        if (comp.getId().equals(id))
                            components.remove(comp);
                    }

                    componentTagGroups.put(tag, components);
                }

                root.remove(id);
            }
            controller.clear();
        }

        PARSER.reloadFile(filePath);
    }


    public static void postEvent(String id, String action, UIEvent event) {
        if (components.containsKey(id)) {
            String[] groups = components.get(id).getGroups();
            controller.postIDEvent(id, action, event);
            controller.postTagEvent(event.component().getMappedUIComponent().getType(), action, event);
            if (groups != null)
                if (groups.length > 0)
                    for (String group : groups) {
                        postGroupEvent(group, action, event);
                    }
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

        if (reloadLater || (Input.keyDown(GLFW_KEY_LEFT_CONTROL) && Input.keyDown(GLFW_KEY_R))) {
            reload();
            reloadLater = false;
        }
    }

    public static void reload() {
        imports.clear();
        components.clear();
        componentGroups.clear();
        root.clear();
        controller.clear();
        root.add(rootConstraints);
        PARSER.parse();
        root.add(displayBlock);
    }

    public static void reloadLater() {
        reloadLater = true;
    }
}
