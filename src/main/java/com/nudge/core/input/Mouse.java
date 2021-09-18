package com.nudge.core.input;

import com.nudge.core.CORE;
import com.nudge.core.glfwEvents.CursorPositionCallback;
import com.nudge.core.glfwEvents.MouseButtonCallback;
import com.nudge.core.glfwEvents.ScrollCallback;
import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse {
    
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

    // Only one listener at a time. Can be changed at runtime. Really no reason to have more.
    private MouseListener listener = null;
    private final CursorPositionCallback cursorPositionCallback;
    private final MouseButtonCallback mouseButtonCallback;
    private final ScrollCallback scrollCallback;


    public Mouse() {
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
        this.cursorPositionCallback = new CursorPositionCallback();
        this.mouseButtonCallback = new MouseButtonCallback();
        this.scrollCallback = new ScrollCallback();
    }
    
    public void step() {
        
        // Todo: Find out here if the camera has moved.
        //  if it has, we need to unProject the mouse, even if the mouse has not moved..
        //  to update our world coordinates, and notify our listener.
        //  this is not of immediate concern.
        
        if (scrollCallback.eventQueued()) {
    
            if (this.listener != null) {
                
                double yOffset = scrollCallback.getYoffset();
                
                if (yOffset == SCROLL_UP) {
                    this.listener.scrollUp();
                }
                else if (yOffset == SCROLL_DOWN) {
                    this.listener.scrollDown();
                }
            }
            scrollCallback.reset();
        }
        
        if (mouseButtonCallback.eventQueued()) {
    
            MouseListener listener = this.listener;
            
            int action = mouseButtonCallback.getAction();
            int button = mouseButtonCallback.getButton();
    
            if (action == GLFW_PRESS) {
                if (button < this.mouseButtonPressed.length) {
            
                    this.mouseButtonPressed[button] = true;
            
                    if (listener != null) {
                        if (button == LEFT_BUTTON) {
                            listener.leftClick_View(this.positionViewport);
                            listener.leftClick_World(this.positionWorld);
                        }
                        else if (button == RIGHT_BUTTON) {
                            listener.rightClick_View(this.positionViewport);
                            listener.rightClick_World(this.positionWorld);
                        }
                        else {
                            listener.wheelClick_View(this.positionViewport);
                            listener.wheelClick_World(this.positionWorld);
                        }
                    }
                }
            }
            else if (action == GLFW_RELEASE) {
                if (button < this.mouseButtonPressed.length) {
            
                    this.mouseButtonPressed[button] = false;
            
                    if (listener != null) {
                
                        if (this.currentlyDragging[button]) {
                            this.dragStart.set(this.positionWorld);
                        }
                
                        if (!listener.ignoreDragAndHighlight()) {
                            if (button == LEFT_BUTTON) {
                                if(this.currentlyDragging[button]) {
                                    listener.l_dragReleased(this.dragVector);
                                    listener.l_highlightBoxReleased(this.highlightBox);
                                }
                            }
                            else if (button == RIGHT_BUTTON) {
                                if(this.currentlyDragging[button]) {
                                    listener.r_dragReleased(this.dragVector);
                                    listener.r_highlightBoxReleased(this.highlightBox);
                                }
                            }
                            else {
                                if(this.currentlyDragging[button]) {
                                    listener.m_dragReleased(this.dragVector);
                                    listener.m_highlightBoxReleased(this.highlightBox);
                                }
                            }
                        }
                        this.currentlyDragging[button] = false;
                    }
                }
            }
            mouseButtonCallback.reset();
        }
        
        if (cursorPositionCallback.eventQueued()) {
    
            this.lastX = this.xPos;
            this.lastY = this.yPos;
            
            this.xPos = cursorPositionCallback.getX();
            this.yPos = CORE.get().display.screenHeight() - cursorPositionCallback.getY();
    
            MouseListener listener = this.listener;
    
    
            boolean draggingThisFrame = this.mouseButtonPressed[WHEEL_BUTTON] ||
                                                this.mouseButtonPressed[RIGHT_BUTTON] ||
                                                this.mouseButtonPressed[LEFT_BUTTON ];
    
            boolean draggingLastFrame = this.draggingThisFrame[WHEEL_BUTTON] ||
                                                this.draggingThisFrame[RIGHT_BUTTON] ||
                                                this.draggingThisFrame[LEFT_BUTTON ];
    
            this.draggingThisFrame[WHEEL_BUTTON] = this.mouseButtonPressed[WHEEL_BUTTON];
            this.draggingThisFrame[RIGHT_BUTTON] = this.mouseButtonPressed[RIGHT_BUTTON];
            this.draggingThisFrame[LEFT_BUTTON ] = this.mouseButtonPressed[LEFT_BUTTON ];
    
    
            if (listener != null) {
        
                this.position.set(this.xPos,this.yPos);
                this.positionWorld.set(this.position);
                CORE.get().app.camera().unProjectMouse(
                        this.positionWorld,
                        this.positionViewport);
                listener.hover_View(this.positionViewport);
                listener.hover_World(this.positionWorld);
        
                if (!listener.ignoreDragAndHighlight()) {
            
                    if (draggingThisFrame) {
                
                        if (!draggingLastFrame) {
                            this.dragStart.set(this.positionWorld);
                        }
                        else {
                            this.dragVector.set(this.positionWorld).sub(this.dragStart);
                    
                            if (this.draggingThisFrame[WHEEL_BUTTON] && this.draggingLastFrame[WHEEL_BUTTON]) {
                                if ((!this.currentlyDragging[LEFT_BUTTON] && !this.currentlyDragging[RIGHT_BUTTON])) {
                                    this.currentlyDragging[WHEEL_BUTTON] = true;
                                    listener.m_drag(this.dragVector);
                                    float x0 = Math.min(this.dragStart.x, this.positionWorld.x);
                                    float y0 = Math.min(this.dragStart.y, this.positionWorld.y);
                                    float x  = Math.max(this.dragStart.x, this.positionWorld.x);
                                    float y  = Math.max(this.dragStart.y, this.positionWorld.y);
                                    this.highlightBox.setMin(x0,y0);
                                    this.highlightBox.setMax(x,y);
                                    listener.m_highlightBox(this.highlightBox);
                                }
                            }
                            if (this.draggingThisFrame[RIGHT_BUTTON] && this.draggingLastFrame[RIGHT_BUTTON]) {
                                if ((!this.currentlyDragging[LEFT_BUTTON] && !this.currentlyDragging[WHEEL_BUTTON])) {
                                    this.currentlyDragging[RIGHT_BUTTON] = true;
                                    listener.r_drag(this.dragVector);
                                    float x0 = Math.min(this.dragStart.x, this.positionWorld.x);
                                    float y0 = Math.min(this.dragStart.y, this.positionWorld.y);
                                    float x  = Math.max(this.dragStart.x, this.positionWorld.x);
                                    float y  = Math.max(this.dragStart.y, this.positionWorld.y);
                                    this.highlightBox.setMin(x0,y0);
                                    this.highlightBox.setMax(x,y);
                                    listener.r_highlightBox(this.highlightBox);
                                }
                            }
                            if (this.draggingThisFrame[LEFT_BUTTON] && this.draggingLastFrame[LEFT_BUTTON]) {
                                if ((!this.currentlyDragging[WHEEL_BUTTON] && !this.currentlyDragging[RIGHT_BUTTON])) {
                                    this.currentlyDragging[LEFT_BUTTON] = true;
                                    listener.l_drag(this.dragVector);
                                    float x0 = Math.min(this.dragStart.x, this.positionWorld.x);
                                    float y0 = Math.min(this.dragStart.y, this.positionWorld.y);
                                    float x  = Math.max(this.dragStart.x, this.positionWorld.x);
                                    float y  = Math.max(this.dragStart.y, this.positionWorld.y);
                                    this.highlightBox.setMin(x0,y0);
                                    this.highlightBox.setMax(x,y);
                                    listener.l_highlightBox(this.highlightBox);
                                }
                            }
                        }
                    }
                }
            }
    
            System.arraycopy(this.draggingThisFrame,0,this.draggingLastFrame,0,NUM_BUTTONS);
            
            cursorPositionCallback.reset();
        }
        
    }
    
    public void mousePosCallback(long window, double xPos, double yPos) {
        
        this.lastX = this.xPos;
        this.lastY = this.yPos;

        this.xPos = xPos;
        this.yPos = CORE.get().display.screenHeight() - yPos;

        MouseListener listener = this.listener;


        boolean draggingThisFrame = this.mouseButtonPressed[WHEEL_BUTTON] ||
                                    this.mouseButtonPressed[RIGHT_BUTTON] ||
                                    this.mouseButtonPressed[LEFT_BUTTON ];

        boolean draggingLastFrame = this.draggingThisFrame[WHEEL_BUTTON] ||
                                    this.draggingThisFrame[RIGHT_BUTTON] ||
                                    this.draggingThisFrame[LEFT_BUTTON ];

        this.draggingThisFrame[WHEEL_BUTTON] = this.mouseButtonPressed[WHEEL_BUTTON];
        this.draggingThisFrame[RIGHT_BUTTON] = this.mouseButtonPressed[RIGHT_BUTTON];
        this.draggingThisFrame[LEFT_BUTTON ] = this.mouseButtonPressed[LEFT_BUTTON ];


        if (listener != null) {

            this.position.set(this.xPos,this.yPos);
            this.positionWorld.set(this.position);
            CORE.get().app.camera().unProjectMouse(
                    this.positionWorld,
                    this.positionViewport);
            listener.hover_View(this.positionViewport);
            listener.hover_World(this.positionWorld);

            if (!listener.ignoreDragAndHighlight()) {

                if (draggingThisFrame) {

                    if (!draggingLastFrame) {
                        this.dragStart.set(this.positionWorld);
                    }
                    else {
                        this.dragVector.set(this.positionWorld).sub(this.dragStart);

                        if (this.draggingThisFrame[WHEEL_BUTTON] && this.draggingLastFrame[WHEEL_BUTTON]) {
                            if ((!this.currentlyDragging[LEFT_BUTTON] && !this.currentlyDragging[RIGHT_BUTTON])) {
                                this.currentlyDragging[WHEEL_BUTTON] = true;
                                listener.m_drag(this.dragVector);
                                float x0 = Math.min(this.dragStart.x, this.positionWorld.x);
                                float y0 = Math.min(this.dragStart.y, this.positionWorld.y);
                                float x  = Math.max(this.dragStart.x, this.positionWorld.x);
                                float y  = Math.max(this.dragStart.y, this.positionWorld.y);
                                this.highlightBox.setMin(x0,y0);
                                this.highlightBox.setMax(x,y);
                                listener.m_highlightBox(this.highlightBox);
                            }
                        }
                        if (this.draggingThisFrame[RIGHT_BUTTON] && this.draggingLastFrame[RIGHT_BUTTON]) {
                            if ((!this.currentlyDragging[LEFT_BUTTON] && !this.currentlyDragging[WHEEL_BUTTON])) {
                                this.currentlyDragging[RIGHT_BUTTON] = true;
                                listener.r_drag(this.dragVector);
                                float x0 = Math.min(this.dragStart.x, this.positionWorld.x);
                                float y0 = Math.min(this.dragStart.y, this.positionWorld.y);
                                float x  = Math.max(this.dragStart.x, this.positionWorld.x);
                                float y  = Math.max(this.dragStart.y, this.positionWorld.y);
                                this.highlightBox.setMin(x0,y0);
                                this.highlightBox.setMax(x,y);
                                listener.r_highlightBox(this.highlightBox);
                            }
                        }
                        if (this.draggingThisFrame[LEFT_BUTTON] && this.draggingLastFrame[LEFT_BUTTON]) {
                            if ((!this.currentlyDragging[WHEEL_BUTTON] && !this.currentlyDragging[RIGHT_BUTTON])) {
                                this.currentlyDragging[LEFT_BUTTON] = true;
                                listener.l_drag(this.dragVector);
                                float x0 = Math.min(this.dragStart.x, this.positionWorld.x);
                                float y0 = Math.min(this.dragStart.y, this.positionWorld.y);
                                float x  = Math.max(this.dragStart.x, this.positionWorld.x);
                                float y  = Math.max(this.dragStart.y, this.positionWorld.y);
                                this.highlightBox.setMin(x0,y0);
                                this.highlightBox.setMax(x,y);
                                listener.l_highlightBox(this.highlightBox);
                            }
                        }
                    }
                }
            }
        }

        System.arraycopy(this.draggingThisFrame,0,this.draggingLastFrame,0,NUM_BUTTONS);

    }

    public void mouseButtonCallback(long window, int button, int action, int mods) {
        
        MouseListener listener = this.listener;

        if (action == GLFW_PRESS) {
            if (button < this.mouseButtonPressed.length) {

                this.mouseButtonPressed[button] = true;

                if (listener != null) {
                    if (button == LEFT_BUTTON) {
                        listener.leftClick_View(this.positionViewport);
                        listener.leftClick_World(this.positionWorld);
                    }
                    else if (button == RIGHT_BUTTON) {
                        listener.rightClick_View(this.positionViewport);
                        listener.rightClick_World(this.positionWorld);
                    }
                    else {
                        listener.wheelClick_View(this.positionViewport);
                        listener.wheelClick_World(this.positionWorld);
                    }
                }
            }
        }
        else if (action == GLFW_RELEASE) {
            if (button < this.mouseButtonPressed.length) {

                this.mouseButtonPressed[button] = false;

                if (listener != null) {

                    if (this.currentlyDragging[button]) {
                        this.dragStart.set(this.positionWorld);
                    }

                    if (!listener.ignoreDragAndHighlight()) {
                        if (button == LEFT_BUTTON) {
                            if(this.currentlyDragging[button]) {
                                listener.l_dragReleased(this.dragVector);
                                listener.l_highlightBoxReleased(this.highlightBox);
                            }
                        }
                        else if (button == RIGHT_BUTTON) {
                            if(this.currentlyDragging[button]) {
                                listener.r_dragReleased(this.dragVector);
                                listener.r_highlightBoxReleased(this.highlightBox);
                            }
                        }
                        else {
                            if(this.currentlyDragging[button]) {
                                listener.m_dragReleased(this.dragVector);
                                listener.m_highlightBoxReleased(this.highlightBox);
                            }
                        }
                    }
                    this.currentlyDragging[button] = false;
                }
            }
        }
    }

    public void mouseScrollCallback(long window, double xOffset, double yOffset) {
        
        if (this.listener != null) {
            if (yOffset == SCROLL_UP) {
                this.listener.scrollUp();
            }
            else if (yOffset == SCROLL_DOWN) {
                this.listener.scrollDown();
            }
        }
    }

    public float x() {return (float) xPos;}

    public float y() {return (float) yPos;}

    public float worldX() { return positionWorld.x; }

    public float worldY() { return positionWorld.y; }

    public float viewportX() { return positionViewport.x; }

    public float viewportY() { return positionViewport.y; }

    public float dX() {return (float) (lastX - xPos);}

    public float dY() {return (float) (lastY - yPos);}

    public boolean isDragging(int button) {
        if (button < NUM_BUTTONS) {
            return draggingThisFrame[button];
        }
        return false;
    }

    public  boolean isDragging() {
        return  this.draggingThisFrame[RIGHT_BUTTON] ||
                this.draggingThisFrame[WHEEL_BUTTON] ||
                this.draggingThisFrame[LEFT_BUTTON ];
    }

    public void setListener(MouseListener listener) {
        this.listener = listener;
    }

    public  boolean mouseButtonDown(int button) {
        if (button < mouseButtonPressed.length) {
            return mouseButtonPressed[button];
        }
        else return false;
    }
    
    public CursorPositionCallback cursorPositionCallback() {
        return cursorPositionCallback;
    }
    
    public MouseButtonCallback mouseButtonCallback() {
        return mouseButtonCallback;
    }
    
    public ScrollCallback scrollCallback() {
        return scrollCallback;
    }
}
