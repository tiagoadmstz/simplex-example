package io.github.tiagoadmstz.simplex;

import io.github.tiagoadmstz.simplex.models.SimplexMatrix;
import io.github.tiagoadmstz.simplex.utils.FileUtil;

public class Simplex {

    public static void main(String[] args) {
        new Simplex().solve();
    }

    public void solve() {
        FileUtil fileUtil = new FileUtil("/Simplex.txt");
        Integer[] variablesAndRestrictions = fileUtil.getVariablesAndRestrictions();
        SimplexMatrix simplexMatrix = new SimplexMatrix(variablesAndRestrictions[0],
                variablesAndRestrictions[1],
                fileUtil.getCoeficientes(),
                fileUtil.getCustosVariaveis(),
                fileUtil.getDemandas());
        simplexMatrix.calculateNewMatrix();
        System.out.println(simplexMatrix.toString());
    }

}
