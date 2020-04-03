package io.github.tiagoadmstz.simplex;

import io.github.tiagoadmstz.simplex.models.SimplexMatrix;
import io.github.tiagoadmstz.simplex.utils.FileUtil;
import io.github.tiagoadmstz.simplex.utils.MatrixUtil;

public class Simplex {

    public static void main(String[] args) {
        new Simplex().solve();
    }

    public void solve() {
        MatrixUtil matrixUtil = new MatrixUtil(new FileUtil().getLinesFromFile("/Simplex2.txt"));
        Integer[] variablesAndRestrictions = matrixUtil.getVariablesAndRestrictions();
        new SimplexMatrix(variablesAndRestrictions[0],
                variablesAndRestrictions[1],
                matrixUtil.getCoeficientes(),
                matrixUtil.getCustosVariaveis(),
                matrixUtil.getDemandas());
    }

}
