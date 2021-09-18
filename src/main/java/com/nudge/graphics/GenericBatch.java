package com.nudge.graphics;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

/**
 * @author Frederik Dahl
 * 05/07/2021
 */


public abstract class GenericBatch<T extends BatchOBJ> implements Disposable {

    protected VertexAttributeArray vao;
    protected VertexBufferObject vbo;
    protected ElementBufferArray ebo;
    protected ShaderProgram program;
    protected FloatBuffer vertices;

    private int count;
    private int renderCalls;
    private int renderCallsHigh;
    private final int capacity;

    /**
     * GenericBatch constructor
     * @param capacity The maximum number of BatchOBJ "objects" buffered to the GPU in one call. (The size of the batch.)
     */

    public GenericBatch(int capacity) {
        this.capacity = capacity;
        this.count = 0;
        this.renderCalls = 0;
        this.renderCallsHigh = 0;
    }

    private boolean drawing;

    public abstract void init();

    public abstract void draw(T obj);

    public abstract void render();

    public void begin() {
        if (drawing) throw new IllegalStateException("Batch.end must be called before begin.");
        renderCalls = 0;
        drawing = true;
    }

    public void end() {
        if (!drawing) throw new IllegalStateException("Batch.begin must be called before end.");
        if (count > 0) flush();
        renderCallsHigh = Math.max(renderCalls, renderCallsHigh);
        drawing = false;
    }

    public void flush() {
        if (count == 0) return;
        vertices.flip();
        renderCalls++;
        render();
        vertices.clear();
        count = 0;
    }

    public VertexAttributeArray vao() {
        return vao;
    }

    public VertexBufferObject vbo() {
        return vbo;
    }

    public ElementBufferArray ebo() {
        return ebo;
    }

    public FloatBuffer vertices() {
        return vertices;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public int count() {
        return count;
    }

    public int capacity() {
        return capacity;
    }

    public int renderCalls() {
        return renderCalls;
    }

    public int highestCallCount() {
        return renderCallsHigh;
    }

    @Override
    public void dispose() {
        MemoryUtil.memFree(vertices);
        program.delete();
        ebo.delete();
        vbo.delete();
        vao.delete();
    }
}
