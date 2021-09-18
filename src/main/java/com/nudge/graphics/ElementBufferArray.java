package com.nudge.graphics;

import static org.lwjgl.opengl.GL15.*;

/**
 * @author Frederik Dahl
 * XX/06/2021
 */

public class ElementBufferArray {

    private final int id;
    private int primitiveType;

    public ElementBufferArray() { id = glGenBuffers(); }

    public void bind(int target) { glBindBuffer(target, id); }

    public void uploadQuads(int count) {

        final int len = count * 6;

        if (len <= Short.MAX_VALUE) {
            final short[] indices = new short[len];
            short j = 0;
            for (int i = 0; i < len; i += 6, j += 4) {
                indices[i] = j;
                indices[i + 1] = (short)(j + 1);
                indices[i + 2] = (short)(j + 2);
                indices[i + 3] = (short)(j + 2);
                indices[i + 4] = (short)(j + 3);
                indices[i + 5] = j;
            }
            uploadData(indices);
            primitiveType = GL_UNSIGNED_SHORT;
        }
        else {
            final int[] indices = new int[len];
            int j = 0;
            for (int i = 0; i < len; i += 6, j += 4) {
                indices[i] = j;
                indices[i + 1] = j + 1;
                indices[i + 2] = j + 2;
                indices[i + 3] = j + 2;
                indices[i + 4] = j + 3;
                indices[i + 5] = j;
            }
            uploadData(indices);
            primitiveType = GL_UNSIGNED_INT;
        }
    }

    public void uploadData(final short[] indices) {
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public void uploadData(final int[] indices) {
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public int primitiveType() { return primitiveType; }

    public void delete() { glDeleteBuffers(id); }
}
