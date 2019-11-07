package me.jraynor.pong.render;

import me.jraynor.misc.Camera;
import me.jraynor.pong.entities.*;
import me.jraynor.pong.physics.EntityBall;
import me.jraynor.pong.physics.EntityPhysics;
import me.jraynor.pong.physics.PhysicalWorld;
import org.joml.Vector3f;

public class PongManager extends Renderer {
    private EntityPaddle player1Paddle;
    private EntityGoal player1Goal;
    private EntityPaddle player2Paddle;
    private EntityGoal player2Goal;
    private EntityBall ball;
    private PhysicalWorld physicalWorld;

    public PongManager(PhysicalWorld physicalWorld, Camera camera) {
        super(camera);
        this.physicalWorld = physicalWorld;
    }

    @Override
    public void init() {
        addEntity(new EntityOther("field.obj", "floor.png", new Vector3f(0, -0.1f, 0), new Vector3f(0, 90, 0), new Vector3f(5)));

        ball = new EntityBall("green.png", new Vector3f(0, 200, 0), 10, 0.2f);
        addEntity(ball);
        player1Goal = new EntityGoal(false, ball, "red", "goal.obj", "red_white.png", new Vector3f(0, 50, -495), new Vector3f(0, 0, 0), new Vector3f(100, 50f, 6), 0);
        player2Goal = new EntityGoal(true, ball, "blue", "goal.obj", "blue.png", new Vector3f(0, 50, 495), new Vector3f(0, 0, 0), new Vector3f(100, 50f, 6), 0);
        player1Goal.setOther(player2Goal);
        player2Goal.setOther(player1Goal);
        addEntity(player1Goal);
        addEntity(player2Goal);
        player1Paddle = new EntityPaddle(ball, new Vector3f(0, 50, -400), new Vector3f(0, 180, 0), new Vector3f(60, 50, 3));
        player2Paddle = new EntityPaddle(new Vector3f(0, 50, 400), new Vector3f(0, 0, 0), new Vector3f(60, 50, 3));
        EntityPhysics boundry1 = new EntityBoundry(new Vector3f(-300, 0, 0), new Vector3f(5, 200, 500));
        EntityPhysics boundry2 = new EntityBoundry(new Vector3f(300, 0, 0), new Vector3f(5, 200, 500));
        EntityPhysics boundry3 = new EntityBoundry(new Vector3f(0, 0, 505), new Vector3f(300, 200, 5));
        EntityPhysics boundry4 = new EntityBoundry(new Vector3f(0, 0, -505), new Vector3f(300, 200, 5));
        addEntity(player1Paddle);
        addEntity(player2Paddle);
        addEntity(boundry1);
        addEntity(boundry2);
        addEntity(boundry3);
        addEntity(boundry4);
        physicalWorld.addEntity(new EntityPlane(new Vector3f(0, 1, 0)));
        physicalWorld.addEntity(boundry1);
        physicalWorld.addEntity(boundry2);
        physicalWorld.addEntity(boundry3);
        physicalWorld.addEntity(boundry4);
        physicalWorld.addEntity(ball);
        physicalWorld.addEntity(player2Goal);
        physicalWorld.addEntity(player1Goal);
        physicalWorld.addEntity(player1Paddle);
        physicalWorld.addEntity(player2Paddle);
        ball.body.applyCentralForce(new javax.vecmath.Vector3f(Math.round(Math.random() * 30), 0, Math.round(Math.random() * 30)));
        ball.body.setDamping(0, 0);
        super.init();
    }


}
