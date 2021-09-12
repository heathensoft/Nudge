package nudge.core;


/**
 * @author Frederik Dahl
 * 13/07/2021
 */


public abstract class Game implements Application {


    private Config config;
    private TimeCycle timer;

    private float frameTime;
    private float accumulator;



    public abstract void update(float dt);

    public abstract void render(float at);

    public abstract void start();


    @Override
    public void configure() {

    }

    @Override
    public void init() {
        start();
        accumulator = 0f;
        // start Config logging;
        timer.init();
    }

    @Override
    public void execute() {

        float delta = 1f / config.targetUPS();
        accumulator += timer.frameTime();

        while (accumulator >= delta) {
            update(delta);
            timer.incUpsCount();
            accumulator -= delta;
        }
        render(accumulator / delta);
        timer.incFpsCount();
        timer.update();

        if (!config.vsyncEnabled() & config.fpsCapEnabled())
            sync();
    }

    private void sync() {

        double lastFrame = timer.lastFrame();
        double now = timer.getTime();
        float targetTime = 0.96f / config.targetFPS();

        while (now - lastFrame < targetTime) {
            if (config.sleepOnSync()) {
                Thread.yield();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            now = timer.getTime();
        }
    }


    @Override
    public Config settings() {
        return config;
    }
    

    @Override
    public float fps() {
        return timer.fps();
    }

    @Override
    public float ups() {
        return timer.ups();
    }

    @Override
    public double runtime() {
        return timer.runTime();
    }
}
