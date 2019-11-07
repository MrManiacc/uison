package me.jraynor.pong.physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import me.jraynor.pong.Pong;
import me.jraynor.pong.entities.EntityBoundry;
import me.jraynor.pong.entities.EntityGoal;
import me.jraynor.pong.entities.EntityPaddle;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.random;

public class PhysicalWorld {
    private List<EntityPhysics> physicalEntities = new ArrayList<>();
    private DynamicsWorld dynamicsWorld;
    private boolean initialized = false;
    private Set<RigidBody> rigidBodies = new HashSet<>();
    private CollisionDispatcher dispatcher;

    public void init() {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        dispatcher = new CollisionDispatcher(collisionConfiguration);
        ConstraintSolver solver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3f(0, -5f, 0));
        dynamicsWorld.setInternalTickCallback(new InternalTickCallback() {
            @Override
            public void internalTick(DynamicsWorld world, float timeStep) {
                PhysicalWorld.this.internalTick(timeStep);
            }
        }, null);
        initialized = true;
    }

    private void internalTick(float timeStep) {
        int manifoldCount = dispatcher.getNumManifolds();
        for (int i = 0; i < manifoldCount; i++) {
            PersistentManifold manifold = dispatcher.getManifoldByIndexInternal(i);
            RigidBody object1 = (RigidBody) manifold.getBody0();
            RigidBody object2 = (RigidBody) manifold.getBody1();
            EntityPhysics entity1Physics = (EntityPhysics) object1.getUserPointer();
            EntityPhysics entity2Physics = (EntityPhysics) object2.getUserPointer();
            boolean hit = false;
            Vector3f normal = null;
            for (int j = 0; j < manifold.getNumContacts(); j++) {
                ManifoldPoint contactPoint = manifold.getContactPoint(j);
                if (contactPoint.getDistance() < 0.0f) {
                    hit = true;
                    normal = contactPoint.normalWorldOnB;
                    break;
                }
            }
            if (hit) {
                // Collision happened between physicsObject1 and physicsObject2. Collision normal is in variable 'normal'.
                if (entity1Physics instanceof EntityBoundry && !(entity2Physics instanceof EntityPaddle)) {
                    entity2Physics.applyForce(new org.joml.Vector3f((float) (-normal.x * (random() * 2)), -normal.y, (float) (-normal.z * (random() * 2))));
                    System.out.println("force!");
                }
                float multiplier = 2;
                if (entity1Physics instanceof EntityPaddle && entity2Physics instanceof EntityBall) {
                    System.out.println("collision!");
                    entity2Physics.applyForce(new org.joml.Vector3f(normal.x * multiplier, normal.y * multiplier, normal.z * multiplier));
                } else if (entity2Physics instanceof EntityPaddle && entity1Physics instanceof EntityBall) {
                    System.out.println("collision!");
                    entity1Physics.applyForce(new org.joml.Vector3f(normal.x * multiplier, normal.y * multiplier, normal.z * multiplier));
                } else if (entity1Physics instanceof EntityGoal) {
                    EntityGoal goal = (EntityGoal) entity1Physics;
                    EntityBall ball = (EntityBall) entity2Physics;
                    goal.setPoints(goal.getPoints() + 1);

                    object2.translate(new Vector3f(0, 10, 0));
                } else if (entity2Physics instanceof EntityGoal) {

                    EntityGoal goal = (EntityGoal) entity2Physics;
                    EntityBall ball = (EntityBall) entity1Physics;

                    goal.setPoints(goal.getPoints() + 1);

                    ball.body.applyCentralForce(new javax.vecmath.Vector3f(Math.round(Math.random() * 30), 0, Math.round(Math.random() * 30)));
                    ball.body.setDamping(0, 0);

                    entity1Physics.setPosition(new org.joml.Vector3f(0, 100, 0));

                    if (goal.getGoalName().equalsIgnoreCase("red"))
                        Pong.pongUI.getXPos().setText(goal.getGoalName() + ": " + goal.getPoints() + "/7");
                    else
                        Pong.pongUI.getYPos().setText(goal.getGoalName() + ": " + goal.getPoints() + "/7");


                }

            }
        }
    }

    public void addEntity(EntityPhysics entityPhysics) {
        if (initialized) {
            entityPhysics.initPhysics();
            entityPhysics.integrate(dynamicsWorld);
            rigidBodies.add(entityPhysics.body);
            physicalEntities.add(entityPhysics);
        }

    }

    public void update(float delta) {
        if (initialized) {
            dynamicsWorld.stepSimulation(1.0f / 60.0f);


            for (EntityPhysics entity : physicalEntities) {
                Vector3f location = entity.body.getWorldTransform(new Transform()).origin;
                entity.getLocation().x = location.x;
                entity.getLocation().y = location.y;
                entity.getLocation().z = location.z;
            }

        }
    }
}
