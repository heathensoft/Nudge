package nudge.core.input;

import nudge.core.view.VIEW;
import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import static org.lwjgl.glfw.GLFW.*;

public final class MOUSE {


    // todo update mousePos when camera moves

    // https://www.glfw.org/docs/3.3/input_guide.html#input_mouse

    private static final int NUM_BUTTONS = 3;

    private static final int SCROLL_DOWN = -1;
    private static final int SCROLL_UP = 1;

    private static final int WHEEL_BUTTON = GLFW_MOUSE_BUTTON_MIDDLE;
    private static final int RIGHT_BUTTON = GLFW_MOUSE_BUTTON_RIGHT;
    private static final int LEFT_BUTTON  = GLFW_MOUSE_BUTTON_LEFT;

    private final boolean[] mouseButtonPressed = new boolean[NUM_BUTTONS];
    private final boolean[] draggingLastFrame  = new boolean[NUM_BUTTONS];
    private final boolean[] draggingThisFrame  = new boolean[NUM_BUTTONS];
    private final boolean[] currentlyDragging  = new boolean[NUM_BUTTONS];

    private final Rectanglef highlightBox;
    private final Vector2f position;
    private final Vector2f positionWorld;
    private final Vector2f positionViewport;
    private final Vector2f dragVector;
    private final Vector2f dragStart;

    private double xPos, yPos, lastX, lastY;

    // only one listener at a time. Can be changed at runtime
    private MouseListener listener = null;
    private static MOUSE instance;


    private MOUSE() {
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
        this.highlightBox = new Rectanglef();
        this.position = new Vector2f();
        this.positionWorld = new Vector2f();
        this.positionViewport = new Vector2f();
        this.dragVector = new Vector2f();
        this.dragStart = new Vector2f();
    }

    public static MOUSE get() {
        if (instance == null) {
            instance = new MOUSE();
        }
        return instance;
    }

