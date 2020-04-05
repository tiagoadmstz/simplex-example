package io.github.tiagoadmstz.simplex.utils;

import io.github.tiagoadmstz.simplex.models.SimplexMatrix;

public abstract class PrintUtil {

    public static String matrixSimplexToString(Object[][] matrix) {
        String matrixString = "";
        int columnLength = getGretterLabelColumnLength(matrix);
        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; column < matrix[0].length; column++) {
                Object value = matrix[row][column];
                matrixString += mountColumnString(columnLength, value.toString());
            }
            matrixString += "\n";
        }
        return matrixString;
    }

    public static String newLineToString(SimplexMatrix simplexMatrix) {
        String newLineString = "";
        Object[][] matrix = simplexMatrix.getMatrix();
        Object[] newLine = simplexMatrix.getNewLine();
        int columnLength = getGretterLabelColumnLength(matrix);
        for (int column = 0; column < matrix[0].length; column++) {
            newLineString += mountColumnString(columnLength, matrix[0][column].toString());
        }
        newLineString += "\n";
        for (int column = 0; column < matrix[0].length; column++) {
            newLineString += mountColumnString(columnLength, newLine[column].toString());
        }
        return newLineString;
    }

    public static String resultTableToString(Object[][] resultTable) {
        String resultTableString = "";
        for (int row = 0; row < resultTable.length; row++) {
            resultTableString += String.format("%s = %s\n", resultTable[row][0], resultTable[row][1]);
        }
        return resultTableString;
    }

    private static int getGretterLabelColumnLength(Object[][] matrix) {
        int gretter = 0;
        for (int row = 1; row < matrix.length; row++) {
            for (int column = 1; column < matrix[0].length; column++) {
                Object value = matrix[row][column];
                if (value != null) {
                    int length = value.toString().length();
                    if (gretter < length) {
                        gretter = length;
                    }
                }
            }
        }
        return gretter;
    }

    private static String mountColumnString(int columnLength, String value) {
        String baseStr = "| %s ";
        for (int cl = 0; cl < columnLength / 2; cl++) {
            baseStr = String.format(baseStr, " %s ");
        }
        return String.format(baseStr, !"".equals(value) ? value : " ");
    }

}
