package me.jraynor.uison.parser;

import lombok.Getter;
import me.jraynor.uison.UIMaster;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class UIWatcher implements Runnable {
    @Getter
    private boolean started = false;
    private Path filePath;
    private WatchService watchService;

    public UIWatcher(String path) {
        this.filePath = Paths.get(path);
    }

    @Override
    public void run() {
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            filePath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        WatchKey key;
        try {
            while ((key = watchService.take()) != null) {
                Path path = null;
                for (WatchEvent<?> event : key.pollEvents())
                    path = (Path) event.context();
                if (path != null) {
                    if (!path.toFile().getName().contains("___jb_tmp___") && !path.toFile().getName().contains("___jb_old___")) {
                        String name = path.toFile().getName();
                        String location = filePath.toFile().getAbsolutePath();
                        String absPath = location + File.separator + name;
                        UIMaster.reloadFile(absPath);
                    }
                }

                key.reset();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
