package me.jraynor.uison.parser;

import lombok.Getter;
import me.jraynor.uison.UIMaster;
import me.jraynor.uison.logic.color.UIColor;
import me.jraynor.uison.logic.color.UIImage;
import me.jraynor.uison.logic.constraint.Constraints;
import me.jraynor.uison.parser.parser.ParserJson;
import org.lwjgl.nanovg.NanoVG;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Props {
    @Getter
    private Map<String, Object> props;
    private Map<String, Prop<?>> propTypes;
    private String id;
    private ParserJson parser;

    public Props(String id, ParserJson parser) {
        this.id = id;
        this.props = new HashMap<>();
        this.parser = parser;
    }

    public void parse(ParserJson.OrderedComponent oc) {
        for (String key : oc.map.keySet()) {
            UIType type = parser.getType(key);
            if (type.equals(UIType.UIUNKOWN)) {
                parseProperty(key, oc.map.get(key).getAsString());
            }
        }
    }

    public boolean has(String id) {
        return props.containsKey(id.toLowerCase());
    }

    public boolean is(String id, Class type) {
        if (!has(id)) return false;
        return type.getName().equals(get(id).getClass().getName());
    }

    public Object get(String id) {
        return props.get(id.toLowerCase());
    }

    public UIColor Color(String id) {
        return (UIColor) get(id);
    }

    public Constraints.Constraint Constraint(String id) {
        return (Constraints.Constraint) get(id);
    }

    public float Float(String id) {
        return (float) get(id);
    }

    public int Int(String id) {
        return (int) get(id);
    }

    public String String(String id) {
        return (String) get(id);
    }

    public int[] IntArray(String id) {
        return (int[]) get(id);
    }

    public float[] FloatArray(String id) {
        return (float[]) get(id);
    }

    public String[] StringArray(String id) {
        return (String[]) get(id);
    }

    public UIImage Image(String id) {
        return (UIImage) props.get(id);
    }

    private void parseProperty(String id, String value) {
        if (id.startsWith("~")) {
            if (value.equalsIgnoreCase("all")) {
                UIMaster.copyProps(id, props);
            } else {
                if (value.contains(",")) {
                    String[] split = value.trim().split(",");
                    for (String val : split) {
                        Object obj = UIMaster.prop(id, val);
                        if (obj != null) {
                            props.put(val, obj);
                        }
                    }
                } else {
                    Object obj = UIMaster.prop(id, value);
                    if (obj != null) {
                        props.put(value, obj);
                    }
                }
            }
        } else {
            if (!isColor(id.toLowerCase(), value)) {
                if (!isNumber(id.toLowerCase(), value)) {
                    if (!isConstraint(id.toLowerCase(), value)) {
                        if (!isArray(id.toLowerCase(), value)) {
                            if (!isImage(id.toLowerCase(), value)) {
                                if (!id.toLowerCase().equalsIgnoreCase("fam")) {
                                    props.put(id.toLowerCase(), value);
//                                    System.err.println("Unknown value at id: " + id.toLowerCase() + ", '" + value + "', assigning as a string");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isImage(String id, String value) {
        if (id.equalsIgnoreCase("background") || id.equalsIgnoreCase("bg")) {
            UIImage image = new UIImage(value, NanoVG.NVG_IMAGE_GENERATE_MIPMAPS);
            UIMaster.registerImage(value, image);
            props.put(id, image);
            return true;
        }
        return false;
    }

    private boolean isNumber(String id, String value) {
        boolean isNumber;
        Object output = null;
        try {
            output = Integer.parseInt(value);
            isNumber = true;
        } catch (NumberFormatException e) {
            try {
                output = Float.parseFloat(value);
                isNumber = true;
            } catch (NumberFormatException e1) {
                isNumber = false;
            }
        }
        if (isNumber) {
            props.put(id, output);
            //////System.out.println("Found number with id[" + id + "]");
        }
        return isNumber;
    }

    private boolean isArray(String id, String value) {
        if (!value.contains(","))
            return false;
        String[] split = value.trim().split(",");

        ArrayList<Integer> iVals = new ArrayList<>();
        boolean isIntArray = true;
        for (String val : split) {
            try {
                int v = Integer.parseInt(val);
                iVals.add(v);
            } catch (NumberFormatException e) {
                isIntArray = false;
            }
        }

        ArrayList<Float> fVals = new ArrayList<>();
        boolean isFloatArray = true;
        for (String val : split) {
            try {
                float v = Float.parseFloat(val);
                fVals.add(v);
            } catch (NumberFormatException e) {
                isFloatArray = false;
            }
        }


        if (isIntArray) {
            int[] intValues = new int[iVals.size()];
            for (int i = 0; i < intValues.length; i++)
                intValues[i] = iVals.get(i);
            props.put(id, intValues);
            //////System.out.println("Found int array with id[" + id + "]");
            return true;
        } else if (isFloatArray) {
            float[] floatVals = new float[fVals.size()];
            for (int i = 0; i < floatVals.length; i++)
                floatVals[i] = fVals.get(i);
            props.put(id, floatVals);
            //////System.out.println("Found float array with id[" + id + "]");
            return true;
        } else {
            props.put(id, split);
            return true;
        }
    }

    private boolean isColor(String id, String value) {
        UIColor c = UIColor.copy(UIColor.DARK_GRAY);
        boolean isColor = false;
        if (value.startsWith("#")) {
            Color color = Color.decode(value);
            c = UIColor.rgba(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            isColor = true;
        } else if (UIColor.colors.containsKey(value)) {
            c = UIColor.colors.get(value);
            isColor = true;
        }
        if (isColor) {
            props.put(id, c);
            //////System.out.println("Found color with id[" + id + "]");
        }
        return isColor;
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


    private boolean isConstraint(String id, String value) {
        String constraintType = parseConstraintType(value);
        boolean isConstraint = (constraintType.equalsIgnoreCase("pixel") ||
                constraintType.equalsIgnoreCase("center") ||
                constraintType.equalsIgnoreCase("stick")) ||
                constraintType.equalsIgnoreCase("rel");
        if (isConstraint) {
            Constraints.Constraint constraint = parseConstraint(id, value);
            props.put(id, constraint);
        }
        return isConstraint;
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

    private UIColor parseColor(String value) {
        UIColor c = UIColor.copy(UIColor.DARK_GRAY);
        if (value.startsWith("#")) {
            Color color = Color.decode(value);
            c = UIColor.rgba(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        } else if (UIColor.colors.containsKey(value))
            c = UIColor.colors.get(value);

        return c;
    }

}
