package me.jraynor.pong.entities;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.MotionState;
import me.jraynor.pong.physics.EntityPhysics;
import org.joml.Vector3f;

public class EntityPlane extends EntityPhysics {

    public EntityPlane(Vector3f normal) {
        super(normal, new Vector3f(1));
    }

    @Override
    protected CollisionShape initShape() {
        return new StaticPlaneShape(new javax.vecmath.Vector3f(location.x, location.y, location.z), 0.25f);
    }

    @Override
    protected RigidBody initRigidBody() {
        return new RigidBody(rigidBodyInfo);
    }


    @Override
    protected RigidBodyConstructionInfo initRigidBodyInfo(MotionState state, float mass) {
        RigidBodyConstructionInfo rigidBodyConstructionInfo = new RigidBodyConstructionInfo(0, state, shape, new javax.vecmath.Vector3f(0, 0, 0));
        rigidBodyConstructionInfo.restitution = 0.25f;
        return rigidBodyConstructionInfo;
    }
}
