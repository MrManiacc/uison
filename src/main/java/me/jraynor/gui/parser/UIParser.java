package me.jraynor.gui.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.jraynor.gui.UIMaster;
import me.jraynor.gui.logic.UIComponent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

import static me.jraynor.gui.parser.UIType.UIUNKOWN;

public class UIParser {
    public void reloadUI() {
        try {
            Files.walk(Paths.get("src/main/resources/ui/"))
                    .filter(this::isValidDirectory)
                    .forEach(p -> Arrays.asList(Objects.requireNonNull(new File(p.toUri()).listFiles())).forEach(handleResources));
        } catch (IOException e) {
            e.printStackTrace();
        }

        UIMaster.getRoot().add(UIMaster.component("main_menu").get().getComponent());
        UIMaster.getRoot().add(UIMaster.component("main_menu2").get().getComponent());
        UIMaster.addEvents();
    }

    private UIComponent parseComponent(File file) {
        JsonParser jsonParser = new JsonParser();
        try {
            List jsonLines = FileUtils.readLines(file);
            String jsonText = removeComments(jsonLines);
            JsonObject root = jsonParser.parse(jsonText).getAsJsonObject();
            mapImports(root);
            mapComponents(root, null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String removeComments(List<String> jsonText) {
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
                sb.append(line + "\n");
        }
        return sb.toString();
    }


    /**
     * Recursively map all of the components
     *
     * @param element
     * @return the total number of mapped components
     */
    private MappedUIComponent mapComponents(JsonElement element, MappedUIComponent parent, MappedUIComponent root) {
        if (element == null && root != null) return root;
        OrderedComponent orderedComponent = getComponents(element);
        if (orderedComponent.map.isEmpty() && root != null) return root;
        for (int i = 0; i < orderedComponent.total; i++) {
            String key = orderedComponent.idMap.get(i);
            if (key.contains("#")) {
                //It's a component root
                String[] idData = key.split("#");
                UIType type = getType(idData[0]);
                String id = idData[1];

                JsonElement e = orderedComponent.map.get(key);
                MappedUIComponent mappedUIComponent = new MappedUIComponent(id, type, e, this, parent);
                mappedUIComponent.parse();
                if (root == null)
                    root = mappedUIComponent;

                root = mapComponents(e, mappedUIComponent, root);
            } else if (key.startsWith("@")) {
                MappedImport mappedImport = new MappedImport(key, this, orderedComponent.map.get(key));
                mappedImport.parse();
                UIMaster.registerMappedImport(mappedImport);
            }
        }
        return root;
    }

    private void mapImports(JsonElement element) {
        if (element == null) return;
        OrderedComponent orderedComponent = getComponents(element);
        if (orderedComponent.map.isEmpty()) return;
        for (int i = 0; i < orderedComponent.total; i++) {
            String key = orderedComponent.idMap.get(i);
            if (key.startsWith("~")) {
                JsonElement e = orderedComponent.map.get(key);
                MappedImport mappedImport = new MappedImport(key, this, e);
                mappedImport.parse();
                UIMaster.registerMappedImport(mappedImport);
                mapImports(e);
            }
        }
    }

    public class OrderedComponent {
        public Map<String, JsonElement> map;
        public Map<Integer, String> idMap = new HashMap<>();
        public int total = 0;
    }

    public OrderedComponent getComponents(JsonElement element) {
        Set<Map.Entry<String, JsonElement>> entries = element.getAsJsonObject().entrySet();
        Map<String, JsonElement> map = new HashMap<>();
        OrderedComponent orderedComponent = new OrderedComponent();
        orderedComponent.map = map;
        int i = 0;
        for (Map.Entry<String, JsonElement> entry : entries) {
            orderedComponent.idMap.put(i++, entry.getKey());
            map.put(entry.getKey(), entry.getValue());
        }
        orderedComponent.total = i;
        return orderedComponent;
    }


    public UIType getType(String entry) {
        for (UIType type : UIType.values()) {
            if (entry.toLowerCase().startsWith(type.getIdentifier().toLowerCase())) {
                return type;
            }
        }
        return UIUNKOWN;
    }


    private Consumer<File> handleResources = file -> {
        if (!file.isFile()) return;
        Optional<String> extension = getExtension(file.getName());
        if (extension.isEmpty()) return;
        if (extension.get().equalsIgnoreCase("ui")) {
            Optional<String> name = removeExtension(file.getName());
            if (name.isPresent()) {
                parseComponent(file);
            }
        }
    };


    private Optional<String> removeExtra(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(0, filename.lastIndexOf(".")));
    }

    private Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    private Optional<String> removeExtension(String filename) {
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
        return Files.isDirectory(p) && p.getParent().getFileName().normalize().toString().equalsIgnoreCase("resources");
    }


}
