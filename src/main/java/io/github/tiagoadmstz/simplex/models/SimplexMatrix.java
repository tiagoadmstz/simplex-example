package io.github.tiagoadmstz.simplex.models;

import io.github.tiagoadmstz.simplex.utils.PrintUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

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
    private BigDecimal matrix[][];
    private BigDecimal newLine[];
    private PrintUtil printUtil;

    public SimplexMatrix(Integer quantVariaveis, Integer quantFolgas) {
        this.quantVariaveis = quantVariaveis;
        this.quantFolgas = quantFolgas;
        initVaribles();
    }

    public SimplexMatrix(Integer quantVariaveis, Integer quantFolgas, Float[] coeficientes, Float[] custosVariaveis, Float[] demandas) {
        this.quantVariaveis = quantVariaveis;
        this.quantFolgas = quantFolgas;
        initVaribles(coeficientes, custosVariaveis, demandas);
        executeCalcules();
    }

    public SimplexMatrix(BigDecimal[][] matrix, Integer quantVariaveis, Integer quantFolgas) {
        this.matrix = matrix;
        this.rows = matrix.length;
        this.columns = matrix[0].length;
        this.newLine = new BigDecimal[columns];
        this.quantVariaveis = quantVariaveis;
        this.quantFolgas = quantFolgas;
    }

    private void initVaribles() {
        this.rows = quantFolgas + 1;
        this.columns = quantVariaveis + quantFolgas + 1;
        this.matrix = new BigDecimal[rows][columns];
        this.newLine = new BigDecimal[columns];
        this.printUtil = new PrintUtil(quantVariaveis, quantFolgas);
    }

    private void initVaribles(Float[] coeficientes, Float[] custosVariaveis, Float[] demandas) {
        initVaribles();
        setCoeficientesRestricoes(coeficientes);
        setCustosVariaveis(custosVariaveis);
        setDemandas(demandas);
    }

    public BigDecimal[][] executeCalcules() {
        findPivotColumnValue();
        findPivotLineValue();
        findNewLine();
        System.out.println(toString());
        return recaluleMatrix();
    }

    public void setCustosVariaveis(Float... custoVariavel) {
        for (int column = 0; column < columns; column++) {
            if (column < quantVariaveis) {
                matrix[0][column] = new BigDecimal(custoVariavel[column]).multiply(BigDecimal.valueOf(1l).negate());
            } else {
                matrix[0][column] = new BigDecimal(0);
            }
        }
    }

    public void setCoeficientesRestricoes(Float... coeficiente) {
        int count = 0;
        for (int row = 1; row < (quantFolgas + 1); row++) {
            for (int column = 0; column < (columns - 1); column++) {
                if (column < quantVariaveis) {
                    matrix[row][column] = new BigDecimal(coeficiente[count++]);
                } else {
                    matrix[row][column] = new BigDecimal(column == (quantVariaveis + row - 1) ? 1 : 0);
                }
            }
        }
    }

    public BigDecimal[] getDemandas() {
        BigDecimal[] demandas = new BigDecimal[rows];
        for (int row = 0; row < rows; row++) {
            demandas[row] = matrix[row][columns - 1];
        }
        return demandas;
    }

    public void setDemandas(Float... demanda) {
        for (int row = 1; row < rows; row++) {
            matrix[row][columns - 1] = new BigDecimal(demanda[row - 1]);
        }
    }

    private String getPivotColumn() {
        return String.format("X%s", pivotColumn + 1);
    }

    private String getPivotLine() {
        return String.format("F%s", pivotLine);
    }

    private BigDecimal findPivotColumnValue() {
        pivotColumnValue = BigDecimal.valueOf(0l);
        for (int column = 0; column < quantVariaveis; column++) {
            if (pivotColumnValue.floatValue() > this.matrix[0][column].floatValue()) {
                pivotColumn = column;
                pivotColumnValue = this.matrix[0][column];
            }
        }
        return pivotColumnValue;
    }

    private BigDecimal findPivotLineValue() {
        findPivotColumnValue();
        BigDecimal[] demandas = getDemandas();
        for (int d = 1; d < demandas.length; d++) {
            if (matrix[d][pivotColumn].floatValue() != 0.000f) {
                BigDecimal divide = demandas[d].divide(matrix[d][pivotColumn], 2, RoundingMode.HALF_EVEN);
                if (pivotLineValue == null) {
                    pivotLine = d;
                    pivotNumber = matrix[d][pivotColumn];
                    pivotLineValue = divide;
                }
                if (pivotLineValue.floatValue() > divide.floatValue()) {
                    pivotLine = d;
                    pivotNumber = matrix[d][pivotColumn];
                    pivotLineValue = divide;
                }
            }
        }
        return pivotLineValue;
    }

    private BigDecimal[] findNewLine() {
        for (int column = 0; column < columns; column++) {
            newLine[column] = matrix[pivotLine][column].divide(pivotNumber, 2, RoundingMode.HALF_EVEN);
        }
        return newLine;
    }

    /**
     * Linha Antiga - (CP * NLP) + valor linha
     *
     * @return
     */
    private BigDecimal[][] recaluleMatrix() {
        BigDecimal[][] newMatrix = new BigDecimal[matrix.length][matrix[0].length];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (row == pivotLine) {
                    newMatrix[row][column] = newLine[column];
                } else {
                    BigDecimal baseNumber = matrix[row][pivotColumn].multiply(BigDecimal.ONE.negate());
                    BigDecimal multiply = baseNumber.multiply(newLine[column]);
                    multiply.add(matrix[row][column]);
                    newMatrix[row][column] = baseNumber.multiply(newLine[column]).add(matrix[row][column]);
                }
            }
        }
        System.out.println(printUtil.stringCabecalho()
                + printUtil.stringProblemaProgramacaoLinear(newMatrix)
                + printUtil.stringRestricoes(newMatrix));

        while (zRowContainNegativeNumber(newMatrix)) {
            SimplexMatrix simplexMatrix = new SimplexMatrix(newMatrix, quantVariaveis, quantFolgas);
            newMatrix = simplexMatrix.executeCalcules();
            System.out.println(simplexMatrix.toString());
        }
        return newMatrix;
    }

    private boolean zRowContainNegativeNumber(BigDecimal[][] matrix) {
        return Stream.of(matrix[0]).anyMatch(number -> number.floatValue() < 0);
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
