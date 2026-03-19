package mandlbrot.renderer;

import static mandlbrot.renderer.util.LUTILVB.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import org.joml.Vector2f;
import org.joml.Vector3f;

import mandlbrot.renderer.things.Shader;
import mandlbrot.renderer.util.LUTILVB;
import mandlbrot.renderer.util.Mandlbrot;

//https://learnopengl.com/Getting-started/OpenGL
//https://www.lwjgl.org/guide
public class Renderer {

	static boolean wireframe = false;
	static boolean drawDebug = false;
	static boolean contCalc = false;
	static boolean linesDraw = true;

	static int iterations = 300;
	static float bailOutDist = 3;

	static Shader shaderProgram1;
	static Shader shaderProgramUI;
	static Shader shaderProgramL;

	static int vertexArray1;
	static int vertexArrayUI;

	static long window;

	static float ratio = 1;

	static Vector2f cursorPos = new Vector2f();
	static Vector2f convPos = new Vector2f();

	private static float lastFrame = 0.0f; // Time of last frame
	public static float deltaTime = 0.0f;	// Time between current frame and last frame
	//(ZSetR,ZSetI,CSetR,CSetI,ESetR)
	//{ 0, 0,-1,-2, 0};
	//{ 0, 0, 0, 0, 2};
	public static int[] setters = { 0, 0,-1,-2, 0};
	public static float[] values= { 0, 0, 0, 0, 2};

	static Vector2f camPos = new Vector2f();
	static float camZoom = 1.0f;

	private static int createVertexArray(float vertices[], int indices[]){
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
		// 4. then set the vertex attributes pointers
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
		glEnableVertexAttribArray(0); 
		return vertexArray;
	}

	public static void drawSquare(Vector2f scale, Vector2f xy, Vector3f rgb){
		shaderProgramUI.use();
		shaderProgramUI.setFloat("ScreenRatio", ratio);
		shaderProgramUI.setUniform("Scale", scale);
		shaderProgramUI.setUniform("Offset", xy);
		shaderProgramUI.setUniform("Color", rgb);
		
		glBindVertexArray(vertexArrayUI);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}

