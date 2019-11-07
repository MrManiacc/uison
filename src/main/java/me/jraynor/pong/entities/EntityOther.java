package me.jraynor.pong.entities;

import me.jraynor.misc.Model;
import me.jraynor.misc.ModelLoader;
import me.jraynor.misc.Texture;
import me.jraynor.misc.Vao;
import org.joml.Vector3f;

public class EntityOther extends EntityRenderable {
    private String modelName;
    private String textureName;

    public EntityOther(String modelName, Vector3f location, Vector3f rotation, Vector3f scale) {
        super(location, rotation, scale);
        this.modelName = modelName;
    }

    public EntityOther(String modelName, String textureName, Vector3f location, Vector3f rotation, Vector3f scale) {
        this(modelName, location, rotation, scale);
        this.textureName = textureName;
        this.setTexture(Texture.loadTexture(textureName));
    }

    @Override
    protected Model initModel() {
        return new Model(ModelLoader.loadModel(modelName));
    }
}
