package me.jraynor.gui.parser;

import com.google.gson.JsonElement;
import lombok.Getter;
import me.jraynor.gui.logic.color.UIColor;
import me.jraynor.gui.logic.constraint.Constraints;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MappedImport {
    @Getter
    private String id;
    private Map<String, Object> properties = new HashMap<>();
    private UIParser parser;
    private JsonElement element;

    public MappedImport(String id, UIParser parser, JsonElement element) {
        this.id = id;
        this.parser = parser;
        this.element = element;
    }

    public void parse() {
        parseProperties();
    }

    public Object prop(String key) {
        return properties.get(key);
    }

    public void copyProps(Map<String, Object> to) {
        for (String key : properties.keySet()){
            to.put(key, properties.get(key));
        }
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

    private void parseProperty(String id, String value) {
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

}

