package me.jraynor.misc;

import com.bulletphysics.linearmath.IDebugDraw;

import javax.vecmath.Vector3f;

public class PhysicsDebug extends IDebugDraw {
    private Vao lineBuffer;

    public void init() {
        lineBuffer = Vao.create();
        lineBuffer.bind(0, 1);
        lineBuffer.createAttribute(0, toFloatArray(new Vector3f(0, 0, 0), new Vector3f(10, 0, 10)), 3);
        lineBuffer.createAttribute(1, toFloatArray(new Vector3f(0.1f, 0, 0), new Vector3f(0, 0, 0)), 3);
        lineBuffer.unbind(0, 1);
    }

    private float[] toFloatArray(Vector3f... toFloatArray) {
        float[] data = new float[toFloatArray.length * 3];
        for (int i = 0; i < toFloatArray.length; i++) {
            data[i * 3] = toFloatArray[i].x;
            data[i * 3 + 1] = toFloatArray[i].y;
            data[i * 3 + 2] = toFloatArray[i].z;
        }
        return data;
    }

    @Override
    public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
        float[] vertexData = toFloatArray(from, to);
        float[] colorData = toFloatArray(color);
        lineBuffer.bind(0,1);
    }

    @Override
    public void drawContactPoint(Vector3f PointOnB, Vector3f normalOnB, float distance, int lifeTime, Vector3f color) {

    }

    @Override
    public void reportErrorWarning(String warningString) {

    }

    @Override
    public void draw3dText(Vector3f location, String textString) {

    }

    @Override
    public void setDebugMode(int debugMode) {

    }

    @Override
    public int getDebugMode() {
        return 0;
    }
}
