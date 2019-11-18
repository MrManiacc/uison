package me.jraynor.gl;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private List<Vao> vaos;

    public Model(List<Vao> vaos) {
        this.vaos = vaos;
    }

    public Model(Vao vao) {
        this.vaos = new ArrayList<>();
        this.vaos.add(vao);
    }

    public void render(int... binds) {
        for (Vao vao : vaos) {
            vao.bind(binds);
            GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
            vao.unbind(binds);
        }
    }

    public Vao getFirst() {
        return vaos.get(0);
    }
}
