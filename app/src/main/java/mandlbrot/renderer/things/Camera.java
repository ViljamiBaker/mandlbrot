package mandlbrot.renderer.things;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;

//import glame.game.util.GUTILVB;

public class Camera {
	public Vector3f position = new Vector3f(0.0f, 0.0f,  0.0f);
	public Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
	public Vector3f up = new Vector3f(0.0f, 1.0f,  0.0f);
	public float fov = 1.0f;
	public float yaw = 0;
	public float pitch = 0;
	public boolean projectionGood = false;

	private float lastX = 400, lastY = 300;
	private boolean firstMouse = true;
    private long window;

	private Matrix4f lastProjection;

	boolean mouseMoveEnabled = true;
    public Camera(long window){
        this.window = window;
    }

	public void enableCameraMovement(){
		glfwSetCursorPosCallback(window, (windowInner, xpos, ypos)->{
			if(!mouseMoveEnabled)return;
			if (firstMouse) // initially set to true
			{
				lastX = (float)xpos;
				lastY = (float)ypos;
				firstMouse = false;
			}

			float xoffset = (float)xpos - lastX;
			float yoffset = lastY - (float)ypos;
			lastX = (float)xpos;
			lastY = (float)ypos;
			
			float sensitivity = 0.002f;
			xoffset *= sensitivity;
			yoffset *= sensitivity;

			yaw   += xoffset;
			pitch += yoffset;  

			if(pitch > 1.55f)
			pitch =  1.55f;
			if(pitch < -1.55f)
			pitch = -1.55f;

			//front = GUTILVB.eulerAngToVector3(pitch, yaw);
		}); 
	}

    public Matrix4f getProjection(){
		if(projectionGood){
			return lastProjection;
		}
		projectionGood = true;
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetWindowSize(window, width, height);
		Matrix4f projection;
		if(!ortho){
			projection = new Matrix4f().perspective(fov, (float)width[0]/(float)height[0], 0.01f, 2000.0f);
		}else{
			projection =  new Matrix4f().ortho(-1, 1, -1, 1,0.01f, 100.0f);
		}
		lastProjection = projection;

		return projection;
    }

	public Matrix4f getVeiw(){
        return new Matrix4f().lookAt(position, position.add(front, new Vector3f()), up);
    }

	boolean hDownLast = false;
	boolean ortho = false;
    public void processInput(){
		if(glfwGetKey(window, GLFW_KEY_R)==GLFW_PRESS){
			fov += 0.05f;
			projectionGood = false;
		}
		if(glfwGetKey(window, GLFW_KEY_F)==GLFW_PRESS){
			fov -= 0.05f;
			projectionGood = false;
		}
		if(glfwGetKey(window, GLFW_KEY_H)==GLFW_PRESS&&!hDownLast){
			ortho = !ortho;
			projectionGood = false;
			System.out.println(ortho);
		}
		hDownLast = glfwGetKey(window, GLFW_KEY_H)==GLFW_PRESS;
    }

	public boolean projectionGood(){
		return projectionGood;
	}
	public void setMouseMoveEnabled(boolean mouseMoveEnabled) {
		this.mouseMoveEnabled = mouseMoveEnabled;
		if(mouseMoveEnabled){
			firstMouse = true;
		}
	}
}
