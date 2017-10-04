package renderEngine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

public class DisplayManager{

    private static long window;

    public static double dX;
    public static double dY;

    private static double lastY = 0;
    private static double lastX = 0;

    public final int WIDTH = 1280;
    public final int HEIGHT = 720;

    public static int SCROLL = 0;

    private final int FPS_CAP= 120;
    private final String TITLE = "GLEngine_V2";

    private static long lastFrameTime;
    private static float delta;

    public void createDisplay(){
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);

        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, posX, posY);
        lastX = posX.get(0);
        lastY = posY.get(0);

        glfwSetScrollCallback(window, GLFWScrollCallback.create((window, xoffset, yoffset) -> {
            SCROLL+=yoffset*3.0;
        }));

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        glfwShowWindow(window);
        GL.createCapabilities();
        lastFrameTime = getCurrrentTime();
    }

    public void updateDisplay(){
        updateMouse();
        glfwPollEvents();
        long currentFrameTime = getCurrrentTime();
        delta = (currentFrameTime - lastFrameTime)/1000f;
        lastFrameTime = currentFrameTime;
    }

    public static float getFrameTimeSeconds(){
        return delta;
    }

    public void closeDisplay(){
        glfwSetWindowShouldClose(window, true);
        glfwSetErrorCallback(null).free();
        glfwTerminate();
    }

    private static void updateMouse() {
        double tempX = lastX;
        double tempY = lastY;
        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, posX, posY);
        lastX = posX.get(0);
        lastY = posY.get(0);
        dX = posX.get(0)-tempX;
        dY = posY.get(0)-tempY;
    }

    private static long getCurrrentTime(){
        return System.nanoTime()/1000000L;
    }

    public static long getWindow() {
        return window;
    }
}
