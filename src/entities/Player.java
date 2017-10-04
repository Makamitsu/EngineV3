package entities;


import joml.Vector3f;
import models.TextureModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class Player extends Entity{

    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -80;
    private static final float JUMP_POWER = 30;

    private static final float TERRAIN_HEIGHT = 6;

    private boolean isInAir = false;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    public Player(TextureModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(Terrain terrain){
        checkInputs();
        super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
        float dx = (float) (distance*Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance*Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx,0, dz);
        upwardsSpeed += GRAVITY*DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0,upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float terrainHeight= terrain.getHeightOfTerrain(super.getPosition().x,super.getPosition().z);
        if(super.getPosition().y < terrainHeight){
            upwardsSpeed = 0;
            increasePosition(0,terrainHeight-getPosition().y > 0.1f?0.1f:terrainHeight-getPosition().y,0);
            //super.getPosition().y = terrainHeight;
            isInAir = false;
        }
    }

    private void jump(){
        if(!isInAir){
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    private void checkInputs(){
        if(GLFW_PRESS == glfwGetKey(DisplayManager.getWindow(),GLFW_KEY_W)){
            this.currentSpeed = currentSpeed==0?RUN_SPEED:currentSpeed>=40?currentSpeed:currentSpeed+0.1f;
        }else if(GLFW_PRESS == glfwGetKey(DisplayManager.getWindow(),GLFW_KEY_S)){
            this.currentSpeed = -RUN_SPEED;
        }else {
            this.currentSpeed = 0;
        }

        if(GLFW_PRESS == glfwGetKey(DisplayManager.getWindow(),GLFW_KEY_D)){
            this.currentTurnSpeed = -TURN_SPEED;
        }else if(GLFW_PRESS == glfwGetKey(DisplayManager.getWindow(),GLFW_KEY_A)){
            this.currentTurnSpeed = TURN_SPEED;
        }else {
            this.currentTurnSpeed = 0;
        }

        if(GLFW_PRESS == glfwGetKey(DisplayManager.getWindow(),GLFW_KEY_SPACE)){
            jump();
        }
    }
}
