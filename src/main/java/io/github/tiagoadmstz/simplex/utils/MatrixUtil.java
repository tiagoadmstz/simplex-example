package io.github.tiagoadmstz.simplex.utils;

import io.github.tiagoadmstz.simplex.models.SimplexMatrix;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class MatrixUtil {

    public static Object[][] mountColumnsLabels(Object[][] columnsNames, Integer quantVariaveis, Integer quantFolgas) {
        int count = 0;
        for (String variavel : mountVariables(quantVariaveis)) columnsNames[0][count++] = variavel;
        List<String> folgas = mountClearances(quantFolgas);
        int row = 1;
        for (String folga : folgas) columnsNames[row++][0] = folga;
        folgas.add("LD");
        for (String folga : folgas) if (!"Z".equals(folga)) columnsNames[0][count++] = folga;
        return columnsNames;
    }

    private static boolean zRowContainNegativeNumber(Object[][] matrix) {
        return Stream.of(matrix[1])
                .filter(f -> f instanceof BigDecimal)
                .map(number -> (BigDecimal) number)
                .anyMatch(number -> number.floatValue() < 0);
    }

    private static List<String> mountVariables(int quantVariaveis) {
        return IntStream.rangeClosed(0, quantVariaveis).boxed().map(i -> i == 0 ? "" : "X" + i).collect(Collectors.toList());
    }

    private static List<String> mountClearances(int quantFolgas) {
        return IntStream.rangeClosed(0, quantFolgas).boxed().map(i -> i == 0 ? "Z" : "F" + i).collect(Collectors.toList());
    }

    public static void findPivotColumn(SimplexMatrix simplexMatrix) {
        Object[][] matrix = simplexMatrix.getMatrix();
        simplexMatrix.setPivotColumnValue(BigDecimal.valueOf(0l));
        for (int column = 1; column < simplexMatrix.getVariables() + 1; column++) {
            if (simplexMatrix.getPivotColumnValue().floatValue() > ((BigDecimal) matrix[1][column]).floatValue()) {
                simplexMatrix.setPivotColumn(column);
                simplexMatrix.setPivotColumnValue((BigDecimal) matrix[1][column]);
            }
        }
    }

    public static void findPivotLine(SimplexMatrix simplexMatrix) {
        findPivotColumn(simplexMatrix);
        BigDecimal[] demandas = simplexMatrix.getDemandas();
        for (int d = 2; d < demandas.length; d++) {
            BigDecimal value = (BigDecimal) simplexMatrix.getMatrix()[d][simplexMatrix.getPivotColumn()];
            if (value.floatValue() != 0.000f) {
                BigDecimal divide = demandas[d - 1].divide(value, 2, RoundingMode.HALF_EVEN);
                if (simplexMatrix.getPivotLineValue() == null) setPivotLineValues(simplexMatrix, d, value, divide);
                if (simplexMatrix.getPivotLineValue().floatValue() > divide.floatValue())
                    setPivotLineValues(simplexMatrix, d, value, divide);
            }
        }
    }

    private static void setPivotLineValues(SimplexMatrix simplexMatrix, Integer pivotLineIndex, BigDecimal pivotNumber, BigDecimal pivotLineValue) {
        simplexMatrix.setPivotLine(pivotLineIndex);
        simplexMatrix.setPivotNumber(pivotNumber);
        simplexMatrix.setPivotLineValue(pivotLineValue);
    }

    public static void calculateNewLine(SimplexMatrix simplexMatrix) {
        for (int column = 1; column < simplexMatrix.getColumns(); column++) {
            simplexMatrix.getNewLine()[column] = ((BigDecimal) simplexMatrix.getMatrix()[simplexMatrix.getPivotLine()][column]).divide(simplexMatrix.getPivotNumber(), 2, RoundingMode.HALF_EVEN);
        }
        simplexMatrix.getNewLine()[0] = simplexMatrix.getPivotColumnLabel();
    }

    /**
     * Linha Antiga - (CP * NLP) + valor linha
     *
     * @return
     */
    public static void calculetePPLWithSimplexAlgorithm(SimplexMatrix simplexMatrix) {
        simplexMatrix.setNewMatrix(cloneTwoDimensionalArray(simplexMatrix.getMatrix()));
        for (int row = 1; row < simplexMatrix.getRows(); row++) {
            BigDecimal baseNumber = ((BigDecimal) simplexMatrix.getMatrix()[row][simplexMatrix.getPivotColumn()]).multiply(BigDecimal.ONE.negate());
            if (row == simplexMatrix.getPivotLine())
                simplexMatrix.getNewMatrix()[row][0] = simplexMatrix.getPivotColumnLabel();
            for (int column = 1; column < simplexMatrix.getColumns(); column++) {
                if (row == simplexMatrix.getPivotLine()) {
                    simplexMatrix.getNewMatrix()[row][column] = simplexMatrix.getNewLine()[column];
                } else {
                    simplexMatrix.getNewMatrix()[row][column] = baseNumber.multiply((BigDecimal) simplexMatrix.getNewLine()[column]).add((BigDecimal) simplexMatrix.getNewMatrix()[row][column]);
                }
            }
        }

        while (zRowContainNegativeNumber(simplexMatrix.getNewMatrix())) {
            SimplexMatrix sm = new SimplexMatrix(
                    simplexMatrix.getNewMatrix(),
                    simplexMatrix.getVariables(),
                    simplexMatrix.getClearances()
            );
            sm.findKeyValues();
            simplexMatrix.setNewMatrix(sm.calculateNewMatrix());
        }
        generateResultTable(simplexMatrix);
    }

    public static void generateResultTable(SimplexMatrix simplexMatrix) {
        Object[][] newMatrix = simplexMatrix.getNewMatrix();
        simplexMatrix.setResultTable(new Object[newMatrix[0].length - 1][2]);
        simplexMatrix.getResultTable()[0][0] = "Z";
        simplexMatrix.getResultTable()[0][1] = BigDecimal.ZERO;

        for (int row = 1; row < simplexMatrix.getResultTable().length; row++) {
            simplexMatrix.getResultTable()[row][0] = newMatrix[0][row];
            simplexMatrix.getResultTable()[row][1] = BigDecimal.ZERO;
        }

        for (int row = 1; row < newMatrix.length; row++) {
            for (int rRow = 0; rRow < simplexMatrix.getResultTable().length; rRow++) {
                if (newMatrix[row][0].equals(simplexMatrix.getResultTable()[rRow][0])) {
                    simplexMatrix.getResultTable()[rRow][1] = newMatrix[row][(newMatrix[0].length - 1)];
                    break;
                }
            }
        }
    }

    public static Object[][] cloneTwoDimensionalArray(Object[][] array) {
        Object[][] newArray = new Object[array.length][array[0].length];
        for (int row = 0; row < array.length; row++) {
            for (int column = 0; column < array[0].length; column++) {
                newArray[row][column] = colneObject(array[row][column]);
            }
        }
        return newArray;
    }

    public static Object colneObject(Object object) {
        Object newObject = null;
        try {
            if (object != null) {
                if (object instanceof String) {
                    newObject = object.toString();
                } else if (object instanceof BigDecimal) {
                    newObject = new BigDecimal(object.toString());
                }
            }
            return newObject;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newObject;
    }

}
