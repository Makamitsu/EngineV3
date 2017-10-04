package shaders;


import entities.Camera;
import entities.Light;
import joml.Matrix4f;
import joml.Vector2f;
import joml.Vector3f;
import toolbox.Maths;

import java.util.List;

public class StaticShader extends ShaderProgram{

    private static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "src/shaders/vertexShader";
    private static final String FRAGMENT_FILE = "src/shaders/fragmentShader";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition[];
    private int location_lightColour[];
    private int location_attenuation[];
    private int location_shineDamping;
    private int location_reflectivity;
    private int location_useFakeLighting;
    private int location_skyColour;
    private int location_numberOfRows;
    private int location_offset;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_shineDamping = super.getUniformLocation("shineDamping");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        location_skyColour = super.getUniformLocation("skyColour");
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");

        location_lightPosition = new int[MAX_LIGHTS];
        location_lightColour = new int[MAX_LIGHTS];
        location_attenuation = new int[MAX_LIGHTS];
        for(int i = 0; i < MAX_LIGHTS; i++){
            location_lightPosition[i]  = super.getUniformLocation("lightPosition["+i+"]");
            location_lightColour[i]  = super.getUniformLocation("lightColour["+i+"]");
            location_attenuation[i]  = super.getUniformLocation("attenuation["+i+"]");
        }
    }

    public void loadNumberOfRows(float nb){
        super.loadFloat(location_numberOfRows, nb);
    }

    public void loadOffset(Vector2f vec2){
        super.loadVec2(location_offset, vec2);
    }

    public void loadSkyColour(float r, float g, float b){
        super.loadVec3(location_skyColour, new Vector3f(r, g, b));
    }

    public void loadFakeLightingVariable(boolean useFake){
        super.loadBoolean(location_useFakeLighting, useFake);
    }

    public void loadShineVariables(float damper, float reflectivity){
        super.loadFloat(location_shineDamping, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadLights(List<Light> lights){
        for(int i = 0 ; i < MAX_LIGHTS; i++){
            if(i < lights.size()){
                super.loadVec3(location_lightPosition[i], lights.get(i).getPosition());
                super.loadVec3(location_lightColour[i], lights.get(i).getColour());
                super.loadVec3(location_attenuation[i], lights.get(i).getAttenuation());
            }else {
                super.loadVec3(location_lightPosition[i], new Vector3f(0,0,0));
                super.loadVec3(location_lightColour[i], new Vector3f(0,0,0));
                super.loadVec3(location_attenuation[i], new Vector3f(1,0,0));
            }
        }
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }





}
