package nudge.core;

/**
 * @author Frederik Dahl
 * 05/07/2021
 */


public abstract class Application {

    private Settings settings;
    private Timer timer;

    public abstract void init();
    public abstract Settings settings();

    // String title();
    // String windowTitle();
    // int versionMinor();
    // int versionMajor();
    // int runtime();




}
