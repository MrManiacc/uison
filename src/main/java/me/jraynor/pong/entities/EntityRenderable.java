package me.jraynor.pong.entities;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.misc.Model;
import me.jraynor.misc.Shader;
import me.jraynor.misc.Texture;
import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class EntityRenderable extends Entity {
    protected Model model;
    @Getter
    protected Matrix4f transformationMatrix;
    @Getter
    @Setter
    protected Texture texture;

    public EntityRenderable(Vector3f location, Vector3f scale) {
        super(location, scale);
        this.transformationMatrix = new Matrix4f()
                .translate(location.x, location.y, location.z)
                .scale(scale.x, scale.y, scale.z);
    }
    public EntityRenderable(Vector3f location, Vector3f rotation, Vector3f scale) {
        super(location, scale);
        this.rotation = rotation;
        this.transformationMatrix = new Matrix4f()
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .translate(location.x, location.y, location.z)
                .scale(scale.x, scale.y, scale.z);
    }

    @Override
    public void update(float deltaTime) {
        if (transformationMatrix != null) {
            transformationMatrix.identity()
                    .rotateX((float) Math.toRadians(rotation.x))
                    .rotateY((float) Math.toRadians(rotation.y))
                    .rotateZ((float) Math.toRadians(rotation.z))
                    .translate(location.x, location.y, location.z)
                    .scale(scale.x, scale.y, scale.z);
        }
    }

    @Override
    public void init() {
        model = initModel();
    }

    /**
     * Overridable method to render a vao
     */
    public void render(Shader shader) {
        if (!shader.isStarted())
            shader.start();
        if (texture != null)
            texture.bindToUnit(2);
        shader.loadMat4("transformMatrix", transformationMatrix);
        model.render();
    }


    protected abstract Model initModel();

    @Override
    public boolean shouldRender() {
        return true;
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
