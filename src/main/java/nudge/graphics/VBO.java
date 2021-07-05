package nudge.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

/**
 * @author Frederik Dahl
 * 29/06/2021
 */

public class VBO {

    private final int id;

    public VBO() { id = glGenBuffers(); }

    public void bind(int target) { glBindBuffer(target, id); }

    public void uploadData(int target, FloatBuffer data, int usage) {
        glBufferData(target, data, usage);
    }

    public void uploadData(int target, float[] data, int usage) {
        glBufferData(target, data, usage);
    }

    public void uploadData(int target, long size, int usage) {
        glBufferData(target, size, usage);
    }

    public void uploadSubData(int target, long offset, float[] data) {
        glBufferSubData(target, offset, data);
    }

    public void uploadSubData(int target, long offset, FloatBuffer data) {
        glBufferSubData(target, offset, data);
    }

    public void uploadData(int target, IntBuffer data, int usage) {
        glBufferData(target, data, usage);
    }

    public void delete() { glDeleteBuffers(id); }

    public int getID() { return id; }

}
