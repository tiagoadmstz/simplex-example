package io.github.tiagoadmstz.simplex.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MatrixUtil {

    private final List<String> lines;

    public MatrixUtil(List<String> lines) {
        this.lines = lines;
    }

    public Integer[] getVariablesAndRestrictions() {
        return Stream.of(lines.get(0).split("\\s+")).map(Integer::parseInt).toArray(Integer[]::new);
    }

    public Float[] getCoeficientes() {
        return Stream.of(Stream.of(lines.get(2), lines.get(3), lines.get(4), lines.get(5)).collect(Collectors.joining(" ")).split("\\s+")).map(Float::parseFloat).toArray(Float[]::new);
    }

    public Float[] getDemandas() {
        return Stream.of(lines.get(7).split("\\s+")).map(Float::parseFloat).toArray(Float[]::new);
    }

    public Float[] getCustosVariaveis() {
        return Stream.of(lines.get(8).split("\\s+")).map(Float::parseFloat).toArray(Float[]::new);
    }

}