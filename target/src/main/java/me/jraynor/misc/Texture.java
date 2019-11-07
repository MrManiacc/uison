package me.jraynor.misc;


import lombok.Getter;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    /**
     * Stores the handle of the texture.
     */
    private int id;

    private String path;

    @Getter
    private boolean loaded;

    @Getter
    private boolean hdr;

    /**
     * Width of the texture.
     */
    private int width;
    /**
     * Height of the texture.
     */
    private int height;

    public String registryName;

    /**
     * Creates a texture.
     */
    public Texture() {
        id = glGenTextures();
    }

    /**
     * Creates a texture.
     */
    public Texture(int id) {
        this.id = id;
    }

    /**
     * Binds the texture.
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void bindToUnit(int textureUnit) {
        glActiveTexture(GL_TEXTURE0 + textureUnit);
        bind();
    }

    /**
     * Sets a parameter of the texture.
     *
     * @param name  Name of the parameter
     * @param value Value to set
     */
    public void setParameter(int name, int value) {
        glTexParameteri(GL_TEXTURE_2D, name, value);
    }

    /**
     * Uploads image data with specified width and height.
     *
     * @param width  Width of the image
     * @param height Height of the image
     * @param data   Pixel data of the image
     */
    public void uploadData(int width, int height, ByteBuffer data) {
        uploadData(GL_RGBA8, width, height, GL_RGBA, data);
    }


    /**
     * Uploads image data with specified width and height.
     *
     * @param width  Width of the image
     * @param height Height of the image
     * @param data   Pixel data of the image
     */
    public void uploadDataHDR(int width, int height, FloatBuffer data) {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, width, height, 0, GL_RGB, GL_FLOAT, data);
    }


    /**
     * Uploads image data with specified internal format, width, height and
     * image format.
     *
     * @param internalFormat Internal format of the image data
     * @param width          Width of the image
     * @param height         Height of the image
     * @param format         Format of the image data
     * @param data           Pixel data of the image
     */
    public void uploadData(int internalFormat, int width, int height, int format, ByteBuffer data) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, data);
    }

    /**
     * Delete the texture.
     */
    public void unload() {
        glDeleteTextures(id);
        loaded = false;
    }

    /**
     * Reloads the texture.
     */
    public void load() {
        Texture copy = loadTexture(this.path);
        this.id = copy.id;
        this.width = copy.width;
        this.height = copy.height;
        this.loaded = true;
    }

    public void reload() {
        unload();
        load();
    }

    /**
     * Gets the texture width.
     *
     * @return Texture width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the texture width.
     *
     * @param width The width to set
     */
    public void setWidth(int width) {
        if (width > 0) {
            this.width = width;
        }
    }

    /**
     * Gets the texture height.
     *
     * @return Texture height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the texture height.
     *
     * @param height The height to set
     */
    public void setHeight(int height) {
        if (height > 0) {
            this.height = height;
        }
    }


    /**
     * Creates a texture with specified width, height and data.
     *
     * @param width  Width of the texture
     * @param height Height of the texture
     * @param data   Picture Data in RGBA format
     * @return Texture from the specified data
     */
    public static Texture createTextureHDR(int width, int height, FloatBuffer data, String path) {
        Texture texture = new Texture();
        texture.setWidth(width);
        texture.setHeight(height);
        texture.path = path;
        texture.bind();
        texture.hdr = true;
        texture.uploadDataHDR(width, height, data);
        texture.setParameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        texture.setParameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        texture.setParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        texture.setParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        stbi_image_free(data);
        texture.loaded = true;
        return texture;
    }

    /**
     * Creates a texture with specified width, height and data.
     *
     * @param width  Width of the texture
     * @param height Height of the texture
     * @param data   Picture Data in RGBA format
     * @return Texture from the specified data
     */
    public static Texture createTexture(int width, int height, ByteBuffer data, String path) {
        Texture texture = new Texture();
        texture.setWidth(width);
        texture.setHeight(height);
        texture.path = path;
        texture.bind();
        texture.hdr = false;
        texture.setParameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        texture.setParameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        texture.setParameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        texture.setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        texture.uploadData(GL_RGBA8, width, height, GL_RGBA, data);
        texture.loaded = true;
        stbi_image_free(data);
        return texture;
    }
    /**
     * Load texture from file.
     *
     * @param path File path of the texture
     * @return Texture from specified file
     */
    public static Texture loadTextureHDR(String path) {
        FloatBuffer image;
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            /* Prepare image buffers */
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            /* Load image */
            stbi_set_flip_vertically_on_load(true);
            image = stbi_loadf(path, w, h, comp, 0);
            if (image == null) return null;
            /* Get width and height of image */
            width = w.get();
            height = h.get();

            if(image.hasRemaining()){
                return createTextureHDR( width, height, image, path);
            }

        }
        return null;
    }

    /**
     * Load texture from file.
     *
     * @param path File path of the texture
     * @return Texture from specified file
     */
    public static Texture loadTexture(String path) {
        ByteBuffer image;
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            /* Prepare image buffers */
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            /* Load image */
            stbi_set_flip_vertically_on_load(true);
            image = stbi_load("src/main/resources/textures/" + path, w, h, comp, 4);
            if (image == null) {
                return null;
            }

            /* Get width and height of image */
            width = w.get();
            height = h.get();
        }

        return createTexture(width, height, image, path);
    }

    @Override
    public String toString() {
        return registryName + ": {" +
                "\n\tpath='" + path + "'" +
                "\n\tloaded='" + loaded + "'" +
                "\n}";
    }
}
