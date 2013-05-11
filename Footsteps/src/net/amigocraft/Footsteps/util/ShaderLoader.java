package net.amigocraft.Footsteps.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.amigocraft.Footsteps.util.ShaderLoader;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderLoader {
	
    public static int loadShaderPair(String vertexShaderLocation, String fragmentShaderLocation) {
        int shaderProgram = glCreateProgram();
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        StringBuilder vertexShaderSource = new StringBuilder();
        StringBuilder fragmentShaderSource = new StringBuilder();
        BufferedReader vertexShaderFileReader = null;
        try {
        	vertexShaderFileReader = new BufferedReader(new InputStreamReader(ShaderLoader.class.getResourceAsStream(vertexShaderLocation)));
            String line;
            while ((line = vertexShaderFileReader.readLine()) != null) {
                vertexShaderSource.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (vertexShaderFileReader != null) {
                try {
                    vertexShaderFileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        BufferedReader fragmentShaderFileReader = null;
        try {
        	fragmentShaderFileReader = new BufferedReader(new InputStreamReader(ShaderLoader.class.getResourceAsStream(fragmentShaderLocation)));
            String line;
            while ((line = fragmentShaderFileReader.readLine()) != null) {
                fragmentShaderSource.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (fragmentShaderFileReader != null) {
                try {
                    fragmentShaderFileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Vertex shader could not compile correctly. Error log:");
            System.err.println(glGetShaderInfoLog(vertexShader, 1024));
            return -1;
        }
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Fragment shader could not compile correctly. Error log:");
            System.err.println(glGetShaderInfoLog(fragmentShader, 1024));
        }
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram);
        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Shader program was not linked properly");
            System.err.println(glGetProgramInfoLog(shaderProgram, 1024));
            return -1;
        }
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        return shaderProgram;
    }
}