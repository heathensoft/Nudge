package nudge.core;

import static org.lwjgl.glfw.GLFW.glfwGetTime;


// https://github.com/SilverTiger/lwjgl3-tutorial/blob/77022e860c2debcc022932d7dbc3787a2d71b805/src/silvertiger/tutorial/lwjgl/core/Timer.java

public class Timer {

    private double lastLoopTime;
    private float timeCount;
    private int fpsCount;
    private int upsCount;
    private int fps;
    private int ups;

    public void init() { lastLoopTime = getTime(); }

    public double getTime() { return glfwGetTime(); } // time since glfwInit() (seconds)}

    /**
     * Returns the time that have passed since the last loop.
     * @return Delta time in seconds
     */
    public float getDelta() {
        double time = getTime();
        float delta = (float) (time - lastLoopTime);
        lastLoopTime = time;
        timeCount += delta;
        return delta;
    }

    public void updateFPS() { fpsCount++; }

    public void updateUPS() { upsCount++; }
    /**
     * Updates FPS and UPS if a whole second has passed.
     */
    public void update() {
        if (timeCount > 1f) {

            fps = fpsCount;
            fpsCount = 0;

            ups = upsCount;
            upsCount = 0;

            timeCount -= 1f;
        }
    }

    public int getFPS() { return fps > 0 ? fps : fpsCount; }

    public int getUPS() { return ups > 0 ? ups : upsCount; }

    public double getLastLoopTime() { return lastLoopTime; }

}
