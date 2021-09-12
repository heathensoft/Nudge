package nudge.core;

import nudge.core.view.Camera;

/**
 * @author Frederik Dahl
 * 05/07/2021
 */


public interface Application {


    void configure();

    void init();

    void execute();

    void exit();

    Config settings();

    Camera camera();

    float fps();

    float ups();

    double runtime();

    default String title() {
        return "NudgeApp";
    }

    default short versionMinor() { return 0; }

    default short versionMajor() { return 0; }





}
