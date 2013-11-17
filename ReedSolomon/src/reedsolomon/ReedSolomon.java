/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package reedsolomon;

import java.util.AbstractMap;

import java.util.BitSet;

/**
 *
 * @author wirus
 */
public class ReedSolomon {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        WavHelper wh = new WavHelper("F:\\hallelujah.wav");
        
        /*Mathematic math = new Mathematic();
        int[] divisorVector = new int[8];
        int[] dividentVector = new int[5];
        divisorVector[7] = 1;
        divisorVector[6] = 1;
        divisorVector[3] = 1;
        dividentVector[4] = 1;
        dividentVector[2] = 1;
        AbstractMap.SimpleEntry<int[], int[]> result = math.divideVectors(divisorVector, dividentVector);
        int[] resultVector = result.getKey();
        int[] restVector = result.getValue();
        for(int i = resultVector.length - 1; i >= 0; i--)
        {
            System.out.printf(Integer.toString(resultVector[i]));
        }*/   
    }
}
