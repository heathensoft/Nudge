package nudge.graphics;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

/**
 * @author Frederik Dahl
 * 29/06/2021
 */

public class ShaderProgram {

    private final int id;

    public ShaderProgram() { id = glCreateProgram(); }


    public void vertexAttributePointer(int location, int size, int stride, int offset) {

        glVertexAttribPointer(location, size, GL_FLOAT, false, stride, offset);
    }

    public int getUniformLocation(CharSequence name) {

        return glGetUniformLocation(id, name);
    }

    public void attachShader(Shader shader) {

        // A shaders' constructor is exclusively private.
        // the shader would be compiled atp

        glAttachShader(id, shader.getID());
    }

    private void checkStatus() {

        int status = glGetProgrami(id, GL_LINK_STATUS);

        if (status != GL_TRUE) {

            throw new RuntimeException(glGetProgramInfoLog(id));
        }
    }

    public void link() {

        glLinkProgram(id);

        checkStatus();
    }

    public void setUniform(String name, int value) {

        int location = getUniformLocation(name);

        setUniform(location, value);
    }

    public void setUniform(int location, int value) {

        glUniform1i(location, value);
    }

    public void setUniform(int location, float value) {

        glUniform1f(location, value);
    }

    public void setUniform(int location, Vector2f value) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2);
            buffer.put(value.x).put(value.y);
            buffer.flip();
            glUniform2fv(location, buffer);
        }
    }

    public void setUniform(int location, Vector3f value) {

        glUniform3f(location,value.x,value.y,value.z);
    }

    public void setUniform(int location, Vector4f value) {

        glUniform4f(location,value.x,value.y,value.z,value.w);
    }

    public void setUniform(int location, Matrix3f value) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(9);
            value.get(buffer);
            glUniformMatrix3fv(location, false, buffer);
        }
    }

    public void setUniform(int location, Matrix4f value) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void setUniform(int location, int[] array) {

        glUniform1iv(location,array);
    }

    public void bindFragmentDataLocation(int number, CharSequence name) {

        glBindFragDataLocation(id, number, name);
    }

    public void use() { glUseProgram(id); }

    public void unBind() { glUseProgram(0); }

    public int getId() { return id; }

    public void delete() { glDeleteProgram(id); }
}
