package me.jraynor.bootstrap;

public interface IEngine {
    //Called before window is created
    void preInit();

    //Called after window is created but before loop
    void postInit();

    //Called as many times as possible, delta being the time passed since last call
    void render(double delta);

    //Called a fixed number of times, tick being the time passed since last call (should be static amount)
    void update(double tick);

    void renderUI(double delta);
}
