package com.nudge.core.view;

import com.nudge.core.CORE;
import com.nudge.util.U;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.primitives.Rectanglef;

import java.util.ArrayList;
import java.util.List;

public class Camera {

    private final Vector2f position;
    private final Vector2f lastPosition;
    private final Rectanglef worldView;

    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f combined;
    private final Matrix4f uiMatrix;

    private final Matrix4f invProjection;
    private final Matrix4f inverseUI;
    private final Matrix4f inverseView;

    private final Vector3f cameraFront;
    private final Vector3f cameraUp;
    private final Vector3f vec3Temp1;
    private final Vector3f vec3Temp2;
    private final Vector2f vec2Temp;

    private float zoom = 1f;
    private float lastZoom = 1f;
    private final float zoomFloor = 1/8f;
    private final float zoomCeil = 16f;

    private final float virtualWidth_WO;
    private final float virtualHeight_WO;
    private final float virtualWidthHalf_WO;
    private final float virtualHeightHalf_WO;
    private final float widthInUnits_WO;
    private final int unitSize_WO;

    private final float virtualWidth_UI;
    private final float virtualHeight_UI;
    private final float widthInUnits_UI;
    private final int unitSize_UI;

    private final List<Culling> listeners;

    public Camera() { this(new Vector2f()); }

    public Camera(Vector2f position) {
        this(
                position,
                CORE.get().display.viewportWidth(),
                1,
                CORE.get().display.viewportWidth(),
                1);
    }

    public Camera(Vector2f position, int unitSize_WO, float widthInUnits_WO, int unitSize_UI, float widthInUnits_UI) {

        this.position = position;
        this.lastPosition = new Vector2f(position);

        this.unitSize_WO = unitSize_WO;
        this.unitSize_UI = unitSize_UI;
        this.widthInUnits_WO = widthInUnits_WO;
        this.widthInUnits_UI = widthInUnits_UI;

        virtualWidth_WO = unitSize_WO * widthInUnits_WO;
        virtualWidth_UI = unitSize_UI * widthInUnits_UI;
        virtualHeight_WO = virtualWidth_WO / CORE.get().display.aspectRatio();
        virtualHeight_UI = virtualWidth_UI / CORE.get().display.aspectRatio();
        virtualWidthHalf_WO = virtualWidth_WO / 2;
        virtualHeightHalf_WO = virtualHeight_WO / 2;

        worldView = new Rectanglef();
        projectionMatrix = new Matrix4f();
        invProjection = new Matrix4f();
        uiMatrix = new Matrix4f();
        inverseUI = new Matrix4f();
        inverseView = new Matrix4f();
        viewMatrix = new Matrix4f();
        combined = new Matrix4f();
        cameraFront = new Vector3f();
        vec3Temp1 = new Vector3f();
        vec3Temp2 = new Vector3f();
        vec2Temp = new Vector2f();
        cameraUp = new Vector3f(0,1,0);

        this.listeners = new ArrayList<>();

        setWindowMatrices();
        adjustProjection();
        adjustView();
        setCombined();
    }

    private void setWindowMatrices() {
        uiMatrix.ortho(0, virtualWidth_WO,0, virtualHeight_WO,-1.0f,1.0f);
        uiMatrix.invert(inverseUI);
    }

    private void adjustProjection() {

        float left   = -virtualWidthHalf_WO * zoom;
        float right  =  virtualWidthHalf_WO * zoom;
        float top    =  virtualHeightHalf_WO * zoom;
        float bottom = -virtualHeightHalf_WO * zoom;

        projectionMatrix.identity();
        projectionMatrix.ortho(left,right,bottom,top,-1.0f,1.0f);
        projectionMatrix.invert(invProjection);
        worldView.setMax(position.x + right, position.y + top);
        worldView.setMin(position.x - right,position.y - top);
    }

    private void adjustView() {
        cameraFront.set(position.x,position.y,-1.0f);
        vec3Temp1.set(position.x,position.y,20.0f);
        viewMatrix.identity().lookAt(vec3Temp1, cameraFront, cameraUp);
        viewMatrix.invert(inverseView);
        if (!listeners.isEmpty()) {
            for (Culling listener : listeners) {
                listener.onCameraTranslation(worldView,position);
            }
        }
    }

