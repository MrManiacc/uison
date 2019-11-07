package me.jraynor.pong.audio;

import me.jraynor.pong.entities.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class AudioMaster {
    private long device;

    private long context;

    private AudioObserver listener;

    private final List<AudioBuffer> soundBufferList;

    private final Map<String, AudioSource> soundSourceMap;

    private final Matrix4f cameraMatrix;

    public AudioMaster() {
        soundBufferList = new ArrayList<>();
        soundSourceMap = new HashMap<>();
        cameraMatrix = new Matrix4f();
    }

    public void init() throws Exception {
        this.device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        this.context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }

    public void addSoundSource(String name, AudioSource soundSource) {
        this.soundSourceMap.put(name, soundSource);
    }

    public AudioSource getSoundSource(String name) {
        return this.soundSourceMap.get(name);
    }

    public void playSoundSource(String name) {
        AudioSource soundSource = this.soundSourceMap.get(name);
        if (soundSource != null && !soundSource.isPlaying()) {
            soundSource.play();
        }
    }

    public void removeSoundSource(String name) {
        this.soundSourceMap.remove(name);
    }

    public void addSoundBuffer(AudioBuffer soundBuffer) {
        this.soundBufferList.add(soundBuffer);
    }

    public AudioObserver getListener() {
        return this.listener;
    }

    public void setListener(AudioObserver listener) {
        this.listener = listener;
    }

    public void updateListenerPosition(Player player) {
        // Update camera matrix with camera data
        player.getCamera().getViewMatrix().get(cameraMatrix);
        listener.setPosition(player.getLocation());
        Vector3f at = new Vector3f();
        cameraMatrix.positiveZ(at).negate();
        Vector3f up = new Vector3f();
        cameraMatrix.positiveY(up);
        listener.setOrientation(at, up);
    }

    public void setAttenuationModel(int model) {
        alDistanceModel(model);
    }

    public void cleanup() {
        for (AudioSource soundSource : soundSourceMap.values()) {
            soundSource.cleanup();
        }
        soundSourceMap.clear();
        for (AudioBuffer soundBuffer : soundBufferList) {
            soundBuffer.cleanup();
        }
        soundBufferList.clear();
        if (context != NULL) {
            alcDestroyContext(context);
        }
        if (device != NULL) {
            alcCloseDevice(device);
        }
    }

}
