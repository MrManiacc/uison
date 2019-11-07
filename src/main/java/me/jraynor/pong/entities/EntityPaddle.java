package me.jraynor.pong.entities;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import me.jraynor.misc.Input;
import me.jraynor.misc.Model;
import me.jraynor.misc.ModelLoader;
import me.jraynor.pong.physics.EntityBall;
import me.jraynor.pong.physics.EntityPhysics;
import org.joml.Vector3f;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class EntityPaddle extends EntityPhysics {
    private final Vector3f startingPosition;
    private EntityBall entityBall;

    private long current = 0;
    private Vector3f newPosition;
    private Random random = new Random();
    public EntityPaddle(EntityBall ball, Vector3f location, Vector3f rotation, Vector3f scale) {
        super("goal.obj", "paddle.png", location, scale);
        this.entityBall = ball;
        this.startingPosition = new Vector3f(location);
        this.mass = 10f;
        this.newPosition = new Vector3f(startingPosition.x, startingPosition.y, startingPosition.z);
    }

    public EntityPaddle(Vector3f location, Vector3f rotation, Vector3f scale) {
        super("goal.obj", "paddle.png", location, scale);
        this.startingPosition = new Vector3f(location);
        this.mass = 10f;
    }

    @Override
    public void init() {
        super.init();
    }


    @Override
    protected RigidBodyConstructionInfo initRigidBodyInfo(MotionState state, float mass) {
        javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f();
        shape.calculateLocalInertia(mass, inertia);
        RigidBodyConstructionInfo rigidBodyConstructionInfo = new RigidBodyConstructionInfo(mass, new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new javax.vecmath.Vector3f(location.x, location.y, location.z), 1.0f))), shape, inertia);
        rigidBodyConstructionInfo.restitution = 0f;
        rigidBodyConstructionInfo.angularDamping = 1f;
        return rigidBodyConstructionInfo;
    }

    @Override
    protected CollisionShape initShape() {
        return new BoxShape(new javax.vecmath.Vector3f(scale.x, scale.y, scale.z));
    }


    @Override
    protected RigidBody initRigidBody() {
        RigidBody rigidBody = new RigidBody(rigidBodyInfo);
        rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        rigidBody.setUserPointer(this);
        return rigidBody;
    }


    @Override
    protected Model initModel() {
        return new Model(ModelLoader.loadModel("goal.obj"));
    }

    private Transform transform = new Transform();
    private int max = 2000;
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);


        if (entityBall == null) {
            if (Input.keyDown(GLFW_KEY_UP) || Input.keyDown(GLFW_KEY_E)) {
                body.setLinearVelocity(new javax.vecmath.Vector3f(10, 0, 0));
                transform.setIdentity();
                body.getCenterOfMassTransform(transform);
                transform.set(new Matrix4f(new Quat4f(0, 0, 0, 1), new javax.vecmath.Vector3f(transform.origin.x, transform.origin.y, startingPosition.z), 1.0f));
                body.setCenterOfMassTransform(transform);
            } else if (Input.keyDown(GLFW_KEY_DOWN) || Input.keyDown(GLFW_KEY_Q)) {
                body.setLinearVelocity(new javax.vecmath.Vector3f(-10, 0, 0));
                transform.setIdentity();
                body.getCenterOfMassTransform(transform);
                transform.set(new Matrix4f(new Quat4f(0, 0, 0, 1), new javax.vecmath.Vector3f(transform.origin.x, transform.origin.y, startingPosition.z), 1.0f));
                body.setCenterOfMassTransform(transform);
            }
        } else {
            if (current <= max)
                current += deltaTime;
            else {
                newPosition = new Vector3f(entityBall.location.x, startingPosition.y, startingPosition.z);
                max = random.nextInt(5000);
                current = 0;
            }

//            this.location.lerp(newPosition, deltaTime);
            Vector3f lerp = this.location.lerp(newPosition, deltaTime * 0.001f);
            body.getCenterOfMassTransform(transform);
            transform.set(new Matrix4f(new Quat4f(0, 0, 0, 1), new javax.vecmath.Vector3f(lerp.x, lerp.y, lerp.z), 1.0f));
            body.setCenterOfMassTransform(transform);
        }

    }

}
