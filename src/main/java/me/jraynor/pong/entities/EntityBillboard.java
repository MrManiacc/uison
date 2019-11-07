package me.jraynor.pong.entities;

import me.jraynor.misc.Model;
import me.jraynor.misc.Shader;
import me.jraynor.misc.Texture;
import me.jraynor.misc.Vao;
import me.jraynor.pong.Pong;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class EntityBillboard extends EntityRenderable {
    private static Vao vao;
    private float currentStep;
    private Texture texture;
    private List<float[]> uvList = new ArrayList<>();

    public EntityBillboard(Vector3f location, Vector2f scale) {
        super(location, new Vector3f(scale.x, scale.y, 1.0f));
    }

    @Override
    public void init() {
        super.init();
        this.texture = Texture.loadTexture("winner.png");
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                uvList.add(getUV(x, y));
            }
        }
    }

    private float UNIT = 1.0f / 8.0f;

    public float[] getUV(int x, int y) {
        float[] uv = new float[2 * 4];
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0://top left vertex
                    uv[0] = x * UNIT;
                    uv[1] = y * UNIT;
                    uv[1] = 1 - uv[1];
                    break;
                case 1:
                    uv[2] = x * UNIT;
                    uv[3] = (y + 1) * UNIT;
                    uv[3] = 1 - uv[3];
                    break;
                case 2:
                    uv[4] = (x + 1) * UNIT;
                    uv[5] = y * UNIT;
                    uv[5] = 1 - uv[5];
                    break;
                case 3:
                    uv[6] = (x + 1) * UNIT;
                    uv[7] = (y + 1) * UNIT;
                    uv[7] = 1 - uv[7];
                    break;
            }
        }
        return uv;
    }

    @Override
    public void render(Shader shader) {
        if (!shader.isStarted())
            shader.start();
        if (texture != null)
            texture.bindToUnit(2);
        Matrix4f viewMatrix = Pong.player.getCamera().getViewMatrix();
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.translate(location);
        modelMatrix.m00(viewMatrix.m00());
        modelMatrix.m01(viewMatrix.m10());
        modelMatrix.m02(viewMatrix.m20());

        modelMatrix.m10(viewMatrix.m01());
        modelMatrix.m11(viewMatrix.m11());
        modelMatrix.m12(viewMatrix.m21());

        modelMatrix.m20(viewMatrix.m02());
        modelMatrix.m21(viewMatrix.m12());
        modelMatrix.m22(viewMatrix.m22());

        modelMatrix.scale(scale.x, scale.y, 1);
        modelMatrix = viewMatrix.mul(modelMatrix, modelMatrix);
        shader.loadMat4("modelMatrix", modelMatrix);
//        model.renderBillboard();
    }

    @Override
    protected Model initModel() {
        if (vao != null)
            return new Model(vao);
        else {
            vao = Vao.create();
            vao.bind(0, 1);
            vao.createAttribute(0, new float[]{
                    -0.5f, 0.5f, 0f,//v0
                    -0.5f, -0.5f, 0f,//v1
                    0.5f, -0.5f, 0f,//v2
                    0.5f, 0.5f, 0f,}, 3);
            vao.createAttribute(1, new float[]{
                    0,0,0,
                    1,1,1,
                    3,2,2,
                    2,3,3}, 3);
            vao.createIndexBuffer(new int[]{0, 1, 3,//top left triangle (v0, v1, v3)
                    3, 1, 2});
            vao.unbind(0, 1);
            return new Model(vao);
        }
    }
}
