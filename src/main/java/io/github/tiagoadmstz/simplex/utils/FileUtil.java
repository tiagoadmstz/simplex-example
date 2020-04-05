package io.github.tiagoadmstz.simplex.utils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileUtil {

    private final List<String> lines;

    public FileUtil(String fileName) {
        lines = new ArrayList();
        try {
            File simplexFile = new File(getClass().getResource(fileName).getPath());
            lines.addAll(Files.readAllLines(simplexFile.toPath()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Integer[] getVariablesAndRestrictions() {
        return Stream.of(lines.get(0).split("\\s+")).map(Integer::parseInt).toArray(Integer[]::new);
    }

    public Double[] getCoeficientes() {
        List<String> matrixLines = new ArrayList();
        for (int l = 2; l < lines.size(); l++) {
            if ("".equals(lines.get(l).trim())) break;
            for (String number : lines.get(l).split("\\s+")) matrixLines.add(number);
        }
        return matrixLines.stream().map(Double::parseDouble).toArray(Double[]::new);
    }

    public Double[] getDemandas() {
        return Stream.of(lines.get(getDemandasLine()).split("\\s+")).map(Double::parseDouble).toArray(Double[]::new);
    }

    public Double[] getCustosVariaveis() {
        return Stream.of(lines.get(getDemandasLine() + 1).split("\\s+")).map(Double::parseDouble).toArray(Double[]::new);
    }

    private int getDemandasLine() {
        for (int l = 2; l < lines.size(); l++) {
            if ("".equals(lines.get(l).trim())) {
                return ++l;
            }
        }
        return 0;
    }

}
