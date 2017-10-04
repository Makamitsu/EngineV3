package renderEngine;


import com.sun.org.apache.regexp.internal.RE;
import entities.Camera;
import entities.Entity;
import entities.Light;
import joml.Matrix4f;
import models.TextureModel;
import org.lwjgl.opengl.GL11;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.glClearColor;

public class MasterRenderer {

    private final float FOV = 70;
    private final float NEAR_PLANE = 0.1f;
    private final float FAR_PLANE = 1000;

    private final static float RED = 0.55f;
    private final static float GREEN = 0.62f;
    private final static float BLUE = 0.69f;

    private final DisplayManager displayManager;

    private Matrix4f projectionMatrix;

    private StaticShader shader;
    private EntityRenderer entityRenderer;

    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader;

    private Map<TextureModel,List<Entity>> entities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    private SkyboxRenderer skyboxRenderer;

    public MasterRenderer(DisplayManager displayManager, Loader loader) {
        this.displayManager = displayManager;
        shader = new StaticShader();
        terrainShader = new TerrainShader();
        enableCulling();
        creationProjectionMatrix();
        entityRenderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
    }

    public static void enableCulling(){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling(){
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void render(List<Light> lights, Camera camera){
        prepare();
        shader.start();
        shader.loadSkyColour(RED, GREEN, BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        shader.stop();
        terrainShader.start();
        terrainShader.loadSkyColour(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        skyboxRenderer.render(camera, RED, GREEN, BLUE);
        terrains.clear();
        entities.clear();
    }

    public void processTerrain(Terrain terrain){
        terrains.add(terrain);
    }

    public void processEntity(Entity entity){
        TextureModel entityModel = entity.getModel();
        List<Entity> batch  = entities.get(entityModel);
        if(batch != null){
            batch.add(entity);
        }else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void cleanUp(){
        shader.cleanUp();
        terrainShader.cleanUp();
    }

    public void prepare(){
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        glClearColor(RED, GREEN, BLUE, 1.0f);
    }

    private void creationProjectionMatrix(){
        float aspectRatio = (float) displayManager.WIDTH / (float) displayManager.HEIGHT;
        float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) *aspectRatio);
        float xScale =  yScale/aspectRatio;
        float frustrumLength = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f(
                xScale,0,0,0,
                0,yScale,0,0,
                0,0,-((FAR_PLANE+NEAR_PLANE)/frustrumLength),-1,
                0,0,-((2* NEAR_PLANE * FAR_PLANE) / frustrumLength),0
        );
    }

}
