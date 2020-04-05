package io.github.tiagoadmstz.simplex.models;

import io.github.tiagoadmstz.simplex.utils.MatrixUtil;
import io.github.tiagoadmstz.simplex.utils.PrintUtil;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class SimplexMatrix {

    @Getter
    @Setter
    private Integer pivotColumn = null;
    @Getter
    @Setter
    private BigDecimal pivotColumnValue = null;
    @Getter
    @Setter
    private Integer pivotLine = null;
    @Getter
    @Setter
    private BigDecimal pivotLineValue = null;
    @Getter
    @Setter
    private BigDecimal pivotNumber = null;
    @Getter
    private final Integer variables;
    @Getter
    private final Integer clearances;
    @Getter
    private Integer rows = 0;
    @Getter
    private Integer columns = 0;
    @Getter
    private Object[][] matrix;
    @Getter
    private Object[] newLine;
    @Getter
    @Setter
    private Object[][] newMatrix;
    @Getter
    @Setter
    private Object[][] resultTable;

    public SimplexMatrix(Integer variables, Integer clearances, Double[] coeficientes, Double[] custosVariaveis, Double[] demandas) {
        this.variables = variables;
        this.clearances = clearances;
        initVariables(coeficientes, custosVariaveis, demandas);
        findKeyValues();
    }

    public SimplexMatrix(Object[][] matrix, Integer variables, Integer clearances) {
        this.matrix = matrix;
        this.rows = matrix.length;
        this.columns = matrix[0].length;
        this.newLine = new Object[columns];
        this.variables = variables;
        this.clearances = clearances;
    }

    private void initVariables() {
        rows = clearances + 2;
        columns = variables + clearances + 2;
        matrix = new Object[rows][columns];
        newLine = new Object[columns];
        MatrixUtil.mountColumnsLabels(matrix, variables, clearances);
    }

    private void initVariables(Double[] coeficientes, Double[] custosVariaveis, Double[] demandas) {
        initVariables();
        insertCoeficientesRestricoesOnMatrix(coeficientes);
        insertCustosVariaveisOnMatrix(custosVariaveis);
        insertDemandasOnMatrix(demandas);
    }

    public void findKeyValues() {
        MatrixUtil.findPivotColumn(this);
        MatrixUtil.findPivotLine(this);
        MatrixUtil.calculateNewLine(this);
    }

    private void insertCustosVariaveisOnMatrix(Double... custoVariavel) {
        for (int column = 1; column < columns; column++) {
            if (column < variables + 1) {
                matrix[1][column] = new BigDecimal(custoVariavel[column - 1]).multiply(BigDecimal.valueOf(1l).negate());
            } else {
                matrix[1][column] = new BigDecimal(0);
            }
        }
    }

    private void insertCoeficientesRestricoesOnMatrix(Double... coeficiente) {
        int count = 0;
        for (int row = 2; row < (clearances + 2); row++) {
            for (int column = 1; column < (columns - 1); column++) {
                if (column < variables + 1) {
                    matrix[row][column] = new BigDecimal(coeficiente[count++]);
                } else {
                    matrix[row][column] = new BigDecimal(column == (variables + row - 1) ? 1 : 0);
                }
            }
        }
    }

    private void insertDemandasOnMatrix(Double... demanda) {
        for (int row = 2; row < rows; row++) {
            matrix[row][columns - 1] = new BigDecimal(demanda[row - 2]);
        }
    }

    public BigDecimal[] getDemandas() {
        BigDecimal[] demandas = new BigDecimal[rows];
        for (int row = 1; row < rows; row++) {
            demandas[row - 1] = (BigDecimal) matrix[row][columns - 1];
        }
        return demandas;
    }

    public String getPivotColumnLabel() {
        return matrix[0][pivotColumn].toString();
    }

    public String getPivotLineLabel() {
        return matrix[pivotLine][0].toString();
    }

    public Object[][] calculateNewMatrix() {
        MatrixUtil.calculetePPLWithSimplexAlgorithm(this);
        return newMatrix;
    }

    public String matrixToString() {
        String matrixString = "\nTabela razão:\n%s\n";
        if (matrix[1][1] != null) {
            return String.format(matrixString, PrintUtil.matrixSimplexToString(matrix));
        }
        return String.format(matrixString, "Tabela vazia");
    }

    public String keyValuesToString() {
        String keyValues = "\nColuna Pivô: %s => %s\nLinha Pivô: %s => %s\nNúmero Pivô: %s\n";
        if (pivotColumnValue != null && pivotLineValue != null && pivotNumber != null) {
            return String.format(keyValues,
                    getPivotColumnLabel(),
                    pivotColumnValue,
                    getPivotLineLabel(),
                    pivotLineValue,
                    pivotNumber
            );
        }
        return "\nOs valores chaves ainda não foram encontrados\n";
    }

    public String newLineToString() {
        String newLineToString = "\nNova Linha (Linha Pivô/Número Pivô):\n%s\n";
        if (newLine[0] != null) {
            return String.format(newLineToString, PrintUtil.newLineToString(this));
        }
        return String.format(newLineToString, "Os calculos não foram realizados.");
    }

    public String newMatrixToString() {
        String newMatrixString = "\nTabela Calculada com o algoritimo simplex: \n%s\n";
        if (newMatrix != null) {
            return String.format(newMatrixString, PrintUtil.matrixSimplexToString(newMatrix));
        }
        return String.format(newMatrixString, "Os calculos não foram realizados.");
    }

    public String resultTableToString() {
        String resultString = "\nTabela de resultados: \n%s\n";
        if (resultTable != null) {
            return String.format(resultString, PrintUtil.resultTableToString(resultTable));
        }
        return String.format(resultString, "A tabela de resultados ainda não foi criada");
    }

    @Override
    public String toString() {
        return String.format("%s%s%s%s%s",
                matrixToString(),
                keyValuesToString(),
                newLineToString(),
                newMatrixToString(),
                resultTableToString()
        );
    }

}
