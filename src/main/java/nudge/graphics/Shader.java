package nudge.graphics;

import java.io.*;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;

public class Shader {

    private final int id;
    private final int type;

    private Shader(int type) {

        this.type = type;

        id = glCreateShader(type);
    }

    public void compile() {

        glCompileShader(id);

        checkStatus();
    }

    private void checkStatus() {

        int status = glGetShaderi(id, GL_COMPILE_STATUS);

        if (status != GL_TRUE) {

            throw new RuntimeException(glGetShaderInfoLog(id));
        }
    }

    public void setSourceCode(CharSequence source) { glShaderSource(id, source); }

    // Call this after linking
    public void delete() { glDeleteShader(id); }

    public int getID() { return id; }

    public int type() { return type; }

    public static Shader fromSource(int type, CharSequence source) {

        Shader shader = new Shader(type);

        shader.setSourceCode(source);

        shader.compile();

        return shader;
    }

    public static Shader loadShader(int type, String path) {

        StringBuilder builder = new StringBuilder();

        try (InputStream in = new FileInputStream(path);

             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            String line;

            while ((line = reader.readLine()) != null) {

                builder.append(line).append("\n");
            }
        } catch (IOException e) {

            throw new RuntimeException(
                            "Failed to load a " +
                            typePrefix(type) +
                            " shader file" +
                            System.lineSeparator() +
                            e.getMessage());
        }
        CharSequence source = builder.toString();

        return fromSource(type, source);
    }

    public String typePrefix() {

        return typePrefix(type);
    }

    public static String typePrefix(int type) {

        String prefix;

        switch (type) {
            case GL_VERTEX_SHADER: prefix = "Vertex"; break;
            case GL_FRAGMENT_SHADER: prefix = "Fragment"; break;
            case GL_GEOMETRY_SHADER: prefix = "Geometry"; break;
            case GL_TESS_CONTROL_SHADER: prefix = "Tess control"; break;
            case GL_TESS_EVALUATION_SHADER: prefix = "Tess evaluation"; break;
            default: prefix = "Invalid";
        }
        return prefix;
    }
}
