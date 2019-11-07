package me.jraynor.pong.entities;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import me.jraynor.pong.physics.EntityPhysics;
import org.joml.Vector3f;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

public class EntityBoundry extends EntityPhysics {
    public EntityBoundry(Vector3f location, Vector3f scale) {
        super(location, scale);
    }

    @Override
    protected CollisionShape initShape() {
        return new BoxShape(new javax.vecmath.Vector3f(scale.x, scale.y, scale.z));
    }

    protected RigidBody initRigidBody() {
        RigidBody rigidBody = new RigidBody(rigidBodyInfo);
        rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        return rigidBody;
    }

    @Override
    protected RigidBodyConstructionInfo initRigidBodyInfo(MotionState state, float mass) {
        javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f();
        shape.calculateLocalInertia(mass, inertia);
        RigidBodyConstructionInfo rigidBodyConstructionInfo = new RigidBodyConstructionInfo(mass, new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new javax.vecmath.Vector3f(location.x, location.y, location.z), 1.0f))), shape, inertia);
        rigidBodyConstructionInfo.restitution = 1f;
        rigidBodyConstructionInfo.angularDamping = 0.95f;
        rigidBodyConstructionInfo.friction = 0.0001f;

        return rigidBodyConstructionInfo;
    }
}
