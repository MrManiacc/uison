package me.jraynor.uison.parser;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.uison.UIMaster;
import me.jraynor.uison.elements.*;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.constraint.UIConstraint;
import me.jraynor.uison.misc.Style;
import me.jraynor.uison.parser.parser.ParserJson;

import java.util.*;

public class MappedUIComponent {
    @Getter
    private String id;
    @Getter
    @Setter
    private String[] groups;
    @Getter
    private UIType type;
    private Map<String, Prop> propMap;
    private JsonElement element;
    private ParserJson parser;
    private MappedUIComponent parent;
    @Getter
    private UIComponent component;
    private List<UIComponent> addLater = new ArrayList<>();
    private UUID uuid;
    @Getter
    private Props props;

    public MappedUIComponent(String id, UIType type, JsonElement element, ParserJson parser, MappedUIComponent parent) {
        this.id = id;
        this.type = type;
        this.propMap = new HashMap<>();
        this.parser = parser;
        this.element = element;
        this.parent = parent;
        this.props = new Props(id, parser);
    }

    public void parse() {
        try {
            this.uuid = UUID.randomUUID();
            props.parse(parser.getComponents(element));
            makeComponent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MappedUIComponent)
            return this.uuid.equals(((MappedUIComponent) obj).uuid);
        return false;
    }

    private void makeComponent() {
        switch (type) {
            case UISEPERATOR:
                component = new UISeparator();
                break;
            case UIFLEX:
                component = new UIFlex();
                break;
            case UICONSTRAINT:
                component = new UIConstraint();
                break;
            case UIBLOCK:

                component = new UIBlock();
                break;
            case UIVBOX:

                component = new UIVBox();
                ((UIVBox) component).setFlexWidth(false);
                ((UIVBox) component).setFlexHeight(true);
                break;
            case UIHBOX:
                component = new UIHBox();
                ((UIHBox) component).setFlexWidth(true);
                ((UIHBox) component).setFlexHeight(true);
                break;
            case UITEXTBOX:
                component = new UITextBox();
                break;
            case UIBAR:
                component = new UIBar();
                break;
            case UISLIDER:
                component = new UISlider();
                break;
            case UITEXT:
                component = new UILabel();
                break;
        }
        if (component != null) {
            component.properties = (HashMap<String, Object>) this.props.getProps();
            component.setMappedUIComponent(this);
            component.setIdentifier(this.id);
            component.setProps(props);
            Style style = Style.defaultStyle(component);
            if (style != null) {
                style.parse(props);
                component.setStyle(style);
                component.onStyle(style);
            }
            if (parent != null && component instanceof UIConstraint)
                parent.component.add(component);
            else if (parent != null) {
                parent.addLater.add(component);
            }

            if (props.has("fam")) {
                String[] fam = null;
                if (props.is("fam", String.class))
                    fam = new String[]{props.String("fam")};
                else if (props.is("fam", String[].class))
                    fam = props.StringArray(id);
                UIMaster.registerComponent(id, this, fam);
                this.groups = (String[]) props.get("fam");
            } else
                UIMaster.registerComponent(id, this);
            UIMaster.postComponent(id, component);
        }
    }


    public void onAdded(UIComponent parent) {
        for (UIComponent addLater : addLater)
            component.add(addLater);
    }


    @Override
    public String toString() {

        return "[" + this.id + "-" + this.type + "] ";
    }


}
