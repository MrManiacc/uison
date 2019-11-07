package me.jraynor.misc;

import lombok.Getter;
import org.joml.Matrix4f;

public class Camera {
    @Getter
    private Matrix4f projectionMatrix, viewMatrix;
    private float fov, near, far, aspect;

    public Camera(float fov, float near, float far) {
        this.fov = fov;
        this.near = near;
        this.far = far;
        this.aspect = 1920 / 1080.0f;
        projectionMatrix = new Matrix4f().identity();
        viewMatrix = new Matrix4f().identity();
    }

    public Matrix4f getProjectionMatrix() {
        projectionMatrix.identity();
        return projectionMatrix.setPerspective((float) Math.toRadians(fov), aspect, near, far);
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}
