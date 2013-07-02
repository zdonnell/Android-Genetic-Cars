package com.zdonnell.geneticcars;

/**
 * Created by zdonnell on 7/1/13.
 */
public class GeneticAlgorithms {
    public static boolean[] reproduce(boolean[] dna1, boolean[] dna2) {
        assert(dna1.length == dna2.length);

        boolean[] childDna = new boolean[dna1.length];

        int crossOverLoc = (int) Math.random() * dna1.length;
        for (int gene = 0; gene < dna1.length; gene++) {
           childDna[gene] = gene < crossOverLoc ? dna1[gene] : dna2[gene];
        }

        return childDna;
    }
}
