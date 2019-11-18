package me.jraynor.uison.parser.parser;

import me.jraynor.uison.UIMaster;
import me.jraynor.uison.parser.MappedUIComponent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

public abstract class Parser {
    private String directory;
    protected HashMap<String, MappedUIComponent> rootComponents = new HashMap<>();

    public Parser(String directory) {
        this.directory = directory;
    }

    public void parse() {
        try {
            Files.walk(Paths.get(directory))
                    .filter(this::isValidDirectory)
                    .forEach(p -> Arrays.asList(Objects.requireNonNull(new File(p.toUri()).listFiles())).forEach(handleResources));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String id : rootComponents.keySet()) {
            if (UIMaster.hasComponent(id))
                UIMaster.getRoot().add(rootComponents.get(id).getComponent());
        }
    }

    protected String removeComments(List<String> jsonText) {
        StringBuilder sb = new StringBuilder();
        boolean inComment = false;
        for (String line : jsonText) {
            String l = line.trim();
            if (l.contains("@"))
                line = l.replace("@", "~");
            if (l.startsWith("##")) {
                inComment = !inComment;
                continue;
            }
            if (!inComment)
                sb.append(line);
        }
        return sb.toString();
    }

    public void reloadFile(String filePath) {
        parseFile(new File(filePath));
        for (String id : rootComponents.keySet()) {
            if (UIMaster.hasComponent(id)) {
                UIMaster.getRoot().add(rootComponents.get(id).getComponent());
            }
        }
        UIMaster.addEvents();
    }


    private Consumer<File> handleResources = file -> {
        if (!file.isFile()) return;
        Optional<String> extension = getExtension(file.getName());
        if (extension.isEmpty()) return;
        if (extension.get().equalsIgnoreCase("ui")) {
            Optional<String> name = removeExtension(file.getName());
            if (name.isPresent()) {
                parseFile(file);
            }
        }
    };

    abstract void parseFile(File file);


    protected Optional<String> removeExtra(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(0, filename.lastIndexOf(".")));
    }

    protected Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    protected Optional<String> removeExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(0, filename.lastIndexOf(".")));
    }

    /**
     * Simply checks the path to make sure it's not the "info" directory
     *
     * @param p
     * @return
     */
    private boolean isValidDirectory(Path p) {
        return Files.isDirectory(p);
    }

}
