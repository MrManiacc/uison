package me.jraynor.pong.physics;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import me.jraynor.misc.Input;
import org.joml.Random;
import org.joml.Vector3f;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class EntityBall extends EntityPhysics {
    private javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f();
    private Random random = new Random(Math.round(Math.random() * 2000));

    public EntityBall(String textureName, Vector3f location, float radius, float mass) {
        super("ball.obj", textureName, location, new Vector3f(radius, radius, radius));
        this.mass = mass;
    }


    @Override
    protected RigidBodyConstructionInfo initRigidBodyInfo(MotionState state, float mass) {
        state.setWorldTransform(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1.0f), new javax.vecmath.Vector3f(location.x, location.y, location.z), 1.0f)));
        RigidBodyConstructionInfo rigidBodyConstructionInfo = new RigidBodyConstructionInfo(mass, state, shape, inertia);
        rigidBodyConstructionInfo.restitution = 0.5f;
        rigidBodyConstructionInfo.angularDamping = 0.01f;
        rigidBodyConstructionInfo.friction = 0;
        return rigidBodyConstructionInfo;
    }

    @Override
    public void init() {
        super.init();
        shape.calculateLocalInertia(mass, inertia);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (Input.keyPressed(GLFW_KEY_SPACE)) {
            body.applyCentralForce(new javax.vecmath.Vector3f(Math.round(Math.random() * 100), 0, Math.round(Math.random() * 100)));
            body.setDamping(0, 0);
        }
    }

    @Override
    protected CollisionShape initShape() {
        return new SphereShape(scale.x / 2);
    }

    @Override
    protected RigidBody initRigidBody() {
        RigidBody rigidBody = new RigidBody(rigidBodyInfo);
        rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        rigidBody.activate(true);
        return rigidBody;
    }


}
