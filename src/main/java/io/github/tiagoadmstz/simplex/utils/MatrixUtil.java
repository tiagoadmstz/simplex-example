package io.github.tiagoadmstz.simplex.utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MatrixUtil {

    public Object[][] mountColumnsNames(Object[][] columnsNames, Integer quantVariaveis, Integer quantFolgas) {
        int count = 0;
        for (String variavel : mountVariaveis(quantVariaveis)) columnsNames[0][count++] = variavel;
        List<String> folgas = mountFolgas(quantFolgas);
        for (String folga : folgas) if (!"Z".equals(folga)) columnsNames[0][count++] = folga;
        count = 1;
        for (String folga : folgas) columnsNames[count++][0] = folga;
        return columnsNames;
    }

    public boolean zRowContainNegativeNumber(Object[][] matrix) {
        return Stream.of(matrix[1])
                .filter(f -> f instanceof BigDecimal)
                .map(number -> (BigDecimal) number)
                .anyMatch(number -> number.floatValue() < 0);
    }

    private List<String> mountVariaveis(int quantVariaveis) {
        return IntStream.rangeClosed(0, quantVariaveis).boxed().map(i -> i == 0 ? "" : "X" + i).collect(Collectors.toList());
    }

    private List<String> mountFolgas(int quantFolgas) {
        return IntStream.rangeClosed(0, quantFolgas).boxed().map(i -> i == 0 ? "Z" : "F" + i).collect(Collectors.toList());
    }

    public void iterateBidimencionalArray(Object[][] matrix, Consumer consumer) {
        if (matrix != null) {
            for (int row = 0; row < matrix.length; row++) {
                for (int column = 0; column < matrix[0].length; column++) {
                    consumer.accept(matrix[row][column]);
                }
            }
        }
    }

}
