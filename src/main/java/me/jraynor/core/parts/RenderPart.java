package me.jraynor.core.parts;

import com.badlogic.ashley.core.Component;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.misc.Model;

public class RenderPart implements Component {
    @Getter
    @Setter
    private Model model;

    public RenderPart(Model model) {
        this.model = model;
    }
}
