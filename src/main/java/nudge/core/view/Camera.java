package nudge.core.view;

import nudge.util.U;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.primitives.Rectanglef;

import java.util.ArrayList;
import java.util.List;

public class Camera {

    //
    private final Rectanglef worldView;
    private final Matrix4f projectionMatrix;
    private final Matrix4f inverseProjection;
    private final Matrix4f windowMatrix;
    private final Matrix4f inverseWindow;
    private final Matrix4f viewMatrix;
    private final Matrix4f inverseView;
    private final Matrix4f combined;
    private final Vector3f cameraFront;
    private final Vector3f cameraUp;
    private final Vector2f position;
    private final Vector2f lastPosition;
    private final Vector3f vec3Temp1;
    private final Vector3f vec3Temp2;
    private final Vector2f vec2Temp;

    private final List<ViewListener> listeners;

    private float zoom = 1f;
    private float lastZoom = 1f;
    private final float zoomFloor = 1/8f;
    private final float zoomCeil = 16f;

    private final float viewportW;
    private final float viewportH;
    private final float viewportW_half;
    private final float viewportH_half;

    public Camera() {
        this(new Vector2f());
    }

    public Camera(Vector2f position) {
        this(position,VIEW.viewportWidth(),VIEW.viewportHeight());
    }

    public Camera(Vector2f position, float viewportW, float viewportH) {

        this.position = position;
        this.lastPosition = new Vector2f(position);

        this.worldView = new Rectanglef();
        this.projectionMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.windowMatrix = new Matrix4f();
        this.inverseWindow = new Matrix4f();
        this.inverseView = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.combined = new Matrix4f();
        this.cameraFront = new Vector3f();
        this.vec3Temp1 = new Vector3f();
        this.vec3Temp2 = new Vector3f();
        this.vec2Temp = new Vector2f();
        this.cameraUp = new Vector3f();
        cameraUp.set(0,1,0);

        this.listeners = new ArrayList<>();

        this.viewportW = viewportW;
        this.viewportH = viewportH;
        this.viewportW_half = viewportW / 2;
        this.viewportH_half = viewportH / 2;

        setWindowMatrices();
        adjustProjection();
        adjustView();
        setCombined();
    }

    private void setWindowMatrices() {
        windowMatrix.ortho(0, viewportW,0, viewportH,0.0f,100.0f);
        windowMatrix.invert(inverseWindow);
    }

    private void adjustProjection() {

        float left   = -viewportW_half * zoom;
        float right  =  viewportW_half * zoom;
        float top    =  viewportH_half * zoom;
        float bottom = -viewportH_half * zoom;

        projectionMatrix.identity();
        projectionMatrix.ortho(left,right,bottom,top,0.0f,100.0f);
        projectionMatrix.invert(inverseProjection);
        worldView.setMax(position.x + right, position.y + top);
        worldView.setMin(position.x - right,position.y - top);
    }

    private void adjustView() {
        cameraFront.set(position.x,position.y,-1.0f);
        vec3Temp1.set(position.x,position.y,20.0f);
        viewMatrix.identity().lookAt(vec3Temp1, cameraFront, cameraUp);
        viewMatrix.invert(inverseView);
        if (!listeners.isEmpty()) {
            for (ViewListener listener : listeners) {
                listener.onCameraTranslation(worldView,position);
            }
        }
    }

    private void setCombined() {
        combined.set(projectionMatrix).mul(viewMatrix);
    }

    public void unProject(Vector2f vec) {
        float x = vec.x - VIEW.viewportX();
        float y = vec.y - VIEW.viewportY();
        vec3Temp1.x = (2 * x) * VIEW.viewW_normalized() - 1;
        vec3Temp1.y = (2 * y) * VIEW.viewH_normalized() - 1;
        vec3Temp1.z = 0; // try 1 if anything mysterious
        vec3Temp1.mulProject(inverseProjection).mulProject(inverseView);
        vec.set(vec3Temp1.x, vec3Temp1.y);
    }

    public void unProjectMouse(Vector2f world, Vector2f window) {
        float x = world.x - VIEW.viewportX();
        float y = world.y - VIEW.viewportY();
        vec3Temp1.x = (2 * x) * VIEW.viewW_normalized() - 1;
        vec3Temp1.y = (2 * y) * VIEW.viewH_normalized() - 1;
        vec3Temp1.z = 0; // try 1 if anything mysterious
        vec3Temp2.set(vec3Temp1);
        vec3Temp1.mulProject(inverseProjection).mulProject(inverseView);
        vec3Temp2.mulProject(inverseWindow);
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

    public Matrix4f windowMatrix() { return windowMatrix; }

    public Matrix4f inverseProjection() {
        return inverseProjection;
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
                    for (ViewListener listener : listeners) {
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
                for (ViewListener listener : listeners) {
                    listener.onCameraZoom(worldView,zoom);
                }
            }
        }
    }

    public void addListener(ViewListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ViewListener listener) {
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
