package me.jraynor.bootstrap;

public abstract class IEngine {
    //Called before window is created
    public void preInit(){}

    //Called after window is created but before loop
    public void postInit(){}

    //Called as many times as possible, delta being the time passed since last call
    public void render(double delta){}

    //Called a fixed number of times, tick being the time passed since last call (should be static amount)
    public void update(double tick){}

    public void renderUI(double delta){}
}
