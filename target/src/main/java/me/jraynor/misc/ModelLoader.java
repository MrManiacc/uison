package me.jraynor.misc;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.aiGetErrorString;

public class ModelLoader {
    private static final String models = "src/main/resources/models/";
    private static final String texturesPath = "src/main/resources/textures/";

    /**
     * This method uses assimp to load a model from file
     */
    public static List<Vao> loadModel(String modelName) {

        AIScene modelScene = Assimp.aiImportFile(models + modelName,
                Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_GenSmoothNormals |
                        Assimp.aiProcess_FlipUVs |
                        Assimp.aiProcess_CalcTangentSpace |
                        Assimp.aiProcess_JoinIdenticalVertices |
                        Assimp.aiProcess_LimitBoneWeights
        );


        if (modelScene == null) {
            System.err.println(aiGetErrorString());
            System.exit(0);
        }
        int numMeshes = modelScene.mNumMeshes();
        PointerBuffer aiMeshes = modelScene.mMeshes();

        AIMesh[] meshes = new AIMesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            meshes[i] = AIMesh.create(aiMeshes.get(i));
        }

        int numMaterials = modelScene.mNumMaterials();
        PointerBuffer mats = modelScene.mMaterials();
        AIMaterial[] materials = new AIMaterial[numMaterials];

        ArrayList<AITexture> texList = new ArrayList<>();
        PointerBuffer textures = modelScene.mTextures();
        if (textures != null) {
            for (int i = 0; i < textures.capacity(); i++) {
                texList.add(AITexture.create(textures.get(i)));
            }
        }
        List<Vao> meshList = new ArrayList<>();

        for (int i = 0; i < meshes.length; i++) {
            Vao vao = initMesh(meshes[i]);
            meshList.add(vao);
        }

        return meshList;
    }


    /**
     * This method is a helper to parse out the model from the assimp model
     *
     * @param mesh assimp's model before being parsed
     * @return
     */
    private static Vao initMesh(AIMesh mesh) {
//        createBinaryModel(mesh);
        ByteBuffer vertexArrayBufferData = BufferUtils.createByteBuffer(mesh.mNumVertices() * 3 * Float.BYTES);
        AIVector3D.Buffer vertices = mesh.mVertices();


        ByteBuffer normalArrayBufferData = BufferUtils.createByteBuffer(mesh.mNumVertices() * 3 * Float.BYTES);
        AIVector3D.Buffer normals = mesh.mNormals();


        ByteBuffer tangentsArrayBufferData = BufferUtils.createByteBuffer(mesh.mNumVertices() * 3 * Float.BYTES);
        AIVector3D.Buffer tangents = mesh.mTangents();

        ByteBuffer biTangentsArrayBufferData = BufferUtils.createByteBuffer(mesh.mNumVertices() * 3 * Float.BYTES);
        AIVector3D.Buffer biTangents = mesh.mBitangents();

        ByteBuffer texArrayBufferData = BufferUtils.createByteBuffer(mesh.mNumVertices() * 2 * Float.BYTES);
        Vector3f min = new Vector3f(Float.MAX_VALUE);
        Vector3f max = new Vector3f(Float.MIN_VALUE);
        for (int i = 0; i < mesh.mNumVertices(); ++i) {
            AIVector3D vert = vertices.get(i);
            vertexArrayBufferData.putFloat(vert.x());
            vertexArrayBufferData.putFloat(vert.y());
            vertexArrayBufferData.putFloat(vert.z());

            if (vert.x() < min.x)
                min.x = vert.x();
            if (vert.y() < min.y)
                min.y = vert.y();
            if (vert.z() < min.z)
                min.z = vert.z();


            if (vert.x() > max.x)
                max.x = vert.x();
            if (vert.y() > max.y)
                max.y = vert.y();
            if (vert.z() > max.z)
                max.z = vert.z();


            AIVector3D norm = normals.get(i);
            normalArrayBufferData.putFloat(norm.x());
            normalArrayBufferData.putFloat(norm.y());
            normalArrayBufferData.putFloat(norm.z());

            if (tangents != null) {
                AIVector3D tang = tangents.get(i);
                tangentsArrayBufferData.putFloat(tang.x());
                tangentsArrayBufferData.putFloat(tang.y());
                tangentsArrayBufferData.putFloat(tang.z());

                AIVector3D biTang = biTangents.get(i);
                biTangentsArrayBufferData.putFloat(biTang.x());
                biTangentsArrayBufferData.putFloat(biTang.y());
                biTangentsArrayBufferData.putFloat(biTang.z());
            }


            if (mesh.mNumUVComponents().get(0) != 0) {
                AIVector3D texture = mesh.mTextureCoords(0).get(i);
                texArrayBufferData.putFloat(texture.x()).putFloat(texture.y());
            } else {
                texArrayBufferData.putFloat(0).putFloat(0);
            }
        }
        Vector3f origin = new Vector3f(max.x - min.x, max.y - min.y, max.z - min.z);

        vertexArrayBufferData.flip();
        normalArrayBufferData.flip();
        tangentsArrayBufferData.flip();
        biTangentsArrayBufferData.flip();
        texArrayBufferData.flip();


        int faceCount = mesh.mNumFaces();
        int elementCount = faceCount * 3;
        IntBuffer elementArrayBufferData = BufferUtils.createIntBuffer(elementCount);
        AIFace.Buffer facesBuffer = mesh.mFaces();
        for (int i = 0; i < faceCount; ++i) {
            AIFace face = facesBuffer.get(i);
            if (face.mNumIndices() != 3) {
                throw new IllegalStateException("AIFace.mNumIndices() != 3");
            }
            elementArrayBufferData.put(face.mIndices());
        }
        elementArrayBufferData.flip();
        Vao vao = Vao.create();
        vao.setMin(min);
        vao.setMax(max);
        vao.setOrigin(origin);
        vao.setIndexCount(elementCount);
        vao.bind(0, 1, 2);
        vao.createAttribute(0, vertexArrayBufferData.asFloatBuffer(), 3);
        vao.createAttribute(1, normalArrayBufferData.asFloatBuffer(), 3);
        vao.createAttribute(2, texArrayBufferData.asFloatBuffer(), 2);
//        vao.createAttribute(3, tangentsArrayBufferData.asFloatBuffer(), 3);
//        vao.createAttribute(4, biTangentsArrayBufferData.asFloatBuffer(), 3);
        vao.createIndexBuffer(elementArrayBufferData);
        vao.unbind(0, 1, 2);
        return vao;
    }

}
