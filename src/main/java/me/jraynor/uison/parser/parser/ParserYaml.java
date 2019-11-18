package me.jraynor.uison.parser.parser;

import me.jraynor.bootstrap.Window;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ParserYaml extends Parser {
    private String directory;
    private Yaml yaml;

    public ParserYaml() {
        super("src/main/resources/ui/yml");
        this.yaml = new Yaml();
        this.directory = directory;
    }

    @Override
    void parseFile(File file) {
        List jsonLines = null;
        try {
            Map<String, Object> map = yaml.load(FileUtils.readFileToString(file));
            //System.out.println(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
