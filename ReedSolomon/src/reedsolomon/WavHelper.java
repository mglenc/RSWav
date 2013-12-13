/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package reedsolomon;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.util.BitSet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author wirus
 */
public class WavHelper {
    
    public WavHelper(String filename)
    {
        ReadWav(filename);
    }
    
    public int ReadWav(String filename)
    {
        File wavFile = new File(filename);
        if (!wavFile.exists()) { 
            return 0;
        } 
        AudioInputStream audioInputStream = null;
        try { 
            audioInputStream = AudioSystem.getAudioInputStream(wavFile);
        } catch (UnsupportedAudioFileException ex) { 
            return -1;
        } catch (IOException ex) { 
            return -2;
        } 
        
        byte[] segmentData = new byte[12];
        
        int bytesResult = 0;
        
        try{
            while(bytesResult >= 0){
                bytesResult = audioInputStream.read(segmentData, 0, 8);
             
                BitSet infoBitSet = BitSet.valueOf(segmentData);
                
                int[] infoInt = new int[64];
                
                for(int i = 0; i < 64; i++) {
                    infoInt[i] = infoBitSet.get(i) ? 1 : 0;
                }
                
                //tu wywołanie kodera i dekodera zwracającego tablice byte'ów do odtworzenia
                byte[] coderData = new byte[5];
                coderData[0] = 64;
                coderData[1] = 120;
                coderData[2] = 70;
                coderData[3] = 15;
                coderData[4] = 1;
                
                int[] codedData = coder(infoInt, coderData);
                
                int[] decodedData = decoder(codedData, coderData);
            }
        }
        catch(IOException ex){
                    return -3;
        } 
        
        return 1;
    }
    
    public int hammingWeight(int data[]) {
        int count = 0;
        for(int i = 0; i < data.length; i++) {
            if(data[i] == 1) count++;
        }
        
        return count;
    }
    
    public int[] rotateRight( int data[], int k) {
            int temp = data[data.length-1];
            for(int i = 0; i < k; i++) {
                System.arraycopy(data, 0, data, 1, data.length-1);
                data[0] = temp;
            }
        
        return data;
    }
    
    public int[] rotateLeft(int data[], int k) {
        int temp = data[0];
        for(int i = 0; i < k; i++) {
            System.arraycopy(data, 1, data, 0, data.length-1);
            data[data.length-1] = temp;
        }
        
        return data;
    }
    
    Mathematic math = new Mathematic();
    
    public int[] coder(int[] data, byte[] coderData) {
        BitSet codePolyBitSet = BitSet.valueOf(coderData);
        int[] codePolyInt = new int[40];
        
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 8; j++) {
                codePolyInt[8 * i + j] = codePolyBitSet.get(8 * i + j) ? 1 : 0;
            }
        }
        
        System.out.printf("Wielomian generujacy:");
        for(int i = 0; i < codePolyInt.length; i++) {
            System.out.printf(Integer.toString(codePolyInt[i]));
        }
        
        //Mnozenie przez x do 4
        int [] dataMultiplied = new int[96];
        System.arraycopy(data, 0, dataMultiplied, 32, 64);

                
        AbstractMap.SimpleEntry<int [], int []> result = math.divideVectors(dataMultiplied, codePolyInt);
        int[] resultVector = result.getKey();
        int[] restVector = result.getValue();
        
        System.out.printf("\nDane: ");
        for(int i = 0; i < dataMultiplied.length; i++) {
            System.out.printf(Integer.toString(dataMultiplied[i]));
        }
        
        System.out.printf(" Reszta z dzielenia: ");
        for(int i = 0; i < restVector.length; i++) {
            System.out.printf(Integer.toString(restVector[i]));
        }
        
        //Laczymy czesc informacyjna (dane) z reszta z dzielenia przez generator
        //Wynikogo otrzymujemy ciag 12B = 96b
        System.arraycopy(restVector, 0, dataMultiplied, 0, restVector.length);
        
        System.out.printf("\nZakodowane dane: ");
        for(int i = 0; i < dataMultiplied.length; i++) {
            System.out.printf(Integer.toString(dataMultiplied[i]));
        }
        
        //Zaklamanie jednego bitu
        //dataMultiplied[7] = 1;
        dataMultiplied[25] = 1;
        //dataMultiplied[32] = 1;
        
        return dataMultiplied;
    }
    
    public int[] decoder(int[] data, byte[] coderData) {
        BitSet codePolyBitSet = BitSet.valueOf(coderData);
        int[] codePolyInt = new int[40];
        
        int correctingAbility = 2;
        
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 8; j++) {
                codePolyInt[8 * i + j] = codePolyBitSet.get(8 * i + j) ? 1 : 0;
            }
        }
        
        System.out.printf("\nWielomian generujacy:");
        for(int i = 0; i < codePolyInt.length; i++) {
            System.out.printf(Integer.toString(codePolyInt[i]));
        }

        //data = rotateRight(data, 8);
        
        //Obliczanie syndromu
        AbstractMap.SimpleEntry<int [], int []> result = math.divideVectors(data, codePolyInt);
        int[] resultVector = result.getKey();
        int[] restVector = result.getValue();
        
        System.out.printf("\nReszta z dzielenia zakodowanego słowa (syndrom błędu):");
        for(int i = 0; i < restVector.length; i++) {
            System.out.printf(Integer.toString(restVector[i]));
        }

        int[] decodedData = new int[64];
        //Obliczanie wagi bledu i porownanie ze zdolnoscia korekcyjna t
        if(hammingWeight(restVector) <= correctingAbility) {
            //Bledy w czesci kontrolnej, kopiujemy dobre dane
            System.arraycopy(data, 32, decodedData, 0, decodedData.length);
        } else {
            boolean readyForCorrection = false;
            int rotateCount = 0;
            int dataRotated[] = data;
            //Przesuniecie cykliczne w lewo
            while(!readyForCorrection) {
                rotateCount++;
                dataRotated = rotateLeft(dataRotated, 1);
                
                System.out.printf("\n%d. Obrot danych w lewo: ", rotateCount);
                for(int i = 0; i < dataRotated.length; i++) {
                    System.out.printf(Integer.toString(dataRotated[i]));
                }
                
                result = math.divideVectors(dataRotated, codePolyInt);
                resultVector = result.getKey();
                restVector = result.getValue();
                
                System.out.printf("\nSyndrom: ");
                for(int i = 0; i < restVector.length; i++) {
                    System.out.printf(Integer.toString(restVector[i]));
                }
                
                int weight = hammingWeight(restVector);
                
                System.out.printf("\nWaga Hamminga syndromu: %d", weight);
                
                System.out.printf("\n");
                
                if(weight <= correctingAbility) {
                    readyForCorrection = true;
                }
            }
        }
        
        System.out.printf("\nDane zdekodowane: ");
        for(int i = 0; i < decodedData.length; i++) {
            System.out.printf(Integer.toString(decodedData[i]));
        }
        
        System.out.printf("\n\n");
        
        return decodedData;
    }

}
