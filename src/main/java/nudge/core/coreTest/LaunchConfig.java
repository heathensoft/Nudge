package nudge.core.coreTest;

/**
 * @author Frederik Dahl
 * 17/08/2021
 */


public interface LaunchConfig {
    
    default int desiredResolutionWidth() { return 1280; }
    default int desiredResolutionHeight() { return 720; }
    default boolean verticalSynchronization() { return true; }
    default boolean resizableWindow() { return false; }
    default boolean windowedMode() { return true; }
    default float volumeMaster() { return 1f; }
    default float volumeEffects() { return 1f; }
    default float volumeAmbient() { return 1f; }
    default float volumeDialogue() { return 1f; }
    default float volumeMusic() { return 1f; }
    default boolean audioEnabled() { return false; }
    default boolean controllerSupport() { return false; }
    
}
