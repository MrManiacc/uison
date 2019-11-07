package me.jraynor.misc;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * This represents a glsl shader
 */
public abstract class Shader {
    private int programID, vertexID, fragmentID;
    private String path = "src/main/resources/shaders/";
    private final Map<String, Integer> uniforms;
    private final FloatBuffer matrixBuffer;
    @Getter
    private boolean started = false;

    public Shader(String name) {
        this.path += name + ".glsl";
        uniforms = new HashMap<>();
        matrixBuffer = BufferUtils.createFloatBuffer(16);
        createShader();
        doUniformBinds();
    }

    /**
     * Binding all of the vaos to their correct location in the shader
     */
    protected abstract void doBinds();

    /**
     * Binding all of the uniforms to their correct location in the shader
     */
    protected abstract void doUniformBinds();

    /**
     * Binds a texture
     *
     * @param name texture name
     */
    private void uniformSampleBind(String name) {
        uniforms.put(name, GL20.glGetUniformLocation(programID, name));
    }

    /**
     * Binds a uniform
     *
     * @param name uniform name
     */
    private void bindUniform(String name) {
        int id = GL20.glGetUniformLocation(programID, name);
        uniforms.put(name, id);
    }

    /**
     * Used for easy creation of shader binds
     *
     * @param binds
     */
    protected void binds(ShaderBind... binds) {
        for (ShaderBind shaderBind : binds) {
            bind(shaderBind.getLocation(), shaderBind.getName());
        }
    }

    /**
     * Used for easy creation of uniforms
     *
     * @param uniforms
     */
    public void bindUniforms(String... uniforms) {
        for (String uniform : uniforms) {
            bindUniform(uniform);
        }
    }

    /**
     * Binds a vao
     *
     * @param attribute vao id
     * @param name      the name of attribute in the vao
     */
    private void bind(int attribute, String name) {
        GL20.glBindAttribLocation(programID, attribute, name);
    }

    /**
     * Must be called before using shader
     */
    public void start() {
        GL20.glUseProgram(programID);
        started = true;
    }

    /**
     * Must be called after using shader
     */
    public void stop() {
        GL20.glUseProgram(0);
        started = false;
    }

    /**
     * Cleans up and unloads the shader
     */
    public void cleanUp() {
        stop();
        GL20.glDetachShader(programID, vertexID);
        GL20.glDetachShader(programID, fragmentID);
        GL20.glDeleteShader(vertexID);
        GL20.glDeleteShader(fragmentID);
        GL20.glDeleteProgram(programID);
    }

    /**
     * Pass a uniform float to shader
     *
     * @param name  uniform's name
     * @param value the value of the uniform
     */
    public void loadFloat(String name, float value) {
        GL20.glUniform1f(uniforms.get(name), value);
    }

    /**
     * Pass a uniform texture to shader
     *
     * @param name        uniform's name
     * @param textureUnit the value of the texture
     */
    public void loadSampler(String name, int textureUnit) {
        GL20.glUniform1i(uniforms.get(name), textureUnit);
    }

    /**
     * Pass a vec3 to a shader
     *
     * @param name the uniforms name
     * @param vec  the vec to passed to the shader
     */
    public void loadVec3(String name, Vector3f vec) {
        GL20.glUniform3f(uniforms.get(name), vec.x, vec.y, vec.z);
    }

    /**
     * loads vec4 to the shader
     *
     * @param name the vec4 name in the shader
     * @param vec  the vec4 value
     */
    public void loadVec4(String name, Vector4f vec) {
        GL20.glUniform4f(uniforms.get(name), vec.x, vec.y, vec.z, vec.w);
    }

    /**
     * loads boolean to the shader
     *
     * @param name  the boolean name in the shader
     * @param value the boolean value
     */
    public void loadBool(String name, boolean value) {
        int val = (value) ? 1 : 0;
        GL20.glUniform1i(uniforms.get(name), val);
    }

    /**
     * loads matrix to the shader
     *
     * @param name the matrix name in the shader
     * @param mat  the matrix value
     */
    public void loadMat4(String name, Matrix4f mat) {
        mat.get(matrixBuffer);
        GL20.glUniformMatrix4fv(uniforms.get(name), false, matrixBuffer);
    }

    /**
     * loads matrix to the shader
     *
     * @param name the matrix name in the shader
     */
    public void loadVec2(String name, Vector2f vec) {
        GL20.glUniform2f(uniforms.get(name), vec.x, vec.y);
    }


    /**
     * Compiles the shader
     */
    private void createShader() {
        String[] sources = getSources();

        vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexID, sources[0]);
        GL20.glCompileShader(vertexID);
        if (GL20.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(vertexID, 500));
            System.err.println("Failed to compile vertex shader");
            System.exit(-1);
        }

        fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentID, sources[1]);
        GL20.glCompileShader(fragmentID);
        if (GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(fragmentID, 500));
            System.err.println("Failed to compile fragment shader");
            System.exit(-1);
        }

        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexID);
        GL20.glAttachShader(programID, fragmentID);
        doBinds();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
    }

    /**
     * Compiler for the shader's helper method
     *
     * @return compiled lines of code
     */
    private String[] getSources() {
        StringBuilder vertexSource = new StringBuilder();
        StringBuilder fragSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            boolean isInVert = false;
            boolean isInFrag = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (isInVert && !line.startsWith("#frag")) {
                    vertexSource.append(line).append("\n");
                    isInFrag = false;
                } else if (isInFrag) {
                    fragSource.append(line).append("\n");
                    isInVert = false;
                }
                if (line.startsWith("#vert")) {
                    isInVert = true;
                    isInFrag = false;
                } else if (line.startsWith("#frag")) {
                    isInFrag = true;
                    isInVert = false;
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{vertexSource.toString(), fragSource.toString()};
    }
}
