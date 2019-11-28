//package me.jraynor;
//
//import me.jraynor.bootstrap.IEngine;
//import me.jraynor.bootstrap.Window;
//import me.jraynor.uison.UIMaster;
//import me.jraynor.uison.controller.DefaultController;
//import me.jraynor.uison.controller.WindowController;
//import me.jraynor.uison.misc.Input;
//
//import java.util.Optional;
//
//public class TestRunner extends IEngine {
//    private final Window window;
//
//    private TestRunner(int width, int height, String title) {
//        super(60D);
//        window = new Window(width, height, true, false, true, title);
//        window.start(this);
//    }
//
//    public void postInit() {
//        UIMaster.createUIMaster(window, new WindowController(window));
//    }
//
//    public void renderUI(double v) {
//        UIMaster.update(window);
//    }
//
//    public void update(double v) {
//        Input.globalMouse();
//    }
//
//    public static void main(String[] args) {
//        switch (args.length) {
//            case 1:
//                new TestRunner(Integer.parseInt(args[0]), Integer.parseInt(args[0]), "Fortnite Buddy");
//                break;
//            case 2:
//                new TestRunner(Integer.parseInt(args[0]), Integer.parseInt(args[1]), "Fortnite Buddy");
//                break;
//            default:
//                new TestRunner(1080, 720, "Fortnite Buddy");
//        }
//    }
//}
