package me.jraynor.bootstrap;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.uison.misc.Input;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.nvgDelete;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    @Getter
    private int width, height;
    private boolean bordered = false, resizeable, vSync;
    @Getter
    private String title;
    private long window;
    @Getter
    private long vg;
    @Getter
    @Setter
    private boolean resized = false;
    @Getter
    private boolean fullscreen;
    @Setter
    private ByteBuffer lightFont, boldFont, semiBold, extraBold, regularFont, italic;
    public static Window INSTANCE;

    public Window(int width, int height, boolean fullscreen, boolean bordered, boolean resizeable, boolean vSync, String title) {
        INSTANCE = this;
//        try {
//            this.lightFont = ioResourceToByteBuffer("src/main/resources/fonts/light.ttf", 512 * 1024);
//            this.semiBold = ioResourceToByteBuffer("src/main/resources/fonts/semibold.ttf", 512 * 1024);
//            this.boldFont = ioResourceToByteBuffer("src/main/resources/fonts/bold.ttf", 512 * 1024);
//            this.extraBold = ioResourceToByteBuffer("src/main/resources/fonts/extrabold.ttf", 512 * 1024);
//            this.italic = ioResourceToByteBuffer("src/main/resources/fonts/italic.ttf", 512 * 1024);
//            this.regularFont = ioResourceToByteBuffer("src/main/resources/fonts/regular.ttf", 512 * 1024);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.fullscreen = fullscreen;
        this.width = width;
        this.height = height;
        this.bordered = bordered;
        this.resizeable = resizeable;
        this.vSync = vSync;
        this.title = title;
    }

    public void setPosition(int x, int y) {
        glfwSetWindowPos(window, x, y);
    }

    @Deprecated
    private ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1) {
                    ;
                }
            }
        } else {
            try (
                    InputStream source = Window.class.getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }

        buffer.flip();
        return buffer;
    }

    /**
     * Called when the window is resized
     *
     * @param buffer
     * @param newCapacity
     * @return
     */
    private ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }


    /**
     * Start the window
     */
    public void start(IEngine loopable) {
        loopable.preInit();
        init();
        defaultHints();
        createWindow();
        pushWindow();
        finishWindow();
        createNvg();
        handleResize();
        loopable.postInit();
        loopCallback(loopable);
        stop();
    }

    /**
     * Creates the opengl window context
     */
    private void init() {
        Logger.info("Starting window with of width {} and height {}", width, height);
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            Logger.error("Failed to initialize window, shutting down");
            try {
                Thread.sleep(1000);
                System.exit(-1);
            } catch (InterruptedException e) {
            }
        }
    }


    /**
     * the default hints to be ran
     */
    private void defaultHints() {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, resizeable ? GLFW_TRUE : GLFW_FALSE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        if (bordered) {
            Logger.info("Attempting to create a windowed full screen window");
            glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
        }

        if (fullscreen) {
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        }
    }


    /**
     * Create the window
     */
    private void createWindow() {
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            Logger.error("Failed to create window, shutting down");
            try {
                Thread.sleep(1000);
                System.exit(-2);
            } catch (InterruptedException e) {
            }
        }

        Input.init(window);
    }

    private void createNvg() {
        boolean modernOpenGL = (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) > 3) || (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) == 3 && GL11.glGetInteger(GL30.GL_MINOR_VERSION) >= 2);
        vg = 0;

        if (modernOpenGL) {
            int flags = NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS;
            vg = NanoVGGL3.nvgCreate(flags);
            System.out.println("here not");
        } else {
            int flags = NanoVGGL2.NVG_STENCIL_STROKES | NanoVGGL2.NVG_ANTIALIAS;
            vg = NanoVGGL2.nvgCreate(flags);
            System.out.println("here");
        }
        nvgCreateFontMem(vg, "regular", regularFont, 0);
        nvgCreateFontMem(vg, "light", lightFont, 0);
        nvgCreateFontMem(vg, "semi-bold", semiBold, 0);
        nvgCreateFontMem(vg, "bold", boldFont, 0);
        nvgCreateFontMem(vg, "extra-bold", extraBold, 0);
        nvgCreateFontMem(vg, "italic", italic, 0);
    }

    /**
     * Get the thread stack and push a new frame, centers the window as well
     */
    private void pushWindow() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
            Logger.info("Window pushed to stack frame and centered on main monitor");
        }
    }

    private void handleResize() {
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.resized = true;
            glViewport(0, 0, width, height);
        });
    }

    /**
     * The final step, sets current context and disables/enables vsync, then shows the window
     */
    private void finishWindow() {
        glfwMakeContextCurrent(window);
        glfwSwapInterval(vSync ? 1 : 0);
        glfwShowWindow(window);
        GL.createCapabilities();
    }

    private static final long SECOND = 1000000000L;

    private double getTime() {
        return (double) System.nanoTime() / (double) SECOND;
    }

    /**
     * Handles the update and render method
     *
     * @param engine the class to be updated
     */
    private void loopCallback(@NotNull IEngine engine) {
        long passed = System.currentTimeMillis();
        long lastTime = System.nanoTime();
        final double ticks = engine.getTick();
        double ns = 1000000000 / ticks;
        double delta = 0;
        while (!glfwWindowShouldClose(window)) {


            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            long current = System.currentTimeMillis();
            float d = current - passed;
            passed = current;
            engine.update(d);
            while (delta >= 1) {
                engine.tick((float) delta);
                delta--;
            }


            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
            engine.render(d);
            nvgBeginFrame(vg, width, height, 1);
            nvgSave(vg);
            engine.renderUI(d);
            nvgRestore(vg);
            nvgEndFrame(vg);
            Input.update();


            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    /**
     * Shutdown the window gracefully
     */
    public void stop() {
        nvgDelete(vg);
        Logger.info("Stopping window gracefully");
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
        Logger.info("Window closed gracefully");
        System.exit(0);
    }

}
