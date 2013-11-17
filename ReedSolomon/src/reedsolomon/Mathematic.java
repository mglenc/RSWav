/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package reedsolomon;

import java.util.AbstractMap;

/**
 *
 * @author wirus
 */
public class Mathematic {
    public AbstractMap.SimpleEntry<int[],int[]> divideVectors(int[] divisorVector, int[] dividentVector)
    {
        divisorVector = checkVector(divisorVector);
        dividentVector = checkVector(dividentVector);
        
        int[] resultVector = new int[divisorVector.length - dividentVector.length + 1];
        int scalarValue = 0;
        for(int i = divisorVector.length - 1; i >= dividentVector.length - 1; i--)
        {
            if(divisorVector[i] != 0)
            {
                scalarValue = i - (dividentVector.length - 1);
                resultVector[scalarValue] = divisorVector[i];
                int[] minusVector = mullVectorPerScalarValue(dividentVector, scalarValue);
                divisorVector = minusVectors(divisorVector, minusVector);
            }
        }
        return new AbstractMap.SimpleEntry<int[], int[]>(resultVector,checkVector(divisorVector));
    }
    
    public int[] checkVector(int [] inVector)
    {
        for(int i = inVector.length - 1; i >=0; i--)
        {
            if(inVector[i] == 0)
            {
                inVector = removeLastVectorElement(inVector);
            }
            else
                return inVector;
        }
        return inVector;
    }
    
    public int[] removeLastVectorElement(int[] inVector)
    {
        int[] returnVector = new int[inVector.length - 1];
        for(int i = 0; i < inVector.length - 1; i++)
        {
            returnVector[i] = inVector[i];
        }
        return returnVector;
    }
    
    public int[] mullVectorPerScalarValue(int[] dividentVector, int scalarValue) //scalar value = x^scalarValue
    {
        int[] resultVector = new int[dividentVector.length + scalarValue];
        int j = 0;
        for(int i = dividentVector.length - 1; i >= 0; i--)
        {
            resultVector[resultVector.length - j - 1] = dividentVector[i];
            j++;
        }
        return resultVector;
    }
    
    public int[] minusVectors(int[] divisorVector, int[] minusVector)
    {
        int[] resultVector = new int[divisorVector.length];
        for(int i = minusVector.length - 1; i >= 0; i--)
        {
            resultVector[i] = (divisorVector[i] + minusVector[i]) % 2;
        }
        return resultVector;
    }
}
