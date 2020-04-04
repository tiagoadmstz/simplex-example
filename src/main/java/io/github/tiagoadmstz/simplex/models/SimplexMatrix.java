package io.github.tiagoadmstz.simplex.models;

import io.github.tiagoadmstz.simplex.utils.MatrixUtil;
import io.github.tiagoadmstz.simplex.utils.PrintUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class SimplexMatrix {

    private Integer pivotColumn = null;
    private BigDecimal pivotColumnValue = null;
    private Integer pivotLine = null;
    private BigDecimal pivotLineValue = null;
    private BigDecimal pivotNumber = null;
    private Integer quantVariaveis = 0;
    private Integer quantFolgas = 0;
    private Integer rows = 0;
    private Integer columns = 0;
    private Object[][] matrix;
    private BigDecimal[] newLine;
    private PrintUtil printUtil;
    private MatrixUtil matrixUtil;

    public SimplexMatrix(Integer quantVariaveis, Integer quantFolgas) {
        this.quantVariaveis = quantVariaveis;
        this.quantFolgas = quantFolgas;
        initVariables();
    }

    public SimplexMatrix(Integer quantVariaveis, Integer quantFolgas, Float[] coeficientes, Float[] custosVariaveis, Float[] demandas) {
        this.quantVariaveis = quantVariaveis;
        this.quantFolgas = quantFolgas;
        initVariables(coeficientes, custosVariaveis, demandas);
        executeCalcules();
    }

    public SimplexMatrix(Object[][] matrix, Integer quantVariaveis, Integer quantFolgas) {
        this.matrix = matrix;
        this.rows = matrix.length;
        this.columns = matrix[0].length;
        this.newLine = new BigDecimal[columns];
        this.quantVariaveis = quantVariaveis;
        this.quantFolgas = quantFolgas;
        this.printUtil = new PrintUtil(quantVariaveis, quantFolgas);
        this.matrixUtil = new MatrixUtil();
    }

    private void initVariables() {
        rows = quantFolgas + 2;
        columns = quantVariaveis + quantFolgas + 2;
        matrix = new Object[rows][columns];
        newLine = new BigDecimal[columns];
        printUtil = new PrintUtil(quantVariaveis, quantFolgas);
        matrixUtil = new MatrixUtil();
        matrixUtil.mountColumnsNames(matrix, quantVariaveis, quantFolgas);
    }

    private void initVariables(Float[] coeficientes, Float[] custosVariaveis, Float[] demandas) {
        initVariables();
        setCoeficientesRestricoes(coeficientes);
        setCustosVariaveis(custosVariaveis);
        setDemandas(demandas);
    }

    public Object[][] executeCalcules() {
        findPivotColumnValue();
        findPivotLineValue();
        findNewLine();
        System.out.println(toString());
        return recaluleMatrix();
    }

    public void setCustosVariaveis(Float... custoVariavel) {
        for (int column = 1; column < columns; column++) {
            if (column < quantVariaveis + 1) {
                matrix[1][column] = new BigDecimal(custoVariavel[column - 1]).multiply(BigDecimal.valueOf(1l).negate());
            } else {
                matrix[1][column] = new BigDecimal(0);
            }
        }
    }

    public void setCoeficientesRestricoes(Float... coeficiente) {
        int count = 0;
        for (int row = 2; row < (quantFolgas + 2); row++) {
            for (int column = 1; column < (columns - 1); column++) {
                if (column < quantVariaveis + 1) {
                    matrix[row][column] = new BigDecimal(coeficiente[count++]);
                } else {
                    matrix[row][column] = new BigDecimal(column == (quantVariaveis + row - 1) ? 1 : 0);
                }
            }
        }
    }

    public BigDecimal[] getDemandas() {
        BigDecimal[] demandas = new BigDecimal[rows];
        for (int row = 1; row < rows; row++) {
            demandas[row - 1] = (BigDecimal) matrix[row][columns - 1];
        }
        return demandas;
    }

    public void setDemandas(Float... demanda) {
        for (int row = 2; row < rows; row++) {
            matrix[row][columns - 1] = new BigDecimal(demanda[row - 2]);
        }
    }

    private String getPivotColumn() {
        return String.format("X%s", pivotColumn);
    }

    private String getPivotLine() {
        return String.format("F%s", pivotLine);
    }

    private BigDecimal findPivotColumnValue() {
        pivotColumnValue = BigDecimal.valueOf(0l);
        for (int column = 1; column < quantVariaveis + 1; column++) {
            if (pivotColumnValue.floatValue() > ((BigDecimal) this.matrix[1][column]).floatValue()) {
                pivotColumn = column;
                pivotColumnValue = (BigDecimal) this.matrix[1][column];
            }
        }
        return pivotColumnValue;
    }

    private BigDecimal findPivotLineValue() {
        findPivotColumnValue();
        BigDecimal[] demandas = getDemandas();
        for (int d = 2; d < demandas.length; d++) {
            BigDecimal value = (BigDecimal) matrix[d][pivotColumn];
            if (value.floatValue() != 0.000f) {
                BigDecimal divide = demandas[d - 1].divide(value, 2, RoundingMode.HALF_EVEN);
                if (pivotLineValue == null) {
                    pivotLine = d;
                    pivotNumber = value;
                    pivotLineValue = divide;
                }
                if (pivotLineValue.floatValue() > divide.floatValue()) {
                    pivotLine = d;
                    pivotNumber = value;
                    pivotLineValue = divide;
                }
            }
        }
        return pivotLineValue;
    }

    private BigDecimal[] findNewLine() {
        for (int column = 1; column < columns; column++) {
            newLine[column - 1] = ((BigDecimal) matrix[pivotLine][column]).divide(pivotNumber, 2, RoundingMode.HALF_EVEN);
        }
        return newLine;
    }

    /**
     * Linha Antiga - (CP * NLP) + valor linha
     *
     * @return
     */
    private Object[][] recaluleMatrix() {
        Object[][] newMatrix = Arrays.copyOf(matrix, matrix.length);
        for (int row = 1; row < rows; row++) {
            BigDecimal baseNumber = ((BigDecimal) matrix[row][pivotColumn]).multiply(BigDecimal.ONE.negate());
            if (row == pivotLine) newMatrix[row][0] = getPivotColumn();
            for (int column = 1; column < columns; column++) {
                if (row == pivotLine) {
                    newMatrix[row][column] = newLine[column - 1];
                } else {
                    newMatrix[row][column] = baseNumber.multiply(newLine[column - 1]).add((BigDecimal) matrix[row][column]);
                }
            }
        }
        System.out.println(printUtil.stringCabecalho()
                + printUtil.stringProblemaProgramacaoLinear(newMatrix)
                + printUtil.stringRestricoes(newMatrix));

        while (matrixUtil.zRowContainNegativeNumber(newMatrix)) {
            SimplexMatrix simplexMatrix = new SimplexMatrix(newMatrix, quantVariaveis, quantFolgas);
            newMatrix = simplexMatrix.executeCalcules();
            System.out.println(simplexMatrix.toString());
        }
        return newMatrix;
    }

    @Override
    public String toString() {
        return String.format("Tabela razão:\n%s\n\nColuna Pivô: %s => %s\nLinha Pivô: %s => %s\nNúmero Pivô: %s\n\nNova Linha (Linha Pivô/Número Pivô):\n%s",
                printUtil.stringCabecalho() + printUtil.stringProblemaProgramacaoLinear(matrix) + printUtil.stringRestricoes(matrix),
                getPivotColumn(),
                pivotColumnValue,
                getPivotLine(),
                pivotLineValue,
                pivotNumber,
                printUtil.stringNewLine(newLine, getPivotColumn())
        );
    }

}
