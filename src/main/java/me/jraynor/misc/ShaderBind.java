package me.jraynor.misc;

/**
 * Used to link together a vao location with it's name in the shader
 */
public class ShaderBind {
    private String name;
    private int location;

    public ShaderBind(String name, int location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public int getLocation() {
        return location;
    }
}
