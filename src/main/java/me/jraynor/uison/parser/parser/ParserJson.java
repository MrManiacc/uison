package me.jraynor.uison.parser.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import me.jraynor.bootstrap.Window;
import me.jraynor.uison.UIMaster;
import me.jraynor.uison.parser.MappedImport;
import me.jraynor.uison.parser.MappedUIComponent;
import me.jraynor.uison.parser.UIType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.jraynor.uison.parser.UIType.UIUNKOWN;

public class ParserJson extends Parser {

    public ParserJson() {
        super("src/main/resources/ui/");
    }

    public void parse() {
        super.parse();
        UIMaster.addEvents();
    }

    private MappedUIComponent parseComponent(File file) {
        JsonParser jsonParser = new JsonParser();
        try {
            List jsonLines = FileUtils.readLines(file);
            String jsonText = removeComments(jsonLines);
            JsonObject root = jsonParser.parse(jsonText).getAsJsonObject();
            mapImports(root);
            MappedUIComponent main = mapComponents(file, root, null, null);
            if (main != null) {
                rootComponents.put(main.getId(), main);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            String msg = e.getLocalizedMessage();
            int lineIndex = msg.indexOf("line");
            int path = msg.indexOf("path");
            String message = msg.substring(lineIndex, path);
            System.err.println("Error in json reverting to last working ui source.\nFILE: " + file.getName() + "\nLOCATION: " + message);
        }
        return null;
    }


    /**
     * Recursively map all of the components
     *
     * @param element
     * @return the total number of mapped components
     */
    private MappedUIComponent mapComponents(File file, JsonElement element, MappedUIComponent parent, MappedUIComponent root) {
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
                UIMaster.linkFileComponent(file.getAbsolutePath(), id);
                if (root == null)
                    root = mappedUIComponent;
                mapComponents(file, e, mappedUIComponent, root);

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
        Map<Integer, String> idMap = new HashMap<>();
        int total = 0;
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


    @Override
    void parseFile(File file) {
        parseComponent(file);
    }


}
