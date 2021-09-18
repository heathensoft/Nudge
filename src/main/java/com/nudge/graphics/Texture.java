package com.nudge.graphics;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

/**
 * @author Frederik Dahl
 * 29/06/2021
 */

public class Texture {

    private int width;
    private int height;
    private final int id;

    private String path;


    public Texture() {
        id = glGenTextures();
    }

    public void setParameter(int name, int value) {
        glTexParameteri(GL_TEXTURE_2D, name, value);
    }

    public void setWrapping(int value) {
        setParameter(GL_TEXTURE_WRAP_S, value);
        setParameter(GL_TEXTURE_WRAP_T, value);
    }

    public void setFiltering(int value) {
        setParameter(GL_TEXTURE_MIN_FILTER, value);
        setParameter(GL_TEXTURE_MAG_FILTER, value);
    }

    public void uploadData(int width, int height, ByteBuffer data) {
        uploadData(GL_RGBA8, width, height, GL_RGBA, data);
    }

    public void uploadData(int internalFormat, int width, int height, int format, ByteBuffer data) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, data);
    }

    public void delete() { glDeleteTextures(id); }

    public void bind() { glBindTexture(GL_TEXTURE_2D, id); }

    public void unBind() { glBindTexture(GL_TEXTURE_2D, 0); }

    public int getId() { return id; }

    public int width() { return width; }

    public int height() { return height; }

    public String getPath() { return path == null ? "Internally Generated" : path; }

    private void setPath(String path) { this.path = path;}

    public void setWidth(int width) { if (width > 0) this.width = width; }

    public void setHeight(int height) { if (height > 0) this.height = height; }

    public static Texture create(int width, int height, ByteBuffer data) {

        return create(GL_RGBA8,width,height,GL_RGBA,data,GL_REPEAT,GL_NEAREST);
    }

    public static Texture create(int width, int height, ByteBuffer data, int wrap, int filter) {

        return create(GL_RGBA8,width,height,GL_RGBA,data,wrap,filter);
    }

    public static Texture create(int internalFormat, int width, int height, int format, ByteBuffer data, int wrap, int filter) {

        if (data == null) throw new IllegalArgumentException("ByteBuffer cannot be null");

        Texture texture = new Texture();
        texture.setWidth(width);
        texture.setHeight(height);

        texture.bind();
        texture.setWrapping(wrap);
        texture.setFiltering(filter);

        texture.uploadData(internalFormat, width, height, format, data);

        texture.setWidth(width);
        texture.setHeight(height);

        return texture;
    }

    public static Texture loadTexture(String path) {

        return loadTexture(path,GL_REPEAT,GL_NEAREST);
    }

    public static Texture loadTexture(String path, int wrap, int filter) {

        ByteBuffer image;

        int width;
        int height;
        int channels;

        try (MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer w = stack.mallocInt(1); // width
            IntBuffer h = stack.mallocInt(1); // height
            IntBuffer c = stack.mallocInt(1); // color components

            stbi_set_flip_vertically_on_load(true);
            image = stbi_load(path, w, h, c, 0);
            if (image == null) {
                throw new RuntimeException("Failed to load texture file"
                        + System.lineSeparator() + stbi_failure_reason());
            }
            width = w.get();
            height = h.get();
            channels = c.get();
        }

        int format;
        int internalFormat;

        switch (channels) {
            case 3: format = internalFormat = GL_RGB; break;
            case 4: format = GL_RGBA8; internalFormat = GL_RGBA; break;
            default: throw new RuntimeException("Unsupported file format");
        }

        Texture texture = create(internalFormat,width, height, format, image, wrap, filter);
        texture.setPath(path);

        stbi_image_free(image);

        return texture;
    }

}
