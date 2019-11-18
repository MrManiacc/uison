package me.jraynor.uison.controller;

import me.jraynor.uison.UIMaster;
import me.jraynor.uison.controller.events.UIEvent;
import me.jraynor.uison.elements.UILabel;
import me.jraynor.uison.elements.UISlider;
import me.jraynor.uison.elements.UITextBox;
import me.jraynor.uison.elements.events.UIKeyEvent;
import me.jraynor.uison.elements.events.UIMouseEvent;
import me.jraynor.uison.elements.events.UISliderEvent;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.parser.MappedUIComponent;
import me.jraynor.uison.parser.UIType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class Controller {
    private HashMap<String, Map<String, Method>> mappedGroupEvents = new HashMap<>();
    private HashMap<String, Map<String, Method>> mappedIDEvents = new HashMap<>();
    private HashMap<UIType, Map<String, Method>> mappedTagEvents = new HashMap<>();
    private HashMap<String, Field> mappedFields = new HashMap<>();

    public Controller() {
        processMethods(getClass(), Event.class);
        processFields(getClass(), Component.class);
    }

    protected abstract void onReady();

    public void clear() {
        mappedGroupEvents.clear();
        mappedIDEvents.clear();
        mappedFields.clear();
        mappedTagEvents.clear();
        processMethods(getClass(), Event.class);
        processFields(getClass(), Component.class);
    }


    public void addEvents() {
        for (String id : mappedIDEvents.keySet()) {
            if (UIMaster.hasComponent(id)) {
                UIComponent uiComponent = UIMaster.component(id).getComponent();
                UIMouseEvent uiMouseEvent = new UIMouseEvent(id);
                uiMouseEvent.setMappedUIComponent(uiComponent.getMappedUIComponent());
                UIKeyEvent uiKeyEvent;
                if (uiComponent instanceof UILabel) {
                    UILabel label = (UILabel) uiComponent;
                    uiKeyEvent = new UIKeyEvent(id, (int) label.getStyle().getFontSize());
                } else if (uiComponent instanceof UITextBox) {
                    UITextBox textBox = (UITextBox) uiComponent;
                    uiKeyEvent = new UIKeyEvent(id, textBox.getFontSize());
                } else if (uiComponent instanceof UISlider) {
                    uiKeyEvent = new UIKeyEvent(id);
                } else {
                    uiKeyEvent = new UIKeyEvent(id);
                }
                UISliderEvent sliderEvent = new UISliderEvent(id);
                uiKeyEvent.setMappedUIComponent(uiComponent.getMappedUIComponent());
                for (String action : mappedIDEvents.get(id).keySet()) {
                    switch (action.toLowerCase()) {
                        case "mouse_exit":
                        case "mouse_press":
                        case "mouse_down":
                        case "mouse_up":
                        case "mouse_enter":
                            uiComponent.add(uiMouseEvent);
                            break;
                        case "focus":
                        case "key_pressed":
                        case "text_enter":
                            if (!uiComponent.hasComponent(UIMouseEvent.class))
                                uiComponent.add(uiMouseEvent);
                            if (!uiComponent.hasComponent(UIKeyEvent.class))
                                uiComponent.add(uiKeyEvent);
                            break;
                        case "slider_value":
                        case "slider_hover":
                            if (!uiComponent.hasComponent(UISliderEvent.class))
                                uiComponent.add(sliderEvent);
                    }
                }
            }
        }

        for (String group : mappedGroupEvents.keySet()) {
            if (UIMaster.hasGroup(group)) {
                List<MappedUIComponent> components = UIMaster.group(group);
                for (MappedUIComponent component : components) {
                    UIMouseEvent uiMouseEvent = new UIMouseEvent(component.getId());
                    uiMouseEvent.setMappedUIComponent(component);
                    UIKeyEvent uiKeyEvent;
                    if (component.getComponent() instanceof UILabel) {
                        UILabel label = (UILabel) component.getComponent();
                        uiKeyEvent = new UIKeyEvent(component.getId(), (int) label.getStyle().getFontSize());
                    } else if (component.getComponent() instanceof UITextBox) {
                        UITextBox textBox = (UITextBox) component.getComponent();
                        uiKeyEvent = new UIKeyEvent(component.getId(), textBox.getFontSize());
                    } else {
                        uiKeyEvent = new UIKeyEvent(component.getId());
                    }
                    UISliderEvent sliderEvent = new UISliderEvent(component.getId());
                    uiKeyEvent.setMappedUIComponent(component);
                    for (String action : mappedGroupEvents.get(group).keySet()) {
                        switch (action.toLowerCase()) {
                            case "mouse_exit":
                            case "mouse_press":
                            case "mouse_down":
                            case "mouse_up":
                            case "mouse_enter":
                                component.getComponent().add(uiMouseEvent);
                                break;
                            case "focus":
                            case "key_pressed":
                            case "slider_value":
                            case "slider_hover":
                                if (!component.getComponent().hasComponent(UISliderEvent.class))
                                    component.getComponent().add(sliderEvent);
                            case "text_enter":
                                if (!component.getComponent().hasComponent(UIMouseEvent.class))
                                    component.getComponent().add(uiMouseEvent);
                                if (!component.getComponent().hasComponent(UIKeyEvent.class))
                                    component.getComponent().add(uiKeyEvent);
                                break;
                        }
                    }
                }
            }
        }


        for (UIType type : mappedTagEvents.keySet()) {
            if (UIMaster.hasTag(type)) {
                if (UIMaster.tag(type).isPresent()) {
                    List<MappedUIComponent> components = UIMaster.tag(type).get();
                    for (MappedUIComponent component : components) {
                        UIMouseEvent uiMouseEvent = new UIMouseEvent(component.getId());
                        uiMouseEvent.setMappedUIComponent(component);
                        UIKeyEvent uiKeyEvent;
                        if (component.getComponent() instanceof UILabel) {
                            UILabel label = (UILabel) component.getComponent();
                            uiKeyEvent = new UIKeyEvent(component.getId(), (int) label.getStyle().getFontSize());
                        } else if (component.getComponent() instanceof UITextBox) {
                            UITextBox textBox = (UITextBox) component.getComponent();
                            uiKeyEvent = new UIKeyEvent(component.getId(), textBox.getFontSize());
                        } else if (component.getType().equals(UIType.UISLIDER)) {
                            uiKeyEvent = new UIKeyEvent(component.getId());
                        } else {
                            uiKeyEvent = new UIKeyEvent(component.getId());
                        }
                        UISliderEvent sliderEvent = new UISliderEvent(component.getId());

                        uiKeyEvent.setMappedUIComponent(component);
                        for (String action : mappedTagEvents.get(type).keySet()) {
                            switch (action.toLowerCase()) {
                                case "mouse_exit":
                                case "mouse_click":
                                case "mouse_enter":
                                case "mouse_press":
                                case "mouse_down":
                                    component.getComponent().add(uiMouseEvent);
                                    break;
                                case "focus":
                                case "key_pressed":
                                    if (!component.getComponent().hasComponent(UIMouseEvent.class))
                                        component.getComponent().add(uiMouseEvent);
                                    if (!component.getComponent().hasComponent(UIKeyEvent.class))
                                        component.getComponent().add(uiKeyEvent);
                                    break;
                                case "slider_value":
                                case "slider_hover":
                                    if (!component.getComponent().hasComponent(UISliderEvent.class))
                                        component.getComponent().add(sliderEvent);
                            }
                        }
                    }
                }
            }
        }
        onReady();
    }

    public void postGroupEvent(String id, String action, UIEvent event) {
        if (mappedGroupEvents.containsKey(id)) {
            if (mappedGroupEvents.get(id).containsKey(action)) {
                try {
                    mappedGroupEvents.get(id).get(action).invoke(this, event);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void postIDEvent(String id, String action, UIEvent event) {
        if (mappedIDEvents.containsKey(id)) {
            if (mappedIDEvents.get(id).containsKey(action)) {
                try {
                    mappedIDEvents.get(id).get(action).invoke(this, event);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void update() {
    }

    public void postTagEvent(UIType type, String action, UIEvent event) {
        if (mappedTagEvents.containsKey(type)) {
            if (mappedTagEvents.get(type).containsKey(action)) {
                try {
                    if (event != null)
                        mappedTagEvents.get(type).get(action).invoke(this, event);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    public void postComponent(String id, UIComponent component) {
        if (mappedFields.containsKey(id)) {
            try {
                mappedFields.get(id).set(this, component);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void processMethods(final Class<?> type, final Class<? extends Annotation> annotation) {
        Class<?> klass = type;
        while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance
            // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
            final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
            for (final Method method : allMethods) {
                if (method.isAnnotationPresent(annotation)) {
                    Event annotInstance = (Event) method.getAnnotation(annotation);

                    if (!annotInstance.tag().equals(UIType.UIUNKOWN)) {
                        if (!mappedTagEvents.containsKey(annotInstance.tag())) {
                            Map<String, Method> actions = new HashMap<>();
                            actions.put(annotInstance.action(), method);
                            mappedTagEvents.put(annotInstance.tag(), actions);
                        } else {
                            mappedTagEvents.get(annotInstance.tag()).put(annotInstance.action(), method);
                        }
                    }


                    if (!annotInstance.group().equals("NO_GROUP")) {
                        if (!mappedGroupEvents.containsKey(annotInstance.group())) {
                            Map<String, Method> actions = new HashMap<>();
                            actions.put(annotInstance.action(), method);
                            mappedGroupEvents.put(annotInstance.group(), actions);
                        } else {
                            mappedGroupEvents.get(annotInstance.group()).put(annotInstance.action(), method);
                        }
                    }
                    if (!annotInstance.id().equals("NO_ID")) {
                        if (!mappedIDEvents.containsKey(annotInstance.id())) {
                            Map<String, Method> actions = new HashMap<>();
                            actions.put(annotInstance.action(), method);
                            mappedIDEvents.put(annotInstance.id(), actions);
                        } else {
                            mappedIDEvents.get(annotInstance.id()).put(annotInstance.action(), method);
                        }
                    }

                }
            }
            // move to the upper class in the hierarchy in search for more methods
            klass = klass.getSuperclass();
        }
    }

    /**
     * @return null safe set
     */
    private void processFields(Class<?> classs, Class<? extends Annotation> ann) {
        Class<?> c = classs;
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(ann)) {
                    Component com = field.getAnnotation(Component.class);
                    mappedFields.put(com.id(), field);
                }
            }
            c = c.getSuperclass();
        }
    }


}