    private void setCombined() {
        combined.set(projectionMatrix).mul(viewMatrix);
    }

    public void unProject(Vector2f vec) {
        Display display = CORE.get().display;
        float x = vec.x - display.viewportX0();
        float y = vec.y - display.viewportY0();
        vec3Temp1.x = (2 * x) * display.viewportWidthInv() - 1;
        vec3Temp1.y = (2 * y) * display.viewportHeightInv() - 1;
        vec3Temp1.z = 0; // try 1 if anything mysterious
        vec3Temp1.mulProject(invProjection).mulProject(inverseView);
        vec.set(vec3Temp1.x, vec3Temp1.y);
    }

    public void unProjectMouse(Vector2f world, Vector2f window) {
        Display display = CORE.get().display;
        float x = world.x - display.viewportX0();
        float y = world.y - display.viewportY0();
        vec3Temp1.x = (2 * x) * display.viewportWidthInv() - 1;
        vec3Temp1.y = (2 * y) * display.viewportHeightInv() - 1;
        vec3Temp1.z = 0; // try 1 if anything mysterious
        vec3Temp2.set(vec3Temp1);
        vec3Temp1.mulProject(invProjection).mulProject(inverseView);
        vec3Temp2.mulProject(inverseUI);
        world.set(vec3Temp1.x, vec3Temp1.y);
        window.set(vec3Temp2.x, vec3Temp2.y);
    }

    public void translate(Vector2f vec) {
        lastPosition.set(position);
        position.add(vec);
        worldView.translate(vec);
        adjustView();
        setCombined();
    }

    public void translate(float x, float y) {
        lastPosition.set(position);
        position.add(x,y);
        worldView.translate(x,y);
        adjustView();
        setCombined();
    }

    public void setPosition(Vector2f vec) {
        lastPosition.set(position);
        vec2Temp.set(vec).sub(position);
        position.set(vec);
        worldView.translate(vec2Temp);
        adjustView();
        setCombined();
    }

    public void setPosition(float x, float y) {
        lastPosition.set(position);
        vec2Temp.set(x,y).sub(position);
        position.set(x,y);
        worldView.translate(vec2Temp);
        adjustView();
        setCombined();
    }

    public float x() {
        return position.x;
    }

    public float y() {
        return position.y;
    }

    public Vector2f position() { return position; }

    public Vector2f lastPosition() { return lastPosition; }

    public Matrix4f viewMatrix() {
        return viewMatrix;
    }

    public Matrix4f inverseView() {
        return inverseView;
    }

    public Matrix4f projectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f combined() {
        return combined;
    }

    public Matrix4f windowMatrix() { return uiMatrix; }

    public Matrix4f inverseProjection() {
        return invProjection;
    }

    public Rectanglef worldView() {
        return worldView;
    }

    public boolean moved(float delta) {
        return !position.equals(lastPosition,delta);
    }

    public void zoom(float amount) {
        if (amount != 0) {
            zoom += amount;
            zoom = U.clamp(zoom,zoomFloor,zoomCeil);
            if (zoom != lastZoom) {
                adjustProjection();
                setCombined();
                if (!listeners.isEmpty()) {
                    for (Culling listener : listeners) {
                        listener.onCameraZoom(worldView,zoom);
                    }
                }
                lastZoom = zoom;
            }
        }
    }

    public void setZoom(float value) {
        if (value >= zoomFloor && value <= zoomCeil && value != zoom) {
            lastZoom = zoom;
            zoom = value;
            adjustProjection();
            setCombined();
            if (!listeners.isEmpty()) {
                for (Culling listener : listeners) {
                    listener.onCameraZoom(worldView,zoom);
                }
            }
        }
    }

    public void addListener(Culling listener) {
        listeners.add(listener);
    }

    public void removeListener(Culling listener) {
        listeners.remove(listener);
    }

    public float getZoom() {
        return zoom;
    }

    public float zoomFloor() {
        return zoomFloor;
    }

    public float zoomCeil() {
        return zoomCeil;
    }

}
