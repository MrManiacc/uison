package me.jraynor.core.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import me.jraynor.core.parts.RenderPart;
import me.jraynor.misc.GLUtils;
import me.jraynor.misc.Shader;
import me.jraynor.misc.ShaderBind;
import me.jraynor.pong.entities.Player;

public class BaseRenderer extends EntitySystem {
    private Shader shader;
    private ImmutableArray<Entity> renderEntities;
    private Player player;

    public BaseRenderer(int priority, Player player) {
        super(priority);
        this.shader = makeShader();
        this.player = player;
    }

    @Override
    public void addedToEngine(Engine engine) {
        renderEntities = engine.getEntitiesFor(Family.all(RenderPart.class).get());
    }

    protected Shader makeShader() {
        Shader shader = new Shader("static") {
            @Override
            protected void doBinds() {
                binds(new ShaderBind("vertex", 0), new ShaderBind("normal", 1), new ShaderBind("uv", 2));
            }

            @Override
            protected void doUniformBinds() {
                bindUniforms("proMatrix", "viewMatrix", "transMatrix", "diffuse");
                loadSampler("diffuse", 0);
            }
        };
        return shader;
    }

    protected void render(Entity entity) {
        RenderPart renderPart = entity.getComponent(RenderPart.class);
        renderPart.getModel().render(0, 1, 2);
    }

    @Override
    public void update(float deltaTime) {
        prepare();
        for (int i = 0; i < renderEntities.size(); i++) render(renderEntities.get(i));
        finish();
    }

    protected void prepare() {
        GLUtils.cullBackFaces(true);
        GLUtils.enableDepthTesting(true);
        shader.start();
        shader.loadMat4("proMatrix", player.getCamera().getProjectionMatrix());
        shader.loadMat4("viewMatrix", player.getCamera().getViewMatrix());
    }

    protected void finish() {
        GLUtils.cullBackFaces(false);
        GLUtils.enableDepthTesting(false);
        shader.stop();
    }
}
