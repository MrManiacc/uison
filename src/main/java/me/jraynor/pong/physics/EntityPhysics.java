package me.jraynor.pong.physics;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import lombok.Getter;
import me.jraynor.misc.Model;
import me.jraynor.misc.ModelLoader;
import me.jraynor.misc.Shader;
import me.jraynor.misc.Texture;
import me.jraynor.pong.entities.EntityRenderable;
import org.joml.Vector3f;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

public abstract class EntityPhysics extends EntityRenderable {
    private String modelName;
    protected Vector3f velocity, acceleration;
    protected float mass;
    protected CollisionShape shape;
    public RigidBody body;
    private boolean render;
    protected RigidBodyConstructionInfo rigidBodyInfo;
    private MotionState motionState;

    @Getter
    private boolean integrated = false;

    @Getter
    protected Model bbModel;

    public EntityPhysics(String modelName, String textureName, Vector3f location, Vector3f scale) {
        super(location, scale);
        this.modelName = modelName;
        setTexture(Texture.loadTexture(textureName));
        render = true;
    }

    public EntityPhysics(Vector3f location, Vector3f scale) {
        super(location, scale);
        render = false;
    }

    public void integrate(DynamicsWorld dynamicsWorld) {
        dynamicsWorld.addRigidBody(body);
        integrated = true;
    }

    @Override
    public void render(Shader shader) {
        if (render)
            super.render(shader);
    }

    public void initPhysics() {
        shape = initShape();
        motionState = new DefaultMotionState(new Transform(new Matrix4f(
                new Quat4f(0, 0, 0, 1),
                new javax.vecmath.Vector3f(location.x, location.y, location.z),
                1.0f)));
        rigidBodyInfo = initRigidBodyInfo(motionState, mass);
        body = initRigidBody();
        body.setUserPointer(this);
    }

    public void applyForce(Vector3f force) {
        body.applyImpulse(new javax.vecmath.Vector3f(force.x, force.y, force.z), new javax.vecmath.Vector3f(0, 0, 0));
    }

    public void setPosition(Vector3f location) {
        this.location = location;
        Transform transform = new Transform();
        body.setLinearVelocity(new javax.vecmath.Vector3f(0, 0, 0));
        transform.setIdentity();
        body.getCenterOfMassTransform(transform);
        transform.set(new Matrix4f(new Quat4f(0, 0, 0, 1), new javax.vecmath.Vector3f(location.x, location.y, location.z), 1.0f));
        body.setCenterOfMassTransform(transform);
    }

    /**
     * Overridable method to render a vao
     */
    public void renderDebug(Shader shader) {
        if (!shader.isStarted())
            shader.start();
        if (texture != null)
            texture.bindToUnit(2);
        shader.loadMat4("transformMatrix", transformationMatrix);
        bbModel.render();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
//        location.y+= deltaTime * 10f;
    }


    protected abstract CollisionShape initShape();

    protected abstract RigidBody initRigidBody();


    @Override
    public void init() {
        if (render) {
            super.init();
            Vector3f min = model.getFirst().getMin();
            Vector3f max = model.getFirst().getMax();

            min.x += location.x;
            max.x += location.x;

            min.y += location.y;
            max.y += location.y;

            min.z += location.z;
            max.z += location.z;
        }

    }

    protected abstract RigidBodyConstructionInfo initRigidBodyInfo(MotionState state, float mass);


    @Override
    protected Model initModel() {
        this.bbModel = new Model(ModelLoader.loadModel("cube.obj"));
        return new Model(ModelLoader.loadModel(modelName));
    }
}
