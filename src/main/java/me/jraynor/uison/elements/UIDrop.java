package me.jraynor.uison.elements;

import me.jraynor.uison.logic.UIComponent;
import me.jraynor.uison.logic.color.UIColor;
import me.jraynor.uison.misc.UIRenderable;
import org.joml.Vector4f;

public class UIDrop extends UIComponent implements UIRenderable {
    private UIColor fontColor = UIColor.WHITE;
    private UIColor backgroundColor = UIColor.BLACK;
    private float fontSize = 18;
    private String[] elements = new String[]{"N/A"};
    private Vector4f round = new Vector4f();

    @Override
    protected void onAdded() {
        super.onAdded();
        if (props.has("fc"))
            this.fontColor = (UIColor) props.get("fc");
        if (props.has("fs"))
            this.fontSize = (float) props.get("fs");
        if (props.has("vals"))
            this.elements = (String[]) props.get("vals");
        if (props.has("rad"))
            this.elements = (String[]) props.get("vals");
        if (props.has("c"))
            this.backgroundColor = (UIColor) props.get("c");
    }

    @Override
    protected void render() {
    }
}
