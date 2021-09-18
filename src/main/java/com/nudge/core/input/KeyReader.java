package com.nudge.core.input;

/**
 * @author Frederik Dahl
 * 29/06/2021
 */

public interface KeyReader {

    void registerControl(int glfwKey);

    void registerChar(byte charCode);

    void signalActivate();

    void signalDeactivate();

    boolean isSleeping(); // todo: invert
}
