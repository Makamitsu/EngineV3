package entities;


import joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import renderEngine.DisplayManager;

public class Camera {

    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    private Vector3f position = new Vector3f(0,0,0);
    private float pitch;
    private float yaw;
    private float roll;
    private float targetHeight = 8;

    private Player player;

    public Camera(Vector3f position, float pitch, float yaw, float roll, Player player) {
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.player = player;
    }

    public Camera(Vector3f position, float pitch, float yaw, float roll) {
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }


    public void move(){
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }

    private void calculateCameraPosition(float hDistance, float vDistance){
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (hDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (hDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.y = player.getPosition().y + targetHeight + vDistance;
        position.z = player.getPosition().z - offsetZ;
    }

    private float calculateHorizontalDistance(){
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance(){
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom(){
        distanceFromPlayer = 50 + DisplayManager.SCROLL;
    }

    private void calculatePitch(){
        if (glfwGetMouseButton(DisplayManager.getWindow(), GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            float pitchChange = (float) (DisplayManager.dY*0.1f);
            pitch += pitchChange;
        }
    }

    private void calculateAngleAroundPlayer(){
        if (glfwGetMouseButton(DisplayManager.getWindow(), GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            float angleChange  = (float) (DisplayManager.dX * 0.3f);
            angleAroundPlayer -= angleChange;
        }
    }



    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }



}
