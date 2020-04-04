package io.github.tiagoadmstz.simplex.utils;

import java.math.BigDecimal;

public class PrintUtil {

    private String lineStr = "  ___ ";
    private String columnStr = "|  %s  ";
    private String cabecalho = "";
    private Integer quantVariaveis = 0;
    private Integer quantFolgas = 0;

    public PrintUtil(Integer quantVariaveis, Integer quantFolgas) {
        this.quantVariaveis = quantVariaveis;
        this.quantFolgas = quantFolgas;
    }

    public String stringCabecalho() {
        if ("".equals(cabecalho)) {
            cabecalho = "\n|     ";
            for (int column = 0; column < quantVariaveis; column++) {
                lineStr += column < 10 ? "  ____ " : "  _____ ";
                cabecalho += String.format(columnStr, "X" + (column + 1));
            }
            for (int column = 0; column < quantFolgas; column++) {
                lineStr += column < 10 ? "  ____ " : "  _____ ";
                cabecalho += String.format(columnStr, "F" + (column + 1));
            }
            lineStr += "  ____  ";
            cabecalho += "|  LD  |\n";
            cabecalho = lineStr + cabecalho + lineStr.replaceAll("_", "-");
        }
        return cabecalho;
    }

    public String stringProblemaProgramacaoLinear(Object[][] matrix) {
        String z = "\n|  Z  ";
        for (int column = 1; column < matrix[0].length; column++) {
            z += String.format(((BigDecimal) matrix[1][column]).floatValue() < 0 ? "| %s " : columnStr,
                    ((BigDecimal) matrix[1][column]).floatValue() < 10 ? matrix[1][column] + " " : matrix[1][column]);
        }
        return z + "|\n" + lineStr.replaceAll("_", "-");
    }

    public String stringRestricoes(Object[][] matrix) {
        String r = "";
        for (int f = 1; f <= quantFolgas; f++) {
            r += "\n|  F" + f + " ";
            for (int column = 1; column < matrix[0].length; column++) {
                r += String.format(((BigDecimal) matrix[f][column]).intValue() < 10 ? "|   %s  "
                                : ((BigDecimal) matrix[f][column]).intValue() < 100 ? columnStr : "|  %s ",
                        matrix[f][column]);
            }
            r += "|\n" + lineStr.replaceAll("_", "-");
        }
        return r;
    }

    public String stringNewLine(BigDecimal[] newLine, String pivotColumn) {
        if ("".equals(cabecalho)) stringCabecalho();
        String nl = String.format("\n|  %s ", pivotColumn);
        for (int column = 0; column < newLine.length; column++) {
            nl += String.format("| %s ", newLine[column]);
        }
        return cabecalho + nl + "|\n" + lineStr.replaceAll("_", "-");
    }

}
