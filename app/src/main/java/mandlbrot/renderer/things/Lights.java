package mandlbrot.renderer.things;

import java.util.ArrayList;

import org.joml.Vector3f;

public class Lights {
    private Lights(){}

    static final int MAX_LIGHTS = 128;

    static int dirLightIndex = 0;
    static int pointLightIndex = 0;
    static int spotLightIndex = 0;

    static ArrayList<Light> lights = new ArrayList<>();

    static int dirLightCount = 0;
    static int pointLightCount = 0;
    static int spotLightCount = 0;

    public static void setLights(Shader shader){
        shader.setInt("DIR_LIGHT_COUNT", dirLightCount);
        shader.setInt("POINT_LIGHT_COUNT", pointLightCount);
        shader.setInt("SPOT_LIGHT_COUNT", spotLightCount);
        resetIndices();
        for (Light light : lights) {
            light.setShaderValues(shader);
        }
    }

    public static void resetIndices(){
        dirLightIndex = 0;
        pointLightIndex = 0;
        spotLightIndex = 0;
    }

    public static class Light{
        public Light(){
            Lights.lights.add(this);
        }
        public void setShaderValues(Shader shader, int index) {}
        public void setShaderValues(Shader shader){}
        public void setPosition(Vector3f position) {}
        public void setDirection(Vector3f direction) {}
        public void remove() {}
    }

    public static Light createDirLight(Vector3f direction, Vector3f ambient, Vector3f diffuse, Vector3f specular){
        return new DirLight(direction, ambient, diffuse, specular);
    }

    public static Light createPointLight(Vector3f position, Vector3f diffuse, Vector3f specular, float constant, float linear, float quadratic){
        return new PointLight(position, constant, linear, quadratic, diffuse, specular);
    }

    public static Light createSpotLight(Vector3f position, Vector3f direction, Vector3f diffuse, 
    Vector3f specular, float constant, float linear, float quadratic, float cutOff, float outerCutOff){
        return new SpotLight(position, direction, cutOff, outerCutOff, constant, linear, quadratic, diffuse, specular);
    }

    private static class DirLight extends Light{
        Vector3f direction;
	
        Vector3f ambient;
        Vector3f diffuse;
        Vector3f specular;
        public DirLight(Vector3f direction, Vector3f ambient, Vector3f diffuse, Vector3f specular){
            dirLightCount++;
            this.direction = direction;
            this.ambient = ambient;
            this.diffuse = diffuse;
            this.specular = specular;
            if(dirLightCount>MAX_LIGHTS){
                System.err.println("TOO MANY DIRLIGHTS: " + dirLightCount + " " + MAX_LIGHTS + ". REMOVING");
                this.remove();
            }
        }

        @Override
        public void setShaderValues(Shader shader, int index){
            shader.setUniform("dirLights[" + index +"].direction", direction);
			shader.setUniform("dirLights[" + index +"].ambient", ambient);
			shader.setUniform("dirLights[" + index +"].diffuse", diffuse);
			shader.setUniform("dirLights[" + index +"].specular", specular);
        }

        @Override
        public void setDirection(Vector3f direction) {
            this.direction = direction;
        }

        @Override
        public void setShaderValues(Shader shader){
            setShaderValues(shader, dirLightIndex);
            dirLightIndex++;
        }

        @Override
        public void remove(){
            dirLightCount--;
            lights.remove(this);
        }
    }

    private static class PointLight extends Light{
        Vector3f position;
    
        float constant;
        float linear;
        float quadratic;

        Vector3f diffuse;
        Vector3f specular;
        public PointLight(Vector3f position, float constant, float linear, float quadratic, Vector3f diffuse, Vector3f specular){
            pointLightCount++;
            this.position = position;
            this.constant = constant;
            this.linear = linear;
            this.quadratic = quadratic;
            this.diffuse = diffuse;
            this.specular = specular;
            if(pointLightCount>MAX_LIGHTS){
                System.err.println("TOO MANY POINTLIGHTS: " + pointLightCount + " " + MAX_LIGHTS + ". REMOVING");
                this.remove();
            }
        }

        @Override
        public void setPosition(Vector3f position) {
            this.position = position;
        }

        @Override
        public void setShaderValues(Shader shader, int index){
            shader.setUniform("pointLights["+ index +"].position", position);
            shader.setUniform("pointLights["+ index +"].diffuse", diffuse);
            shader.setUniform("pointLights["+ index +"].specular", specular);
            shader.setFloat("pointLights["+ index +"].constant", constant);
            shader.setFloat("pointLights["+ index +"].linear", linear);
            shader.setFloat("pointLights["+ index +"].quadratic", quadratic);
        }

        @Override
        public void setShaderValues(Shader shader){
            setShaderValues(shader, pointLightIndex);
            pointLightIndex++;
        }

        @Override
        public void remove(){
            pointLightCount--;
            lights.remove(this);
        }
    }

    private static class SpotLight extends Light{
        Vector3f position;
        Vector3f direction;
        float cutOff;
        float outerCutOff;
      
        float constant;
        float linear;
        float quadratic;
    
        Vector3f diffuse;
        Vector3f specular;
        public SpotLight(Vector3f position, Vector3f direction, float cutOff, float outerCutOff, float constant, 
                        float linear, float quadratic, Vector3f diffuse, Vector3f specular){
            spotLightCount++;
            this.position = position;
            this.direction = direction;
            this.cutOff = cutOff;
            this.outerCutOff = outerCutOff;
            this.constant = constant;
            this.linear = linear;
            this.quadratic = quadratic;
            this.diffuse = diffuse;
            this.specular = specular;
            if(spotLightCount>MAX_LIGHTS){
                System.err.println("TOO MANY SPOTLIGHTS: " + spotLightCount + " " + MAX_LIGHTS + ". REMOVING");
                this.remove();
            }
        }

        @Override
        public void setDirection(Vector3f direction) {
            this.direction = direction;
        }

        @Override
        public void setPosition(Vector3f position) {
            this.position = position;
        }

        @Override
        public void setShaderValues(Shader shader, int index){
            shader.setUniform("spotLights[" + index +"].position", position);
			shader.setUniform("spotLights[" + index +"].direction", direction);
			shader.setUniform("spotLights[" + index +"].diffuse", diffuse);
			shader.setUniform("spotLights[" + index +"].specular", specular);
			shader.setFloat("spotLights[" + index +"].constant", constant);
			shader.setFloat("spotLights[" + index +"].linear", linear);
			shader.setFloat("spotLights[" + index +"].quadratic", quadratic);
			shader.setFloat("spotLights[" + index +"].cutOff", cutOff);
			shader.setFloat("spotLights[" + index +"].outerCutOff", outerCutOff);  
        }

        @Override
        public void setShaderValues(Shader shader){
            setShaderValues(shader, spotLightIndex);
            spotLightIndex++;
        }

        @Override
        public void remove(){
            spotLightCount--;
            lights.remove(this);
        }
    }
}
