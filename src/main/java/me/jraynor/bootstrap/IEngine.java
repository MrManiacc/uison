package me.jraynor.bootstrap;

public abstract class IEngine {
    private double tickTime;

    public IEngine(double tickTime) {
        this.tickTime = tickTime;
    }

    //Called before window is created
    public void preInit() {
    }

    //Called after window is created but before loop
    public void postInit() {
    }

    //Called as many times as possible, delta being the time passed since last call
    public void render(float delta) {
    }

    public void tick(float delta) {
    }

    //Called a fixed number of times, tick being the time passed since last call (should be static amount)
    public void update(float tick) {
    }

    public void renderUI(float delta) {
    }

    public double getTick() {
        return tickTime;
    }
}
