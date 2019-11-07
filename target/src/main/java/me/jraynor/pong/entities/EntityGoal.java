package me.jraynor.pong.entities;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.misc.Model;
import me.jraynor.misc.ModelLoader;
import me.jraynor.misc.Texture;
import me.jraynor.pong.Pong;
import me.jraynor.pong.physics.EntityBall;
import me.jraynor.pong.physics.EntityPhysics;
import org.joml.Vector3f;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

public class EntityGoal extends EntityPhysics {
    private String textureName;
    private javax.vecmath.Vector3f iniertia = new javax.vecmath.Vector3f();
    @Getter
    @Setter
    private int points = 0;
    @Getter
    private String goalName;
    private EntityBall ball;
    private boolean npc = false;
    @Getter
    private EntityGoal otherGoal;

    public EntityGoal(boolean npc, EntityBall ball, String goalName, String modelName, String textureName, Vector3f location, Vector3f rotation, Vector3f scale, float mass) {
        super(modelName, textureName, location, scale);
        this.npc = npc;
        this.ball = ball;
        this.goalName = goalName;
        this.rotation = rotation;
        this.textureName = textureName;
        this.mass = mass;
    }

    public EntityGoal(String goalName, String modelName, String textureName, Vector3f location, Vector3f scale, float mass) {
        super(modelName, textureName, location, scale);
        this.goalName = goalName;
        this.textureName = textureName;
        this.mass = mass;
    }


    @Override
    public void init() {
        super.init();
        setTexture(Texture.loadTexture(textureName));
    }

    @Override
    protected RigidBodyConstructionInfo initRigidBodyInfo(MotionState state, float mass) {
        javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f();
        shape.calculateLocalInertia(mass, inertia);
        RigidBodyConstructionInfo rigidBodyConstructionInfo = new RigidBodyConstructionInfo(mass, new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new javax.vecmath.Vector3f(location.x, location.y, location.z), 1.0f))), shape, inertia);
        rigidBodyConstructionInfo.restitution = 1f;
        rigidBodyConstructionInfo.angularDamping = 0.95f;
        return rigidBodyConstructionInfo;
    }

    @Override
    protected Model initModel() {
        return new Model(ModelLoader.loadModel("goal.obj"));
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (points >= 7) {
            ball.setPosition(new org.joml.Vector3f(0, 100, 0));
            if (npc)
                Pong.pongUI.reset("Sorry, you lost: " + points + "/7");
            else
                Pong.pongUI.reset("Congrats, you won!: " + points + "/7");

        }
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

    public void setOther(EntityGoal player2Goal) {
        this.otherGoal = player2Goal;
    }
}
