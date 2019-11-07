package me.jraynor.gui.parser;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.gui.UIMaster;
import me.jraynor.gui.elements.*;
import me.jraynor.gui.logic.UIComponent;
import me.jraynor.gui.logic.color.UIColor;
import me.jraynor.gui.logic.constraint.Constraints;
import me.jraynor.gui.logic.constraint.UIConstraint;

import java.awt.*;
import java.util.List;
import java.util.*;

public class MappedUIComponent {
    @Getter
    private String id;
    @Getter
    @Setter
    private String[] groups;
    @Getter
    private UIType type;
    private Map<String, Object> properties;
    private JsonElement element;
    private UIParser parser;
    private MappedUIComponent parent;
    @Getter
    private UIComponent component;
    private List<UIComponent> addLater = new ArrayList<>();
    private UUID uuid;

    public MappedUIComponent(String id, UIType type, JsonElement element, UIParser parser, MappedUIComponent parent) {
        this.id = id;
        this.type = type;
        this.properties = new HashMap<>();
        this.parser = parser;
        this.element = element;
        this.parent = parent;
    }

    public void parse() {
        parseProperties();
        makeComponent();
        this.uuid = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MappedUIComponent)
            return this.uuid.equals(((MappedUIComponent) obj).uuid);
        return false;
    }

    private void makeComponent() {
        double padding = 0;
        switch (type) {
            case UISEPERATOR:
                UIColor seperatorColor = UIColor.WHITE;
                if (properties.containsKey("c")) {
                    seperatorColor = (UIColor) properties.get("c");
                }
                component = new UISeparator(seperatorColor);
                break;

            case UIFLEX:
                if (properties.containsKey("pad"))
                    padding = (double) properties.get("pad");
                if (properties.containsKey("c"))
                    component = new UIFlex((float) padding).fill((UIColor) properties.get("c"));
                else
                    component = new UIFlex((float) padding);
                break;
            case UICONSTRAINT:
                component = new UIConstraint();
                if (properties.containsKey("x"))
                    ((UIConstraint) component).setXConst((Constraints.Constraint) properties.get("x"));
                if (properties.containsKey("y"))
                    ((UIConstraint) component).setYConst((Constraints.Constraint) properties.get("y"));
                if (properties.containsKey("w"))
                    ((UIConstraint) component).setWConst((Constraints.Constraint) properties.get("w"));
                if (properties.containsKey("h"))
                    ((UIConstraint) component).setHConst((Constraints.Constraint) properties.get("h"));
                break;
            case UIBLOCK:
                if (properties.containsKey("c"))
                    component = new UIBlock((UIColor) properties.get("c"));
                else
                    component = new UIBlock(UIColor.DARK_GRAY);
                break;
            case UIVBOX:
                float pad1 = 0;
                float pad2 = 0;
                float pad3 = 0;
                float pad4 = 0;
                if (properties.containsKey("pad1")) {
                    pad1 = (float) properties.get("pad1");
                    pad2 = (float) properties.get("pad2");
                    pad3 = (float) properties.get("pad3");
                    pad4 = (float) properties.get("pad4");
                }
                if (properties.containsKey("c"))
                    component = new UIVBox(pad1, pad2, pad3, pad4).fill((UIColor) properties.get("c"));
                else
                    component = new UIVBox(pad1, pad2, pad3, pad4);

                ((UIVBox) component).setFlexWidth(false);
                ((UIVBox) component).setFlexHeight(true);
                if (properties.containsKey("ignore")) {
                    String[] ignored = new String[(int) properties.get("ignore")];
                    for (int i = 0; i < ignored.length; i++)
                        ignored[i] = (String) properties.get("ignore" + i);
                    ((UIHBox) component).setIgnored(ignored);
                }
                break;
            case UIHBOX:
                pad1 = 0;
                pad2 = 0;
                pad3 = 0;
                pad4 = 0;
                if (properties.containsKey("pad1")) {
                    pad1 = (float) properties.get("pad1");
                    pad2 = (float) properties.get("pad2");
                    pad3 = (float) properties.get("pad3");
                    pad4 = (float) properties.get("pad4");
                }
                if (properties.containsKey("c"))
                    component = new UIHBox(pad1, pad2, pad3, pad4).fill((UIColor) properties.get("c"));
                else
                    component = new UIHBox(pad1, pad2, pad3, pad4);

                if (properties.containsKey("ignore")) {
                    String[] ignored = new String[(int) properties.get("ignore")];
                    for (int i = 0; i < ignored.length; i++)
                        ignored[i] = (String) properties.get("ignore" + i);
                    ((UIHBox) component).setIgnored(ignored);
                }
                break;
            case UITEXTBOX:
                String font = "regular";
                int fontSize = 18;
                UIColor color = UIColor.TRANSPARENT;
                UIColor placeholderColor = UIColor.DARK_PURPLE;
                UIColor textColor = UIColor.WHITE;
                boolean fill = false;
                boolean radius = false;
                String text = "";
                float rad1 = 0;
                float rad2 = 0;
                float rad3 = 0;
                float rad4 = 0;

                pad1 = 0;
                pad2 = 0;
                pad3 = 0;
                pad4 = 0;
                UIColor cc = UIColor.WHITE;
                UIColor phc = UIColor.rgba(130, 130, 130, 200);
                UIColor fc = UIColor.WHITE;
                float textIndex = 0;
                if (properties.containsKey("ff"))
                    font = (String) properties.get("ff");
                if (properties.containsKey("cc"))
                    cc = (UIColor) properties.get("cc");
                if (properties.containsKey("txt"))
                    text = ((String) properties.get("txt"));
                if (properties.containsKey("c")) {
                    color = (UIColor) properties.get("c");
                    fill = true;
                }
                if (properties.containsKey("phc")) {
                    phc = (UIColor) properties.get("phc");
                }
                if (properties.containsKey("fc")) {
                    fc = (UIColor) properties.get("fc");
                }
                if (properties.containsKey("ti")) {
                    textIndex = (float) properties.get("ti");
                }
                if (properties.containsKey("pad1")) {
                    pad1 = (float) properties.get("pad1");
                    pad2 = (float) properties.get("pad2");
                    pad3 = (float) properties.get("pad3");
                    pad4 = (float) properties.get("pad4");
                }
                if (properties.containsKey("rad1")) {
                    rad1 = (float) properties.get("rad1");
                    rad2 = (float) properties.get("rad2");
                    rad3 = (float) properties.get("rad3");
                    rad4 = (float) properties.get("rad4");
                    radius = true;
                }

                if (properties.containsKey("fc"))
                    textColor = (UIColor) properties.get("fc");
                if (properties.containsKey("fs"))
                    fontSize = (int) properties.get("fs");
                if (fill) {
                    component = new UITextBox(text, font, fontSize, textColor, color);
                    ((UITextBox) component).setPadding(pad1, pad2, pad3, pad4);
                } else {
                    component = new UITextBox(text, font, fontSize, textColor);
                    ((UITextBox) component).setPadding(pad1, pad2, pad3, pad4);
                }

                if (radius) {
                    ((UITextBox) component).setRadius(rad1, rad2, rad3, rad4);
                }
                ((UITextBox) component).setTextIndent(textIndex);
                component.properties.put("cc", cc);
                component.properties.put("fc", fc);
                component.properties.put("phc", phc);
                if (properties.containsKey("regx")) {
                    component.properties.put("regx", properties.get("regx"));
                }
                break;
            case UIBAR:
                component = new UIBar();

                if (properties.containsKey("c"))
                    component.properties.put("c", properties.get("c"));
                break;
            case UITEXT:
                font = "regular";
                fontSize = 18;
                color = UIColor.TRANSPARENT;
                textColor = UIColor.WHITE;
                fill = false;
                radius = false;
                text = "";
                rad1 = 0;
                rad2 = 0;
                rad3 = 0;
                rad4 = 0;

                pad1 = 0;
                pad2 = 0;
                pad3 = 0;
                pad4 = 0;
                if (properties.containsKey("ff"))
                    font = (String) properties.get("ff");
                if (properties.containsKey("txt"))
                    text = ((String) properties.get("txt"));
                if (properties.containsKey("c")) {
                    color = (UIColor) properties.get("c");
                    fill = true;
                }
                if (properties.containsKey("pad1")) {
                    pad1 = (float) properties.get("pad1");
                    pad2 = (float) properties.get("pad2");
                    pad3 = (float) properties.get("pad3");
                    pad4 = (float) properties.get("pad4");
                }
                if (properties.containsKey("rad1")) {
                    rad1 = (float) properties.get("rad1");
                    rad2 = (float) properties.get("rad2");
                    rad3 = (float) properties.get("rad3");
                    rad4 = (float) properties.get("rad4");
                    radius = true;
                }

                if (properties.containsKey("fc"))
                    textColor = (UIColor) properties.get("fc");
                if (properties.containsKey("fs"))
                    fontSize = (int) properties.get("fs");
                if (fill) {
                    component = new UILabel(text, font, fontSize, textColor, color);
                    ((UILabel) component).setPadding(pad1, pad2, pad3, pad4);
                } else {
                    component = new UILabel(text, font, fontSize, textColor);
                    ((UILabel) component).setPadding(pad1, pad2, pad3, pad4);
                }

                if (radius) {
                    ((UILabel) component).setRadius(rad1, rad2, rad3, rad4);
                }
                break;
        }
        if (component != null) {
            component.setMappedUIComponent(this);
            if (parent != null && component instanceof UIConstraint)
                parent.component.add(component);
            else if (parent != null) {
                parent.addLater.add(component);
            }

            if (properties.containsKey("fam")) {
                UIMaster.registerComponent(id, this, (String[]) properties.get("fam"));
                this.groups = (String[]) properties.get("fam");
            } else
                UIMaster.registerComponent(id, this);
            UIMaster.postComponent(id, component);


            component.setIdentifier(this.id);
        }
    }


    public void onAdded(UIComponent parent) {
        for (UIComponent addLater : addLater)
            component.add(addLater);
    }

    private void parseProperty(String id, String value) {
        if (id.startsWith("~")) {
            if (value.equalsIgnoreCase("all")) {
                UIMaster.copyProps(id, properties);
            } else {
                if (value.contains(",")) {
                    String[] split = value.trim().split(",");
                    for (String val : split) {
                        Object obj = UIMaster.prop(id, val);
                        if (obj != null) {
                            properties.put(val, obj);
                        }
                    }
                } else {
                    Object obj = UIMaster.prop(id, value);
                    if (obj != null) {
                        properties.put(value, obj);
                    }
                }
            }
        }
        switch (id.toLowerCase()) {
            case "regx":
                properties.put("regx", value);
                break;
            case "pad":

                if (value.contains(",")) {
                    String[] values = value.trim().split(",");
                    properties.put("pad1", Float.parseFloat(values[0]));
                    properties.put("pad2", Float.parseFloat(values[1]));
                    properties.put("pad3", Float.parseFloat(values[2]));
                    properties.put("pad4", Float.parseFloat(values[3]));
                } else {
                    float radius = Float.parseFloat(value);
                    properties.put("pad1", radius);
                    properties.put("pad2", radius);
                    properties.put("pad3", radius);
                    properties.put("pad4", radius);
                }
                break;
            case "ignore":
                if (value.contains(",")) {
                    String[] values = value.trim().split(",");
                    properties.put("ignore", values.length);
                    for (int i = 0; i < values.length; i++) {
                        properties.put("ignore" + i, values[i]);
                    }
                } else {
                    properties.put("ignore", 1);
                    properties.put("ignore0", value);
                }
                break;
            case "fc":
            case "cc":
            case "phc":
            case "c":
                properties.put(id.toLowerCase(), parseColor(value));
                break;
            case "rad":
                if (value.contains(",")) {
                    String[] values = value.trim().split(",");
                    properties.put("rad1", Float.parseFloat(values[0]));
                    properties.put("rad2", Float.parseFloat(values[1]));
                    properties.put("rad3", Float.parseFloat(values[2]));
                    properties.put("rad4", Float.parseFloat(values[3]));
                } else {
                    float radius = Float.parseFloat(value);
                    properties.put("rad1", radius);
                    properties.put("rad2", radius);
                    properties.put("rad3", radius);
                    properties.put("rad4", radius);
                }
                break;
            case "fam":
                if (value.contains(",")) {
                    String[] values = value.trim().split(",");
                    properties.put("fam", values);
                } else
                    properties.put("fam", new String[]{value});
                break;
            case "x":
            case "y":
            case "w":
            case "h":
                properties.put(id.toLowerCase(), parseConstraint(id, value));
                break;
            case "ti":
                properties.put(id.toLowerCase(), Float.parseFloat(value));
                break;
            case "fs":
                properties.put(id.toLowerCase(), Integer.parseInt(value));
                break;
            case "txt":
            case "ff":
                properties.put(id.toLowerCase(), value);
        }
    }

    private UIColor parseColor(String value) {
        UIColor c = UIColor.copy(UIColor.DARK_GRAY);
        if (value.startsWith("#")) {
            Color color = Color.decode(value);
            c = UIColor.rgba(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        } else if (UIColor.colors.containsKey(value))
            c = UIColor.colors.get(value);

        return c;
    }

    private Constraints.Constraint parseConstraint(String id, String value) {
        String relativeTo = "";
        String data = "";
        if (value.contains("#")) {
            String[] split = value.trim().split("#");
            data = parseConstraintData(split[0]);
            relativeTo = split[1];
        } else
            data = parseConstraintData(value);
        String constraintType = parseConstraintType(value);
        switch (constraintType) {
            case "pixel":
                if (data.isEmpty())
                    return new Constraints.PixelConstraint(0);
                else {
                    double dataVal = Double.parseDouble(data);
                    return new Constraints.PixelConstraint((float) dataVal);
                }
            case "center":
                if (data.isEmpty())
                    return new Constraints.CenterConstraint();
                else {
                    double dataVal = Double.parseDouble(data);
                    return new Constraints.CenterConstraint(dataVal);
                }

            case "stick":
                if (data.isEmpty() && id.equalsIgnoreCase("x")) {
                    Constraints.StickyConstraint c = new Constraints.StickyConstraint(Constraints.StickyConstraint.FACE.LEFT);
                    if (!relativeTo.isEmpty())
                        c.setRelativeConstraintID(relativeTo);
                    return c;
                } else if (data.isEmpty() && id.equalsIgnoreCase("y")) {
                    Constraints.StickyConstraint c = new Constraints.StickyConstraint(Constraints.StickyConstraint.FACE.TOP);
                    if (!relativeTo.isEmpty())
                        c.setRelativeConstraintID(relativeTo);
                    return c;
                } else {
                    String sticky = data;
                    Constraints.StickyConstraint c = null;
                    if (data.contains(",")) {
                        String[] split = data.trim().split(",");
                        sticky = split[0].trim();
                        String subConstraintType = parseConstraintType(split[1].trim());
                        String subConstraintData = parseConstraintData(split[1].trim());
                        if (subConstraintType.equalsIgnoreCase("pixel"))
                            c = new Constraints.StickyConstraint(Constraints.StickyConstraint.FACE.parse(sticky), new Constraints.PixelConstraint((float) Double.parseDouble(subConstraintData)));
                        else if (subConstraintType.equalsIgnoreCase("rel"))
                            c = new Constraints.StickyConstraint(Constraints.StickyConstraint.FACE.parse(sticky), new Constraints.RelativeConstraint((float) Double.parseDouble(subConstraintData)));
                    } else {
                        c = new Constraints.StickyConstraint(Constraints.StickyConstraint.FACE.parse(data));
                    }

                    if (c != null && !relativeTo.isEmpty())
                        c.setRelativeConstraintID(relativeTo);
                    return c;
                }
            case "rel":
                Constraints.RelativeConstraint c = null;
                if (data.isEmpty())
                    c = new Constraints.RelativeConstraint();
                else {
                    if (data.contains(",")) {
                        String[] split = data.trim().split(",");
                        String subMinConstraint = parseConstraintType(split[1].trim());
                        String subMinConstraintData = parseConstraintData(split[1].trim());
                        if (split.length == 2) {
                            if (id.equalsIgnoreCase("w") || id.equalsIgnoreCase("h")) {
                                c = new Constraints.RelativeConstraint((float) Double.parseDouble(split[0].trim()), new Constraints.PixelConstraint((float) Double.parseDouble(subMinConstraintData)));
                            } else {
                                c = new Constraints.RelativeConstraint((float) Double.parseDouble(split[0].trim()), new Constraints.PixelConstraint((float) Double.parseDouble(subMinConstraintData)), new Constraints.PixelConstraint(0));
                            }
                        } else if (split.length == 3) {
                            String subMaxConstraint = parseConstraintType(split[2].trim());
                            String subMaxConstraintData = parseConstraintData(split[2].trim());
                            c = new Constraints.RelativeConstraint((float) Double.parseDouble(split[0].trim()), new Constraints.PixelConstraint((float) Double.parseDouble(subMinConstraintData.trim())), new Constraints.PixelConstraint((float) Double.parseDouble(subMaxConstraintData.trim())));
                        }

                    } else
                        c = new Constraints.RelativeConstraint((float) Double.parseDouble(data));
                }

                if (!relativeTo.isEmpty())
                    c.setRelativeConstraintID(relativeTo);
                return c;
        }
        return null;
    }

    private String parseConstraintType(String value) {
        return value.split("\\((.*?)\\)")[0].toLowerCase();
    }

    private String parseConstraintData(String value) {
        String data = "";
        if (value.contains("(") && value.contains(")"))
            data = value.substring(value.indexOf("(") + 1, value.lastIndexOf(")"));
        return data;
    }


    /**
     * Gets all of the properties that are not UIComponents, and maps them to the correct values
     */
    private void parseProperties() {
        Map<String, JsonElement> properties = parser.getComponents(element).map;
        for (String key : properties.keySet()) {
            UIType type = parser.getType(key);
            if (type.equals(UIType.UIUNKOWN)) {
                parseProperty(key, properties.get(key).getAsString());
            }
        }
    }


}
