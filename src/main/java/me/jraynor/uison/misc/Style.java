package me.jraynor.uison.misc;

import lombok.Getter;
import me.jraynor.uison.elements.UIBlock;
import me.jraynor.uison.elements.UILabel;
import me.jraynor.uison.elements.UITextBox;
import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.color.UIColor;
import me.jraynor.uison.logic.color.UIImage;
import me.jraynor.uison.parser.Props;
import org.joml.Vector4f;

public class Style {
    @Getter
    private UIColor color, focusedColor, hoverColor, textColor, activeTextColor;
    @Getter
    private Vector4f radius = new Vector4f(0), padding = new Vector4f(0);
    @Getter
    private float fontSize, fontIndent;
    @Getter
    private String textFamily, text;
    @Getter
    private boolean rounded = false, padded = false, filled = false, textured = false;
    @Getter
    private UIImage image;
    @Getter
    private float imageAlpha = 0;


    private Style(UIColor color, UIColor focusedColor, UIColor hoverColor, UIColor textColor, UIColor activeTextColor, Vector4f radius, Vector4f padding, float fontSize, float fontIndent, String textFamily, String text) {
        this.color = color;
        this.focusedColor = focusedColor;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
        this.activeTextColor = activeTextColor;
        this.radius = radius;
        this.padding = padding;
        this.fontSize = fontSize;
        this.fontIndent = fontIndent;
        this.textFamily = textFamily;
        this.text = text;
    }


    public static Style defaultStyle(UIComponent component) {
        if (component instanceof UILabel) {
            return new Style(UIColor.TRANSPARENT, UIColor.TRANSPARENT,
                    UIColor.TRANSPARENT, UIColor.BLACK,
                    UIColor.TRANSPARENT, new Vector4f(0),
                    new Vector4f(2), 18, 5, "regular", "");
        } else if (component instanceof UIBlock) {
            return new Style(UIColor.LIGHT_PURPLE, UIColor.DARK_PURPLE,
                    UIColor.TRANSPARENT, UIColor.TRANSPARENT,
                    UIColor.TRANSPARENT, new Vector4f(0),
                    new Vector4f(0), 0, 0, "", "");
        } else if (component instanceof UITextBox) {
            return new Style(UIColor.DARK_GRAY, UIColor.GRAY,
                    UIColor.GRAY, UIColor.WHITE,
                    UIColor.rgba(60,60,60,200), new Vector4f(0),
                    new Vector4f(0), 0, 0, "", "");
        }
        return new Style(UIColor.LIGHT_PURPLE, UIColor.DARK_PURPLE,
                UIColor.TRANSPARENT, UIColor.TRANSPARENT,
                UIColor.TRANSPARENT, new Vector4f(0),
                new Vector4f(0), 15, 0, "regular", "");
    }


    public void parse(Props props) {
        //Main color - background color
        if (props.has("c")) {
            this.color = props.Color("c");
            this.filled = true;
        } else if (props.has("color")) {
            this.color = props.Color("color");
            this.filled = true;
        }
        //Focused color
        if (props.has("fc"))
            this.focusedColor = props.Color("fc");
        else if (props.has("focusedColor"))
            this.focusedColor = props.Color("focusedColor");

        //Text color
        if (props.has("tc"))
            this.textColor = props.Color("tc");
        else if (props.has("textColor"))
            this.textColor = props.Color("textColor");
        ////System.out.println(textColor);
        //Focused text color
        if (props.has("ftc"))
            this.activeTextColor = props.Color("ftc");
        else if (props.has("focusTextColor"))
            this.activeTextColor = props.Color("focusedTextColor");

        //Hover color
        if (props.has("hc"))
            this.hoverColor = props.Color("hc");
        else if (props.has("hover"))
            this.hoverColor = props.Color("hover");
        else if (props.has("hoverColor"))
            this.hoverColor = props.Color("hoverColor");

        //set the radius/roundness of corners
        this.radius = getVec4(props, "rad", "radius");
        if (radius != null)
            this.rounded = true;
        //set the padding/how much space to push outwards
        this.padding = getVec4(props, "pad", "padding");
        if (padding != null)
            this.padded = true;
        //The font size for the text
        if (props.has("fs")) {
            this.fontSize = props.Int("fs");
        } else if (props.has("fontSize")) {
            this.fontSize = props.Int("fontSize");
        } else if (props.has("size")) {
            this.fontSize = props.Int("size");
        }

        //The font indent
        if (props.has("fi")) {
            this.fontIndent = props.Int("fi");
        } else if (props.has("fontIndent")) {
            this.fontIndent = props.Int("fontIndent");
        } else if (props.has("indent")) {
            this.fontIndent = props.Int("indent");
        }

        //The text family or the type of text
        if (props.has("tf")) {
            this.textFamily = props.String("tf");
        } else if (props.has("textFamily")) {
            this.textFamily = props.String("textFamily");
        }
        //The text for the element
        if (props.has("txt"))
            this.text = props.String("txt");
        else if (props.has("text"))
            this.text = props.String("text");

        if (props.has("background"))
            this.image = props.Image("background");
        else if (props.has("bg"))
            this.image = props.Image("bg");
        if (image != null)
            textured = true;

        if (props.has("opacity"))
            this.imageAlpha = props.Float("opacity");
        else if (props.has("alpha"))
            this.imageAlpha = props.Float("alpha");
        if (textured)
            this.image.setOpacity(imageAlpha);

    }


    private Vector4f getVec4(Props props, String... ids) {
        for (String val : ids) {
            if (props.has(val)) {
                Object o = props.get(val);
                if (o instanceof Integer) {
                    return new Vector4f((int) o);
                } else if (o instanceof int[]) {
                    int[] array = (int[]) o;
                    return new Vector4f(array[0], array[1], array[2], array[3]);
                } else if (o instanceof Float)
                    return new Vector4f((float) o);
                else if (o instanceof float[]) {
                    float[] array = (float[]) o;
                    return new Vector4f(array[0], array[1], array[2], array[3]);
                }
            }
        }
        return null;
    }
}
