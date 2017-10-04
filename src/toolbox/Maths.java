package toolbox;


import entities.Camera;
import joml.Math;
import joml.Matrix4f;
import joml.Vector2f;
import joml.Vector3f;

public class Maths {

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos){
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z))/det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z))/det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale){
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.identity();
        matrix4f.translate(translation, matrix4f);
        matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix4f);
        matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix4f);
        matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix4f);
        matrix4f.scale(new Vector3f(scale, scale, scale), matrix4f);
        return matrix4f;
    }

    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale){
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.identity();
        matrix4f.translate(translation.x, translation.y,0);
        matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix4f);
        return matrix4f;
    }

    public static Matrix4f createViewMatrix(Camera camera){
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1,0,0));
        viewMatrix.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0,1,0));
        viewMatrix.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0,0,1));
        Vector3f negVec = new Vector3f(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
        viewMatrix.translate(negVec);
        return viewMatrix;
    }


}
