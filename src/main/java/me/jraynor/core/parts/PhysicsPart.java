package me.jraynor.core.parts;

import com.badlogic.ashley.core.Component;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import org.joml.Vector3f;

public class PhysicsPart implements Component {
    public Vector3f velocity, acceleration, mass;
    public final CollisionShape collisionShape;
    public final RigidBody rigidBody;
    public final MotionState motionState;


    public PhysicsPart(CollisionShape collisionShape, RigidBody rigidBody, MotionState motionState) {
        this.velocity = new Vector3f();
        this.mass = new Vector3f();
        this.acceleration = new Vector3f();
        this.collisionShape = collisionShape;
        this.rigidBody = rigidBody;
        this.motionState = motionState;
    }

}