	public static void run(){
		
		init();

		window = createWindow(1680, 945, "mandlgyatt");

		glfwSetCursorPosCallback(window, (window,xpos,ypos)->{
			int[] width = new int[1];
			int[] height = new int[1];
			glfwGetWindowSize(window, width, height);
			cursorPos.set(xpos/(double)width[0]*2.0-1.0,ypos/(double)height[0]*-2.0+1.0);
			if(contCalc){
				convPos = cursorPos.div(camZoom,new Vector2f()).sub(camPos);
				convPos.x *= ratio;
			}
		});
		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if(button == GLFW_MOUSE_BUTTON_RIGHT&&action == GLFW_PRESS){
				convPos = cursorPos.div(camZoom,new Vector2f()).sub(camPos);
				convPos.x *= ratio;
			}
		});

		// ----------- Creation of the shaderProgram -----------

		shaderProgram1 = new Shader("shaderMV", "shaderMF");
		shaderProgramUI = new Shader("shaderColorV", "shaderColorF");
		shaderProgramL = new Shader("shaderLineV", "shaderLineF");

		//Shader shaderProgram2 = new Shader("shaderVertex2", "shaderFrag2");

		// ----------- creation of all of the buffers -----------

		// create the vertexes to use in triangles
		float vertices[] = {
			-1f,  -1f, 0.0f,  
			-1f, 1f, 0.0f,  
			 1f, 1f, 0.0f,  
			 1f,  -1f, 0.0f
		};
		// create many triangles with reused indecies for efficientices
		int indices[] = {
			3, 1, 0,   
			3, 2, 1,  
		};
		// create a vertexBuffer to store all of the vertexes in
		vertexArray1 = createVertexArray(vertices, indices); 
		vertexArrayUI = createVertexArray(vertices, indices); 

		// ----------- render loop ----------- 
		while(!glfwWindowShouldClose(window))
		{

			//glfwSetTime(0.1);
			float currentFrame = (float)glfwGetTime();
			deltaTime = currentFrame - lastFrame;
			//System.out.println((int)(1/deltaTime) + " |||||| " + currentFrame + " |||||| " + lastFrame + " |||||| " + deltaTime);
			lastFrame = currentFrame;  

			// input
			processInput();
			

			//render code		
			glClearColor(0.2f, 0.3f, 0.3f, 0f);
			glClear(GL_COLOR_BUFFER_BIT);

			shaderProgram1.use();
			shaderProgram1.setFloat("bailout", bailOutDist);
			shaderProgram1.setInt("iterations", iterations);
			shaderProgram1.setUniform("ZSetR", new Vector2f(setters[0],values[0]));
			shaderProgram1.setUniform("ZSetI", new Vector2f(setters[1],values[1]));
			shaderProgram1.setUniform("CSetR", new Vector2f(setters[2],values[2]));
			shaderProgram1.setUniform("CSetI", new Vector2f(setters[3],values[3]));
			shaderProgram1.setUniform("ESetR", new Vector2f(setters[4],values[4]));
			shaderProgram1.setUniform("CamPos", camPos);
			shaderProgram1.setFloat("CamZoom", camZoom);

			int[] width = new int[1];
			int[] height = new int[1];
			glfwGetWindowSize(window, width, height);
			ratio = (float)width[0]/(float)height[0];
			shaderProgram1.setFloat("ScreenRatio", ratio);

			glBindVertexArray(vertexArray1);
			glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
			glBindVertexArray(0);

			if(linesDraw){
				lineVertexData = Mandlbrot.CalcMandlbrot(
					convPos, 
					new Vector2f(setters[0], values[0]), 
					new Vector2f(setters[1], values[1]), 
					new Vector2f(setters[2], values[2]), 
					new Vector2f(setters[3], values[3]), 
					new Vector2f(setters[4], values[4]), 
					iterations,
					bailOutDist
				);

				drawLines(new Vector3f(0.2f));
	
				lineVertexData = new float[] {0,0,convPos.x,convPos.y};
				
				drawLines(new Vector3f(0.9f,0.5f,0.2f));
				
				lineVertexData = new float[] {-bailOutDist,-bailOutDist,-bailOutDist,bailOutDist,bailOutDist,bailOutDist,bailOutDist,-bailOutDist,-bailOutDist,-bailOutDist};
				
				drawLines(new Vector3f(0.0f,0.3f,0.8f));
			}

			if(drawDebug){
				double fps = 1/deltaTime;
				float size = (float)Math.min(fps/60.0f,1.0f);
				drawSquare(new Vector2f(0.055f,0.11f), new Vector2f(-0.95f, 0.8f), new Vector3f(0.2f, 0.2f, 0.2f));
				drawSquare(new Vector2f(0.05f,0.1f*size), new Vector2f(-0.95f, 0.8f-(0.1f*(1.0f-size))), LUTILVB.lerpBetweenManyVector3f(
					new Vector3f[] {
						new Vector3f(0.867f,0.062f,0.062f),
						new Vector3f(0.867f,0.867f,0.062f),
						new Vector3f(0.101f,0.855f,0.226f),
					}, 
					new double[] {0.5,0.75,1}, 
					size
				));
				drawSquare(new Vector2f(0.11f,0.11f), new Vector2f(-0.8f + 0.15f * selectedIndex, 0.8f), new Vector3f(0.2f, 0.2f, 0.2f));
				for (int i = 0; i < setters.length; i++) {
					if(setters[i]==0){
						drawSquare(new Vector2f(0.1f,0.1f), new Vector2f(-0.8f + 0.15f * (float)i, 0.8f), new Vector3f(0.85f, 0.25f, 0.2f));
					}else if(setters[i]==-1){
						drawSquare(new Vector2f(0.1f,0.1f), new Vector2f(-0.8f + 0.15f * (float)i, 0.8f), new Vector3f(0.25f, 0.85f, 0.2f));
					}else{
						drawSquare(new Vector2f(0.1f,0.1f), new Vector2f(-0.8f + 0.15f * (float)i, 0.8f), new Vector3f(0.25f, 0.2f, 0.85f));
					}
				}
			}

			// check events and swap buffers
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
		glfwTerminate();
	}

	static int lineVertexBuffer;
	static int lineVertexArray;
	static float[] lineVertexData = {0.0f,0.0f, 0.2f,0.0f};
	static boolean createdArrays = false;

	public static void drawLines(Vector3f Color){
		if(!createdArrays){
			int vertexBuffer = glGenBuffers(); 

			int vertexArray = glGenVertexArrays();

			glBindVertexArray(vertexArray);
			glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
			glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
			glEnableVertexAttribArray(0); 
			lineVertexBuffer = vertexBuffer;
			lineVertexArray = vertexArray;
			createdArrays = true;
		}
		shaderProgramL.use();
		shaderProgramL.setUniform("Color", Color);
		shaderProgramL.setUniform("CamPos", camPos);
		shaderProgramL.setFloat("CamZoom", camZoom);

		shaderProgramL.setFloat("ScreenRatio", ratio);

		glBindVertexArray(lineVertexArray);
		glBufferData(GL_ARRAY_BUFFER, lineVertexData, GL_STATIC_DRAW);
		glDrawArrays(GL_LINE_STRIP, 0, lineVertexData.length/2);
		glBindVertexArray(0);
	}

	static int maxKey = 300;

	static boolean[] keysDown = new boolean[maxKey];
	static boolean[] keysDownLast = new boolean[maxKey];
	static boolean[] keysPressed = new boolean[maxKey];

	public static void updateKeysDown(long window){
		for (int i = 32; i < maxKey; i++) {
			keysDown[i] = glfwGetKey(window, i) == GLFW_PRESS;
			keysPressed[i] = keysDown[i]&&!keysDownLast[i];
			keysDownLast[i] = glfwGetKey(window, i) == GLFW_PRESS;
		}
	}

	public static boolean isKeyDown(int key){
		return keysDown[key];
	}

	public static boolean isKeyPressed(int key){
		return keysPressed[key];
	}

	public static void setHelper(int key, int index, int value){
		if(isKeyPressed(key))
			if(setters[index] == value){
				setters[index] = 0;
			}else{
				for (int i = 0; i < setters.length; i++) {
					if(i==index){
						setters[i] = value;
					}else if(setters[i] == value){
						setters[i] = 0;
					}
				}
			}
	}

	public static void valHelper(int key, int index, int val){
		if(isKeyDown(key)){
			if(setters[index]==0){
				values[index] += val*deltaTime;
			}
		}
	}

	static int selectedIndex = 0;

	public static void processInput()
	{
		updateKeysDown(window);
		if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
			glfwSetWindowShouldClose(window, true);
		
		/*if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS){
			if(!wireframe){
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			}else{
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			}
			wireframe = !wireframe;
		}*/

		if(isKeyPressed(GLFW_KEY_E)){
			drawDebug = !drawDebug;
		}
		
		if(isKeyDown(GLFW_KEY_W))
			camPos.y-=0.5*deltaTime/camZoom;
		
		if(isKeyDown(GLFW_KEY_S))
			camPos.y+=0.5*deltaTime/camZoom;
		
		if(isKeyDown(GLFW_KEY_D))
			camPos.x-=0.5*deltaTime/camZoom;

		if(isKeyDown(GLFW_KEY_A))
			camPos.x+=0.5*deltaTime/camZoom;
		
		if(isKeyDown(GLFW_KEY_R))
			camZoom*=1+(0.5*deltaTime);
	
		if(isKeyDown(GLFW_KEY_F))
			camZoom*=1-(0.5*deltaTime);

		if(isKeyDown(GLFW_KEY_Z))
			bailOutDist+=10*deltaTime;
	
		if(isKeyDown(GLFW_KEY_X))
			bailOutDist-=10*deltaTime;

		if(isKeyDown(GLFW_KEY_C))
			iterations+=1000*deltaTime;
	
		if(isKeyDown(GLFW_KEY_V)){
			iterations-=1000*deltaTime;
			if(iterations<1){
				iterations = 1;
			}
		}
			

		if(isKeyPressed(GLFW_KEY_T)){
			values[0] = 0;
			values[1] = 0;
			values[2] = 0;
			values[3] = 0;
			values[4] = 2;
			camPos = new Vector2f();
			camZoom = 1.0f;
		}
		if(isKeyPressed(GLFW_KEY_G)){
			contCalc = !contCalc;
		}
		if(isKeyPressed(GLFW_KEY_Y)){
			linesDraw = !linesDraw;
		}


		if(isKeyPressed(GLFW_KEY_LEFT)){
			selectedIndex--;
			if(selectedIndex == -1){
				selectedIndex = setters.length-1;
			}
		}

		if(isKeyPressed(GLFW_KEY_RIGHT)){
			selectedIndex++;
			if(selectedIndex == setters.length){
				selectedIndex = 0;
			}
		}

		if(isKeyDown(GLFW_KEY_UP)){
			values[selectedIndex] += 1*deltaTime;
		}

		if(isKeyDown(GLFW_KEY_DOWN)){
			values[selectedIndex] += -1*deltaTime;
		}

		setHelper(GLFW_KEY_1, selectedIndex, -1);
		setHelper(GLFW_KEY_2, selectedIndex, -2);
	}

	public static void main(String[] args) {
		Renderer.run();
	}
}