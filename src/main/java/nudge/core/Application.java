package nudge.core;

import nudge.core.view.Camera;

/**
 * @author Frederik Dahl
 * 05/07/2021
 */


public interface Application {


    void configure();

    void create();

    void handleInput();

    void update(float dt);

    void render();

    void exit();

    Settings settings();

    Camera camera();

    String title();

    short versionMinor();

    short versionMajor();

    long runtime();




}
