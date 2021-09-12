package nudge.core.coreTest;

import nudge.core.view.Camera;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public interface Application {
    
    LaunchConfig config();
    Camera camera();
    String title();
    String version();
    double runtime();
    void start();
    void queryInput();
    void update(float dt);
    void render();
    void exit();
    
}
