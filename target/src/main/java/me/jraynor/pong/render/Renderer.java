package me.jraynor.pong.render;

import me.jraynor.misc.Camera;
import me.jraynor.misc.GLUtils;
import me.jraynor.misc.Shader;
import me.jraynor.misc.ShaderBind;
import me.jraynor.pong.entities.Entity;
import me.jraynor.pong.physics.EntityPhysics;
import me.jraynor.pong.entities.EntityRenderable;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glEnable;

public abstract class Renderer {
    protected Shader shader, debugShader;
    protected Map<Class, ArrayList<Entity>> entityLists;
    protected Camera camera;
    protected Class[] loadedEntityTypes;

    public Renderer(Camera camera) {
        this.entityLists = new HashMap<>();
        this.camera = camera;
    }

    public void init() {
        this.shader = initShader("static");
        this.debugShader = initShader("debug");
        shader.loadSampler("diffuseMap", 2);
        debugShader.loadSampler("diffuseMap", 2);
        entityLists.forEach((aClass, entities) -> entities.forEach(Entity::init));
    }

    protected Shader initShader(String name) {
        return new Shader(name) {
            @Override
            protected void doBinds() {
                binds(new ShaderBind("vertex", 0), new ShaderBind("normal", 1), new ShaderBind("uv", 2));
            }

            @Override
            protected void doUniformBinds() {
                bindUniforms("projectionMatrix", "viewMatrix", "transformMatrix", "diffuseMap");
                loadSampler("diffuseMap", 2);
            }
        };
    }

    /**
     * Creates and array of the currently loaded class keys
     *
     * @return
     */
    private Class[] loadedEntityTypes() {
        Set<Class> keys = entityLists.keySet();
        Class[] classes = new Class[keys.size()];
        int counter = 0;
        for (Class key : keys)
            classes[counter++] = key;
        return classes;
    }

    public void render() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        GLUtils.antialias(true);
        GLUtils.enableAlphaBlending();
        if (entityLists.containsKey(EntityRenderable.class)) {
            shader.start();
            shader.loadSampler("diffuseMap", 2);
            shader.loadMat4("projectionMatrix", camera.getProjectionMatrix());
            shader.loadMat4("viewMatrix", camera.getViewMatrix());
            entityLists.get(EntityRenderable.class).forEach(entity -> ((EntityRenderable) entity).render(shader));
            shader.stop();
        }
        if (entityLists.containsKey(EntityPhysics.class)) {
            debugShader.start();
            debugShader.loadSampler("diffuseMap", 2);
            debugShader.loadMat4("projectionMatrix", camera.getProjectionMatrix());
            debugShader.loadMat4("viewMatrix", camera.getViewMatrix());
//            entityLists.get(EntityPhysics.class).forEach(entity -> ((EntityPhysics) entity).renderDebug(debugShader));

            debugShader.stop();
        }


    }

    public void update(float deltaTime) {
        if (loadedEntityTypes != null) {
            for (Class clazz : loadedEntityTypes)
                entityLists.get(clazz).forEach(entity -> entity.update(deltaTime));
        }
    }


    public void addEntity(Entity entity) {
        if (entity instanceof EntityPhysics) {
            if (entityLists.containsKey(EntityPhysics.class))
                entityLists.get(EntityPhysics.class).add(entity);
            else {
                ArrayList<Entity> entities = new ArrayList<>();
                entities.add(entity);
                entityLists.put(EntityPhysics.class, entities);
            }
        }
        if (entity instanceof EntityRenderable) {
            if (entityLists.containsKey(EntityRenderable.class))
                entityLists.get(EntityRenderable.class).add(entity);
            else {
                ArrayList<Entity> entities = new ArrayList<>();
                entities.add(entity);
                entityLists.put(EntityRenderable.class, entities);
            }
        }

        if (entityLists.containsKey(entity.getClass()))
            entityLists.get(entity.getClass()).add(entity);
        else
            entityLists.put(entity.getClass(), new ArrayList<>(Arrays.asList(entity)));
        this.loadedEntityTypes = loadedEntityTypes();
    }

    public boolean removeEntity(Entity entity) {
        if (entityLists.containsKey(entity.getClass())) {
            ArrayList<Entity> entities = entityLists.get(entity.getClass());
            return entities.remove(entity);
        }
        return false;
    }


}
