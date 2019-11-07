package me.jraynor.gui.logic;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.gui.elements.UIText;
import me.jraynor.gui.logic.constraint.UIConstraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UIComponent implements Comparable<UIComponent>, Cloneable {
    @Setter
    private int level;
    @Getter
    private List<UIComponent> children = new ArrayList<>();
    private HashMap<Class, List<UIComponent>> mappedChildren = new HashMap<>();
    private HashMap<Class, Integer> childrenTypeCount = new HashMap<>();
    private boolean hasChildren = false;
    @Getter
    @Setter
    private UIComponent parent;
    @Getter
    @Setter
    private boolean active = true;
    @Getter
    @Setter
    private boolean render;
    @Getter
    @Setter
    protected long vg;
    private static int nextID = 0;

    private int id;

    public UIComponent(int level) {
        this.level = level;
        this.render = false;
        this.id = ++nextID;
    }

    public UIComponent(UIComponent other) {
        this.level = other.level;
        this.parent = other.parent;
        this.active = other.active;
        this.vg = other.vg;
        for (UIComponent child : other.children)
            add(child);
        this.id = ++nextID;
    }

    protected void onAdded() {
    }

    protected void onSiblingAdded(UIComponent sibling) {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UIComponent) {
            UIComponent component = (UIComponent) obj;
            return component.id == this.id;
        }
        return super.equals(obj);
    }

    public void setActive(boolean active) {
        this.active = active;
        for (UIComponent uiComponent : children)
            uiComponent.active = active;
    }

    public UIComponent() {
        this(-1);
    }


    public void setText(String text) {
        if (this instanceof UIText) {
            ((UIText) this).setText(text);
        } else {
            if (hasComponent(UIText.class)) {
                UIText uiText = (UIText) getComponent(UIText.class);
                uiText.setText(text);
            }
        }
    }

    /**
     * This will attempt to replace the current UIComponent with specified class type
     * with the new UIComponent with the specified class type
     *
     * @param uiComponent the new component
     * @return whether or not it was replaced
     */
    public boolean replace(UIComponent uiComponent) {
        if (hasComponent(uiComponent.getClass())) {
            this.children.remove(getComponent(uiComponent.getClass()));
            add(uiComponent);
            int count = childrenTypeCount.remove(uiComponent.getClass());
            childrenTypeCount.put(uiComponent.getClass(), --count);
            return true;
        }
        add(uiComponent);
        return false;
    }

    /**
     * Adds a new component to this component, this way we're creating a hierarchy of components
     *
     * @param uiComponent
     * @return returns the component that was added
     */
    public UIComponent add(UIComponent uiComponent) {
        if (uiComponent != null) {
            uiComponent.setParent(this);
            uiComponent.vg = this.vg;
            children.add(uiComponent);
            Collections.sort(children);
            hasChildren = true;
            uiComponent.active = this.active;
            if (childrenTypeCount.containsKey(uiComponent.getClass())) {
                int count = childrenTypeCount.remove(uiComponent.getClass());
                childrenTypeCount.put(uiComponent.getClass(), ++count);
            } else {
                childrenTypeCount.put(uiComponent.getClass(), 1);
            }
            if (mappedChildren.containsKey(uiComponent.getClass())) {
                mappedChildren.get(uiComponent.getClass()).add(uiComponent);
            } else {
                List<UIComponent> components = new ArrayList<>();
                components.add(uiComponent);
                mappedChildren.put(uiComponent.getClass(), components);
            }
            uiComponent.onAdded();

            for (UIComponent children : children) {
                if (children.id != uiComponent.id)
                    children.onSiblingAdded(uiComponent);
            }
            return uiComponent;
        }
        return null;
    }

    public UIComponent remove(UIComponent uiComponent) {
        if (hasComponent(uiComponent.getClass())) {
            int count = childrenTypeCount.remove(uiComponent.getClass());
            childrenTypeCount.put(uiComponent.getClass(), Math.max(--count, 0));
            UIComponent removable = getComponent(uiComponent.getClass());
            children.remove(removable);
            return removable;
        }
        return null;
    }

    /**
     * This will attempt to find a component by the type of class,
     * if there are multiple it will take the first one
     *
     * @param type the type of component to find
     * @return the first component with type
     */
    public UIComponent getComponent(Class type) {
        for (UIComponent uiComponent : children)
            if (uiComponent.getClass().equals(type))
                return uiComponent;
        return null;
    }


    public List<UIComponent> getComponentGroup(Class type) {
        return mappedChildren.get(type);
    }

    /**
     * This method will be called once per frame if the UIComponent is renderable
     */
    protected void render() {
    }


    /**
     * Simple check to see if the parent is present or not
     *
     * @return status of parent
     */
    public boolean hasParent() {
        if (parent == null)
            return false;
        return parent.parent != null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public UIConstraint getMaxX(UIComponent ignore) {
        float max = -1;
        UIConstraint maxConstraint = null;

        for (UIComponent child : children) {
            if (child.id == ignore.id)
                continue;
            if (child instanceof UIConstraint) {
                UIConstraint constraint = (UIConstraint) child;
                if (constraint.w + constraint.x > max) {
                    maxConstraint = constraint;
                    max = constraint.w + constraint.x;
                }
            } else {
                for (UIComponent child2 : child.getChildren())
                    if (child2 instanceof UIConstraint) {
                        if (child2.id == ignore.id)
                            continue;
                        UIConstraint constraint = (UIConstraint) child2;
                        if (constraint.w + constraint.x > max) {
                            maxConstraint = constraint;
                            max = constraint.w + constraint.x;
                        }
                    }
            }
        }

        return maxConstraint;
    }

    /**
     * Checks to see if the component exists as a child
     *
     * @param type the type of component to look for
     * @return returns whether or not the component is a child
     */
    public boolean hasComponent(Class type) {
        return childrenTypeCount.containsKey(type);
    }

    /**
     * Updates all of the ui components,
     * making sure to update the component it's self
     * before it updates all of the component's children
     */
    public void update() {
        if (active) {
            onUpdate();
            if (render)
                render();
            for (UIComponent uiComponent : children)
                uiComponent.update();
        }
    }

    /**
     * this is called for the component to do any logical processing
     * that needs to be done to it's self, for a uicontraint it should
     * in theory calculate the positional data based upon the parent
     */
    protected void onUpdate() {
    }

    public UIConstraint getParentConstraint() {
        if (parent != null) {
            if (parent instanceof UIConstraint)
                return (UIConstraint) parent;
            else {
                if (parent.hasComponent(UIConstraint.class)) {
                    for (UIComponent component : parent.children) {
                        if (component.id != this.id) {
                            if (component instanceof UIConstraint) {
                                return (UIConstraint) component;
                            }
                        }
                    }
                }
                return parent.getParentConstraint();
            }
        }
        return null;
    }

    @Override
    public int compareTo(UIComponent o) {
        if ((this.level == -1 && o.level == -1) || this.level == o.level)
            return 0;
        else if (this.level == -1)
            return -1;
        else if (o.level == -1)
            return 1;
        if (this.level > o.level)
            return -1;
        else
            return 1;
    }

    public boolean hasChildren() {
        return hasChildren;
    }
}
