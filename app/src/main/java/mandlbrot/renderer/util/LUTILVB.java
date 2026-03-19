package mandlbrot.renderer.util;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43.glDebugMessageCallback;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DataBufferInt;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLDebugMessageCallback;

public class LUTILVB {

    public static void init(){
        // makes glfw wake up
        GLFWErrorCallback.createPrint(System.err).set();
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    }

    public static int createVertexArray(float vertices[], int indices[]){
		// create a vertexBuffer to store all of the vertexes in
		int vertexBuffer = glGenBuffers(); 

		// create a vertexArray to make things easier (?)
		int vertexArray = glGenVertexArrays();

		// create an element buffer to allow reusing of the vertexes
		int elementBuffer = glGenBuffers();

		// ..:: Initialization code :: ..
		// 1. bind Vertex Array Object
		glBindVertexArray(vertexArray);
		// 2. copy our vertices array in a vertex buffer for OpenGL to use
		glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		// 3. copy our index array in a element buffer for OpenGL to use
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
		return vertexArray;
	}

    public static int createVertexArray(float vertices[]){
		// create a vertexBuffer to store all of the vertexes in
		int vertexBuffer = glGenBuffers(); 

		// create a vertexArray to make things easier (?)
		int vertexArray = glGenVertexArrays();

		// ..:: Initialization code :: ..
		// 1. bind Vertex Array Object
		glBindVertexArray(vertexArray);
		// 2. copy our vertices array in a vertex buffer for OpenGL to use
		glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		return vertexArray;
	}

    public static int createVertexArray(){

		// create a vertexArray to make things easier (?)
		int vertexArray = glGenVertexArrays();

		// ..:: Initialization code :: ..
		// 1. bind Vertex Array Object
		glBindVertexArray(vertexArray);
		// 2. copy our vertices array in a vertex buffer for OpenGL to use
		glBindBuffer(GL_ARRAY_BUFFER, vertexArray);
		return vertexArray;
	}

    public static long createWindow(int sizex, int sizey, String name){
        glfwInit();
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		// make a window
		long window = glfwCreateWindow(sizex, sizey, name, NULL, NULL);
		if(window == NULL){
			glfwTerminate();
			System.exit(1);
		}
		glfwMakeContextCurrent(window);
		//glViewport(0, 0, sizex, sizey);
		glfwSetWindowSizeCallback(window, (windowInner, width, height) -> {
			glViewport(0, 0, width, height);
		});
        GL.createCapabilities();
        glfwSwapInterval(1);
        return window;
    }

	private static String loadAsString(String location){
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(location));
            String buffer = "";
            while ((buffer = reader.readLine())!=null) {
                result.append(buffer);
                result.append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return result.toString();
    }

    public static int loadShader(String vertPath, String fragPath){
        String vert = loadAsString(System.getProperty("user.dir") + "\\app\\src\\main\\java\\mandlbrot\\renderer\\shaderPrograms\\" + vertPath + ".vert");
        String frag = loadAsString(System.getProperty("user.dir") + "\\app\\src\\main\\java\\mandlbrot\\renderer\\shaderPrograms\\" + fragPath + ".frag");

        return create(vert,frag);
    }

    private static int create(String vert, String frag){

        int program = glCreateProgram();

        int vertId = glCreateShader(GL_VERTEX_SHADER);
        int fragId = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertId, vert);
        glShaderSource(fragId, frag);

        glCompileShader(vertId);
        if(glGetShaderi(vertId,GL_COMPILE_STATUS) == GL_FALSE){
            System.out.println("vertex shader compile fail :(");
            System.out.println(glGetShaderInfoLog(vertId));
        }

        glCompileShader(fragId);

        if(glGetShaderi(fragId,GL_COMPILE_STATUS) == GL_FALSE){
            System.out.println("fragment shader compile fail :(");
            System.out.println(glGetShaderInfoLog(fragId));
        }

        glAttachShader(program, vertId);
        glAttachShader(program, fragId);

        glLinkProgram(program);
        glValidateProgram(program);

