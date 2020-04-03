package io.github.tiagoadmstz.simplex.utils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public List<String> getLinesFromFile(String fileName) {
        try {
            File simplexFile = new File(getClass().getResource(fileName).getPath());
            return Files.readAllLines(simplexFile.toPath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList();
    }

}
