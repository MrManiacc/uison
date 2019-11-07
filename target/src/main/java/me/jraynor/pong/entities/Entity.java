package me.jraynor.pong.entities;

import lombok.Getter;
import me.jraynor.misc.Model;
import me.jraynor.misc.Vao;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class Entity {
    @Getter
    protected Vector3f location, rotation, scale;
    @Getter
    protected Vao model;

    public Entity(Vector3f location, Vector3f scale) {
        this.location = location;
        this.scale = scale;
        this.rotation = new Vector3f();
    }
    public Entity(Vector3f location, Vector3f rotation, Vector3f scale) {
        this.location = location;
        this.scale = scale;
        this.rotation = rotation;
    }

    public void init() {
    }

    public void update(float deltaTime) {

    }


    protected abstract Model initModel();

    public abstract boolean shouldRender();

    public abstract boolean shouldUpdate();


}