        if(glGetProgrami(program,GL_VALIDATE_STATUS) == GL_FALSE){
            System.out.println("program link fail :(");
            System.out.println(glGetProgrami(program, GL_INFO_LOG_LENGTH));
            System.out.println(glGetProgramInfoLog(program));
        }

        glDeleteShader(vertId);
        glDeleteShader(fragId);

        return program;
    }

    public static int[] getImageDims(String imageName){
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new File(System.getProperty("user.dir") + "\\app\\src\\main\\java\\jlwgl\\textures\\" + imageName));
        } catch (Exception e) {
            System.out.println("Failed to load " + imageName);
            System.out.println(e.getMessage());
            return null;
        }
        return new int[] {bi.getWidth(),bi.getHeight()};
    }

    public static ByteBuffer loadImage(String imageName){
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new File(System.getProperty("user.dir") + "\\app\\src\\main\\java\\jlwgl\\textures\\" + imageName));
            BufferedImage convertedImg = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
            convertedImg.getGraphics().drawImage(bi, 0, 0, null);
            bi = convertedImg;
        } catch (Exception e) {
            System.out.println("Failed to load " + imageName);
            System.out.println(e.getMessage());
            return null;
        }

        ByteBuffer byteBuffer;
        DataBuffer dataBuffer = bi.getRaster().getDataBuffer();

        if (dataBuffer instanceof DataBufferByte) {
            byte[] pixelData = ((DataBufferByte) dataBuffer).getData();
            byteBuffer = ByteBuffer.wrap(pixelData);
        }
        else if (dataBuffer instanceof DataBufferUShort) {
            short[] pixelData = ((DataBufferUShort) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
            byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
        }
        else if (dataBuffer instanceof DataBufferShort) {
            short[] pixelData = ((DataBufferShort) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
            byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
        }
        else if (dataBuffer instanceof DataBufferInt) {
            int[] pixelData = ((DataBufferInt) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 4);
            byteBuffer.asIntBuffer().put(IntBuffer.wrap(pixelData));
        }
        else {
            throw new IllegalArgumentException("Not implemented for data buffer type: " + dataBuffer.getClass());
        }
        return byteBuffer;
    }

    public static void enableDebug(){
        glEnable(GL_DEBUG_OUTPUT);
        glDebugMessageCallback(GLDebugMessageCallback.create(
            (source, type, id, severity, length, message, userParam) -> {
                // ignore just notifications like: Buffer object 1 (bound to GL_ARRAY_BUFFER_ARB, usage hint is GL_STATIC_DRAW)
                if (severity == 33387) {
                    return;
                }
                System.err.println("OpenGL Debug Message:");
                System.err.println("    Source: " + source);
                System.err.println("    Type: " + type);
                System.err.println("    ID: " + id);
                System.err.println("    Severity: " + severity);
                System.err.println("    Message: " + GLDebugMessageCallback.getMessage(length, message));
            }
        ),0);
    }

    public static float lerpBetweenMany(float[] vals, double[] timeAlong, double time){
        if(time<=timeAlong[0]){
            return vals[0];
        }
        if(time>=timeAlong[timeAlong.length-1]){
            return vals[vals.length-1];
        }

        for (int i = 0; i < vals.length; i++) {
            if(time == timeAlong[i]){
                return vals[i];
            }
            if(time<timeAlong[i]){
                float lt = (float)((time-timeAlong[i-1])/(timeAlong[i]-timeAlong[i-1]));
                return vals[i-1]*(1-lt)+vals[i]*lt;
            }
        }
        return vals[vals.length-1];
    }

    public static Vector3f lerpBetweenManyVector3f(Vector3f[] vals,double[] timeAlong,double t){
	    float[] rl = new float[vals.length];
	    float[] gl = new float[vals.length];
	    float[] bl = new float[vals.length];
        for (int i = 0; i < vals.length; i++) {
	    	rl[i] = vals[i].x;
	    	gl[i] = vals[i].y;
	    	bl[i] = vals[i].z;
        }
	    float r = lerpBetweenMany(rl,timeAlong,t);
	    float g = lerpBetweenMany(gl,timeAlong,t);
	    float b = lerpBetweenMany(bl,timeAlong,t);
	    return new Vector3f(r,g,b);
    }

    public static String defaultTexture = "CubeTexture.png";
    public static String defaultSpecular = "container2_specular.png";

    public static float cubeVertices[] = {
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.25f, 0.0f,
         0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.5f, 0.0f,
         0.5f,  0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.5f, 0.333333f,
         0.5f,  0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.5f, 0.333333f,
        -0.5f,  0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.25f, 0.333333f,
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.25f, 0.0f,
    
        -0.5f, -0.5f,  0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.333333f,
         0.5f, -0.5f,  0.5f, 0.0f, 0.0f, 1.0f, 0.25f, 0.333333f,
         0.5f,  0.5f,  0.5f, 0.0f, 0.0f, 1.0f, 0.25f, 0.666666f,
         0.5f,  0.5f,  0.5f, 0.0f, 0.0f, 1.0f, 0.25f, 0.666666f,
        -0.5f,  0.5f,  0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.666666f,
        -0.5f, -0.5f,  0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.333333f,
    
        -0.5f,  0.5f,  0.5f, -1.0f,  0.0f, 0.0f, 0.25f, 0.333333f,
        -0.5f,  0.5f, -0.5f, -1.0f,  0.0f, 0.0f, 0.5f, 0.333333f,
        -0.5f, -0.5f, -0.5f, -1.0f,  0.0f, 0.0f, 0.5f, 0.666666f,
        -0.5f, -0.5f, -0.5f, -1.0f,  0.0f, 0.0f, 0.5f, 0.666666f,
        -0.5f, -0.5f,  0.5f, -1.0f,  0.0f, 0.0f, 0.25f, 0.666666f,
        -0.5f,  0.5f,  0.5f, -1.0f,  0.0f, 0.0f, 0.25f, 0.333333f,
    
         0.5f,  0.5f,  0.5f, 1.0f,  0.0f, 0.0f, 0.5f, 0.333333f,
         0.5f,  0.5f, -0.5f, 1.0f,  0.0f, 0.0f, 0.75f, 0.333333f,
         0.5f, -0.5f, -0.5f, 1.0f,  0.0f, 0.0f, 0.75f, 0.666666f,
         0.5f, -0.5f, -0.5f, 1.0f,  0.0f, 0.0f, 0.75f, 0.666666f,
         0.5f, -0.5f,  0.5f, 1.0f,  0.0f, 0.0f, 0.5f, 0.666666f,
         0.5f,  0.5f,  0.5f, 1.0f,  0.0f, 0.0f, 0.5f, 0.333333f,
    
        -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.75f, 0.333333f,
         0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.333333f,
         0.5f, -0.5f,  0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.666666f,
         0.5f, -0.5f,  0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.666666f,
        -0.5f, -0.5f,  0.5f, 0.0f, -1.0f, 0.0f, 0.75f, 0.666666f,
        -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.75f, 0.333333f,
    
        -0.5f,  0.5f, -0.5f, 0.0f,  1.0f,  0.0f, 0.25f, 0.666666f,
         0.5f,  0.5f, -0.5f, 0.0f,  1.0f,  0.0f, 0.5f, 0.666666f,
         0.5f,  0.5f,  0.5f, 0.0f,  1.0f,  0.0f, 0.5f, 1.0f,
         0.5f,  0.5f,  0.5f, 0.0f,  1.0f,  0.0f, 0.5f, 1.0f,
        -0.5f,  0.5f,  0.5f, 0.0f,  1.0f,  0.0f, 0.25f, 1.0f,
        -0.5f,  0.5f, -0.5f, 0.0f,  1.0f,  0.0f, 0.25f, 0.666666f,
    };

    public static int[] cubeIndicies = {
        0,1,2,
        3,4,5,

        6,7,8,
        9,10,11,

        12,13,14,
        15,16,17,

        18,19,20,
        21,22,23,

        24,25,26,
        27,28,29,

        30,31,32,
        33,34,35
    };
}
