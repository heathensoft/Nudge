package nudge.core.input;


import static org.lwjgl.glfw.GLFW.*;

// https://github.com/LWJGL/lwjgl3-wiki/wiki/2.6.3-Input-handling-with-GLFW#joystick-input
// https://www.glfw.org/docs/3.3/input_guide.html#joystick

// todo: Implement support for basic joystick / controller - input handling.

public class GamePad { // Temp

    public static int LEFT_STICK_HORIZONTAL = 0;
    public static int LEFT_STICK_VERTICAL = 1;
    public static int LEFT_TRIGGER = 2;
    public static int RIGHT_TRIGGER = 5;
    public static int RIGHT_STICK_HORIZONTAL = 3;
    public static int RIGHT_STICK_VERITCAL = 4;

    public static int A = 0;
    public static int B = 1;
    public static int X = 2;
    public static int Y = 3;
    public static int LEFT_SHOULDER = 4;
    public static int RIGHT_SHOULDER = 5;
    public static int BACK = 6;
    public static int START = 7;
    public static int XBOX = 8;
    public static int LEFT_STICK = 9;
    public static int RIGHT_STICK = 10;
    public static int D_UP = 11;
    public static int D_RIGHT = 12;
    public static int D_DOWN = 13;
    public static int D_LEFT = 14;


    public static int controllersAvailable () {
        int total = 0;
        for (int i = 0; i < 10; i ++) {
            if (glfwJoystickPresent(i)) {
                total ++;
            }
        }
        return total;
    }

    public static boolean buttonPressed (int controllerId, int button) {
        try {
            return glfwGetJoystickButtons(controllerId).get(button) == 1;
        } catch (NullPointerException e) {
            System.out.println("No Controller Attached on " + controllerId + ".");
            return false;
        }
    }

    public static float axis (int controllerId, int axis) {
        try {
            float x = glfwGetJoystickAxes(controllerId).get(axis);
            if (Math.abs(x) < 0.1) {
                x = 0;
            }
            return x;
        } catch (NullPointerException e) {
            System.out.println("No Controller Attached on " + controllerId + ".");
            return 0;
        }
    }
}