    public static void mousePosCallback(long window, double xPos, double yPos) {

        MOUSE mouse = get();

        mouse.lastX = mouse.xPos;
        mouse.lastY = mouse.yPos;

        mouse.xPos = xPos;
        mouse.yPos = VIEW.screenHeight() - yPos;

        MouseListener listener = mouse.listener;


        boolean draggingThisFrame = mouse.mouseButtonPressed[WHEEL_BUTTON] ||
                                    mouse.mouseButtonPressed[RIGHT_BUTTON] ||
                                    mouse.mouseButtonPressed[LEFT_BUTTON ];

        boolean draggingLastFrame = mouse.draggingThisFrame[WHEEL_BUTTON] ||
                                    mouse.draggingThisFrame[RIGHT_BUTTON] ||
                                    mouse.draggingThisFrame[LEFT_BUTTON ];

        mouse.draggingThisFrame[WHEEL_BUTTON] = mouse.mouseButtonPressed[WHEEL_BUTTON];
        mouse.draggingThisFrame[RIGHT_BUTTON] = mouse.mouseButtonPressed[RIGHT_BUTTON];
        mouse.draggingThisFrame[LEFT_BUTTON ] = mouse.mouseButtonPressed[LEFT_BUTTON ];


        if (listener != null) {

            mouse.position.set(mouse.xPos,mouse.yPos);
            mouse.positionWorld.set(mouse.position);
            Window.get().scene().camera().unProjectMouse(
                    mouse.positionWorld,
                    mouse.positionViewport);
            listener.hover_View(mouse.positionViewport);
            listener.hover_World(mouse.positionWorld);

            if (!listener.ignoreDragAndHighlight()) {

                if (draggingThisFrame) {

                    if (!draggingLastFrame) {
                        mouse.dragStart.set(mouse.positionWorld);
                    }
                    else {
                        mouse.dragVector.set(mouse.positionWorld).sub(mouse.dragStart);

                        if (mouse.draggingThisFrame[WHEEL_BUTTON] && mouse.draggingLastFrame[WHEEL_BUTTON]) {
                            if ((!mouse.currentlyDragging[LEFT_BUTTON] && !mouse.currentlyDragging[RIGHT_BUTTON])) {
                                mouse.currentlyDragging[WHEEL_BUTTON] = true;
                                listener.m_drag(mouse.dragVector);
                                float x0 = Math.min(mouse.dragStart.x, mouse.positionWorld.x);
                                float y0 = Math.min(mouse.dragStart.y, mouse.positionWorld.y);
                                float x  = Math.max(mouse.dragStart.x, mouse.positionWorld.x);
                                float y  = Math.max(mouse.dragStart.y, mouse.positionWorld.y);
                                mouse.highlightBox.setMin(x0,y0);
                                mouse.highlightBox.setMax(x,y);
                                listener.m_highlightBox(mouse.highlightBox);
                            }
                        }
                        if (mouse.draggingThisFrame[RIGHT_BUTTON] && mouse.draggingLastFrame[RIGHT_BUTTON]) {
                            if ((!mouse.currentlyDragging[LEFT_BUTTON] && !mouse.currentlyDragging[WHEEL_BUTTON])) {
                                mouse.currentlyDragging[RIGHT_BUTTON] = true;
                                listener.r_drag(mouse.dragVector);
                                float x0 = Math.min(mouse.dragStart.x, mouse.positionWorld.x);
                                float y0 = Math.min(mouse.dragStart.y, mouse.positionWorld.y);
                                float x  = Math.max(mouse.dragStart.x, mouse.positionWorld.x);
                                float y  = Math.max(mouse.dragStart.y, mouse.positionWorld.y);
                                mouse.highlightBox.setMin(x0,y0);
                                mouse.highlightBox.setMax(x,y);
                                listener.r_highlightBox(mouse.highlightBox);
                            }
                        }
                        if (mouse.draggingThisFrame[LEFT_BUTTON] && mouse.draggingLastFrame[LEFT_BUTTON]) {
                            if ((!mouse.currentlyDragging[WHEEL_BUTTON] && !mouse.currentlyDragging[RIGHT_BUTTON])) {
                                mouse.currentlyDragging[LEFT_BUTTON] = true;
                                listener.l_drag(mouse.dragVector);
                                float x0 = Math.min(mouse.dragStart.x, mouse.positionWorld.x);
                                float y0 = Math.min(mouse.dragStart.y, mouse.positionWorld.y);
                                float x  = Math.max(mouse.dragStart.x, mouse.positionWorld.x);
                                float y  = Math.max(mouse.dragStart.y, mouse.positionWorld.y);
                                mouse.highlightBox.setMin(x0,y0);
                                mouse.highlightBox.setMax(x,y);
                                listener.l_highlightBox(mouse.highlightBox);
                            }
                        }
                    }
                }
            }
        }

        System.arraycopy(mouse.draggingThisFrame,0,mouse.draggingLastFrame,0,NUM_BUTTONS);

    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {

        MOUSE mouse = get();
        MouseListener listener = mouse.listener;

        if (action == GLFW_PRESS) {
            if (button < mouse.mouseButtonPressed.length) {

                mouse.mouseButtonPressed[button] = true;

                if (listener != null) {
                    if (button == LEFT_BUTTON) {
                        listener.leftClick_View(mouse.positionViewport);
                        listener.leftClick_World(mouse.positionWorld);
                    }
                    else if (button == RIGHT_BUTTON) {
                        listener.rightClick_View(mouse.positionViewport);
                        listener.rightClick_World(mouse.positionWorld);
                    }
                    else {
                        listener.wheelClick_View(mouse.positionViewport);
                        listener.wheelClick_World(mouse.positionWorld);
                    }
                }
            }
        }
        else if (action == GLFW_RELEASE) {
            if (button < mouse.mouseButtonPressed.length) {

                mouse.mouseButtonPressed[button] = false;

                if (listener != null) {

                    if (mouse.currentlyDragging[button]) {
                        mouse.dragStart.set(mouse.positionWorld);
                    }

                    if (!listener.ignoreDragAndHighlight()) {
                        if (button == LEFT_BUTTON) {
                            if(mouse.currentlyDragging[button]) {
                                listener.l_dragReleased(mouse.dragVector);
                                listener.l_highlightBoxReleased(mouse.highlightBox);
                            }
                        }
                        else if (button == RIGHT_BUTTON) {
                            if(mouse.currentlyDragging[button]) {
                                listener.r_dragReleased(mouse.dragVector);
                                listener.r_highlightBoxReleased(mouse.highlightBox);
                            }
                        }
                        else {
                            if(mouse.currentlyDragging[button]) {
                                listener.m_dragReleased(mouse.dragVector);
                                listener.m_highlightBoxReleased(mouse.highlightBox);
                            }
                        }
                    }
                    mouse.currentlyDragging[button] = false;
                }
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        MOUSE mouse = get();
        if (mouse.listener != null) {
            if (yOffset == SCROLL_UP) {
                mouse.listener.scrollUp();
            }
            else if (yOffset == SCROLL_DOWN) {
                mouse.listener.scrollDown();
            }
        }
    }

    public static float x() {
        return (float) get().xPos;
    }

    public static float y() {
        return (float) get().yPos;
    }

    public static float worldX() { return get().positionWorld.x; }

    public static float worldY() { return get().positionWorld.y; }

    public static float viewportX() { return get().positionViewport.x; }

    public static float viewportY() { return get().positionViewport.y; }

    public static float dX() {
        return (float) (get().lastX - get().xPos);
    }

    public static float dY() {
        return (float) (get().lastY - get().yPos);
    }

    public static boolean isDragging(int button) {
        if (button < NUM_BUTTONS) {
            return get().draggingThisFrame[button];
        }
        return false;
    }

    public static boolean isDragging() {
        MOUSE mouse = get();
        return  mouse.draggingThisFrame[RIGHT_BUTTON] ||
                mouse.draggingThisFrame[WHEEL_BUTTON] ||
                mouse.draggingThisFrame[LEFT_BUTTON ];
    }

    public static void setListener(MouseListener listener) {
        get().listener = listener;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        }
        else return false;
    }


}
