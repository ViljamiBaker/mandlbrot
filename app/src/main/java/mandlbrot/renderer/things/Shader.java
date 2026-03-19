package mandlbrot.renderer.things;

import static mandlbrot.renderer.util.LUTILVB.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2fv;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniform4fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix2fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Shader {
    int ID;

    public Shader(String vert, String frag){
        ID = loadShader(vert, frag);
    }

    public void use(){
        glUseProgram(ID);
    }

    public void setBool(String name, boolean value)
    {         
        glUniform1i(glGetUniformLocation(ID, name), (value ? 1 : 0)); 
    }
    public void setInt(String name, int value)
    { 
        glUniform1i(glGetUniformLocation(ID, name), value); 
    }
    public void setFloat(String name, float value)
    { 
        glUniform1f(glGetUniformLocation(ID, name), value); 
    } 
    public int getID() {
        return ID;
    }

    public void setUniform(String name, float x, float y) {
        glUniform2fv(glGetUniformLocation(ID, name), new float[] {x, y});
    }

    public void setUniform(String name, Vector2f value) {
        glUniform2fv(glGetUniformLocation(ID, name), new float[] {value.x, value.y});
    }

    public void setUniform(String name, float x, float y, float z) {
        glUniform3fv(glGetUniformLocation(ID, name), new float[] {x, y, z});
    }

    public void setUniform(String name, Vector3f value) {
        glUniform3fv(glGetUniformLocation(ID, name), new float[] {value.x, value.y, value.z});
    }

    public void setUniform(String name, float x, float y, float z, float w) {
        glUniform4fv(glGetUniformLocation(ID, name), new float[] {x, y, z, w});
    }

    public void setUniform(String name, Vector4f value) {
        glUniform4fv(glGetUniformLocation(ID, name), new float[] {value.x, value.y, value.z, value.w});
    }

    public void setUniform(String name, Matrix2f value) {
        glUniformMatrix2fv(glGetUniformLocation(ID, name), false, value.get(new float[4]));
    }

    public void setUniform(String name, Matrix3f value) {
        glUniformMatrix3fv(glGetUniformLocation(ID, name), false, value.get(new float[9]));
    }

    public void setUniform(String name, Matrix4f value) {
        glUniformMatrix4fv(glGetUniformLocation(ID, name), false, value.get(new float[16]));
    }
}
