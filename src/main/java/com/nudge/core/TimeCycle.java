package com.nudge.core;

import static java.lang.System.nanoTime;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class TimeCycle {

    private double initTime;
    private double lastFrame;
    private float timeAccumulator;
    private float frameTimeLimit;
    private int fpsCount;
    private int upsCount;
    private int fps;
    private int ups;

    public TimeCycle() { this(0.25f); }

    public TimeCycle(float frameTimeLimit) { this.frameTimeLimit = frameTimeLimit; }

    public void init() {
        initTime = nanoTime();
        lastFrame = getTime();
    }

    public float frameTime() {

        double timeSeconds = getTime();
        float frameTime = (float) (timeSeconds - lastFrame);
        frameTime = Math.min(frameTime, frameTimeLimit);
        lastFrame = timeSeconds;
        timeAccumulator += frameTime;
        return frameTime;
    }

    public void update() {

        if (timeAccumulator > 1) {
            fps = fpsCount;
            ups = upsCount;
            fpsCount = upsCount = 0;
            timeAccumulator -= 1;
        }
    }

    public double getTime() { return glfwGetTime(); }

    public double runTime() { return nanoTime() - initTime; }

    public void incFpsCount() { fpsCount++; }

    public void incUpsCount() { upsCount++; }

    public int fps() { return fps > 0 ? fps : fpsCount; }

    public int ups() { return ups > 0 ? ups : upsCount; }

    public double lastFrame() { return lastFrame; }

    public float frameTimeLimit() { return frameTimeLimit; }

    public void setFrameTimeLimit(float limit) { frameTimeLimit = limit; }

}
