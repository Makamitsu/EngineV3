package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import joml.Vector2f;
import joml.Vector3f;
import models.TextureModel;
import renderEngine.*;
import models.RawModel;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class MainGameLoop {

    public static void main(String args[]){
        DisplayManager displayManager = new DisplayManager();
        displayManager.createDisplay();
        Loader loader = new Loader();

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("floor/grass.png"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("floor/oldCobbles.png"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("floor/mossyStone.png"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("floor/royalTissue.png"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("floor/pj_map1.png"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        Terrain terrain0 = new Terrain(0,-1,loader,texturePack, blendMap, "hm.png");

        RawModel rawChoco = OBJLoader.loadObjModel("choco3d.obj",loader);
        ModelTexture chocoTex = new ModelTexture(loader.loadTexture("chocoTex.png"));
        TextureModel chocoTextured = new TextureModel(rawChoco, chocoTex);
        chocoTex.setHasTransparency(true);
        chocoTex.setReflectivity(0.25f);
        chocoTex.setShineDamper(35);
        Player choco = new Player(chocoTextured, new Vector3f(50,1,-50),
                0,180,180,0.05f);

        RawModel tuffetRawModel = OBJLoader.loadObjModel("kusa.obj", loader);
        ModelTexture tuffetTexture = new ModelTexture(loader.loadTexture("fern.png"));
        tuffetTexture.setNumberOfRows(2);
        tuffetTexture.setHasTransparency(true);
        tuffetTexture.setUseFakeLighting(true);
        TextureModel tuffetTextureModel = new TextureModel(tuffetRawModel, tuffetTexture);
        ArrayList<Entity> tuffets = new ArrayList<>();
        Vector3f pos;
        for(int i = 0; i < 200; i++){
            pos = new Vector3f();
            pos.x = (float) (Math.random()*800);
            pos.z = (float) -(Math.random()*800);
            pos.y = terrain0.getHeightOfTerrain(pos.x, pos.z);
            tuffets.add(new Entity(tuffetTextureModel, (int) (Math.random()*3),  pos,(float) Math.random(),0,0,2f));
        }

        RawModel treeRawModel = OBJLoader.loadObjModel("tree.obj", loader);
        ModelTexture treeTexture = new ModelTexture(loader.loadTexture("tree.png"));
        TextureModel treeTextureModel = new TextureModel(treeRawModel, treeTexture);
        ArrayList<Entity> trees = new ArrayList<>();
        for(int i = 0; i < 150; i++){
            pos = new Vector3f();
            pos.x = (float) (Math.random()*800);
            pos.z = (float) -(Math.random()*800);
            pos.y = terrain0.getHeightOfTerrain(pos.x, pos.z);
            trees.add(new Entity(treeTextureModel, pos,(float) Math.random(),0,0,10f));
        }


        RawModel treeLowRawModel = OBJLoader.loadObjModel("lowPolyTree.obj", loader);
        ModelTexture treeLowTexture = new ModelTexture(loader.loadTexture("lowPolyTree.png"));
        TextureModel treeLowTextureModel = new TextureModel(treeLowRawModel, treeLowTexture);
        ArrayList<Entity> treesLow = new ArrayList<>();
        for(int i = 0; i < 150; i++){
            pos = new Vector3f();
            pos.x = (float) (Math.random()*800);
            pos.z = (float) -(Math.random()*800);
            pos.y = terrain0.getHeightOfTerrain(pos.x, pos.z);
            treesLow.add(new Entity(treeLowTextureModel, pos,(float) Math.random(),0,0,0.8f));
        }

        List<GuiTexture> guis = new ArrayList<>();
        GuiTexture gui = new GuiTexture(
                loader.loadTexture("gui/chocobo.png"),
                new Vector2f(-0.85f, -0.78f),
                new Vector2f(0.18f, 0.25f));
        guis.add(gui);
        GuiTexture gui2 = new GuiTexture(
                loader.loadTexture("gui/feather.png"),
                new Vector2f(-0.7f, -0.78f),
                new Vector2f(0.07f, 0.1f));
        guis.add(gui2);
        GuiTexture gui3 = new GuiTexture(
                loader.loadTexture("gui/feather.png"),
                new Vector2f(-0.65f, -0.78f),
                new Vector2f(0.07f, 0.1f));
        guis.add(gui3);

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        Light sun = new Light(new Vector3f(500,2000,3800), new Vector3f(0.5f, 0.5f, 0.5f));
        Light light1 = new Light(
                new Vector3f(choco.getPosition().x,
                        choco.getPosition().y+20,
                        choco.getPosition().z),
                new Vector3f(5f,5f,5f),
                new Vector3f(1,0.02f,0.01f));
        Light light2 = new Light(new Vector3f(220,15,-220), new Vector3f(2,2,2), new Vector3f(1,0.01f,0.002f));
        List<Light> lights = new ArrayList<>();
        lights.add(sun);
        lights.add(light1);
        lights.add(light2);

        Camera camera = new Camera(new Vector3f(0,0,0), 0,0,0, choco);
        MasterRenderer renderer = new MasterRenderer(displayManager, loader);

        while ( !glfwWindowShouldClose(DisplayManager.getWindow()) ) {

            displayManager.updateDisplay();

            camera.move();
            choco.move(terrain0);

            renderer.processEntity(choco);

            light1.setPosition(new Vector3f(camera.getPosition().x,
                    camera.getPosition().y,
                    camera.getPosition().z));

            for(Entity e:tuffets){renderer.processEntity(e);}
            for(Entity e:trees){renderer.processEntity(e);}
            for(Entity e:treesLow){renderer.processEntity(e);}
            renderer.processTerrain(terrain0);
            renderer.render(lights, camera);

            guiRenderer.render(guis);

            glfwSwapBuffers(DisplayManager.getWindow());
            glfwPollEvents();
        }

        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        displayManager.closeDisplay();
    }

}
