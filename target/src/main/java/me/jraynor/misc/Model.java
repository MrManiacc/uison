package me.jraynor.misc;

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

    public void render() {
        for (Vao vao : vaos) {
            vao.bind(0, 1, 2);
            GL11.glDrawElements(GL11.GL_TRIANGLES, vao.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
            vao.unbind(0, 1, 2);
        }
    }

    public Vao getFirst(){
        return vaos.get(0);
    }
}
