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
import java.util.Arrays;

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
            int byteCount = 0;
            while(bytesResult >= 0){
                bytesResult = audioInputStream.read(segmentData, 0, 8);
             
                BitSet infoBitSet = BitSet.valueOf(segmentData);
                
                int[] infoInt = new int[64];
                
                for(int i = 0; i < 64; i++) {
                    infoInt[i] = infoBitSet.get(i) ? 1 : 0;
                }
                
                //tu wywołanie kodera i dekodera zwracającego tablice byte'ów do odtworzenia
                int[] codePolyInt1 = {
                    0,0,1,0,1,1,1,0, //116
                    1,1,1,0,0,1,1,1, //231
                    0,0,0,1,1,0,1,1, //216
                    0,1,1,1,1,0,0,0, //30
                    1,0,0,0,0,0,0,0 //1
                };
                
                int[] codePolyInt2 = {
                    0,0,0,0,0,0,1,0, //64
                    0,0,0,1,1,1,1,0, //120
                    0,1,1,0,0,0,1,0, //70
                    1,1,1,1,0,0,0,0, //15
                    1,0,0,0,0,0,0,0 //1
                };
                
                System.out.printf("Przetwarzany blok danych: %d", byteCount);
                byteCount++;
                
                int[] codedData = coder(infoInt, codePolyInt1);
                
                int[] decodedData = decoder(codedData, codePolyInt1);
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
        int temp;
        for(int i = 0; i < k; i++) {
            temp = data[data.length-1];
            System.arraycopy(data, 0, data, 1, data.length-1);
            data[0] = temp;
        }
        
        return data;
    }
    
    public int[] rotateLeft(int data[], int k) {
        int temp;
        for(int i = 0; i < k; i++) {
            temp = data[0];
            System.arraycopy(data, 1, data, 0, data.length-1);
            data[data.length-1] = temp;
        }
        
        return data;
    }
    
    Mathematic math = new Mathematic();
    
    public int[] coder(int[] data, int[] codePolyInt) {
        /*BitSet codePolyBitSet = BitSet.valueOf(coderData);
        int[] codePolyInt = new int[40];
        
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 8; j++) {
                codePolyInt[8 * i + j] = codePolyBitSet.get(8 * i +j) ? 1 : 0;
            }
        }*/
        
        System.out.printf("\nWielomian generujacy:");
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
        
        System.out.printf("\nZakodowane dane (wysylane): ");
        for(int i = 0; i < dataMultiplied.length; i++) {
            System.out.printf(Integer.toString(dataMultiplied[i]));
        }
        
        //Zaklamanie jednego bitu
        //dataMultiplied[0] ^= 1;
        //dataMultiplied[25] = 1;
        //dataMultiplied[32] ^= 1;
        
        return dataMultiplied;
    }
    
    public int[] decoder(int[] data, int[] codePolyInt) {
        /*BitSet codePolyBitSet = BitSet.valueOf(coderData);
        int[] codePolyInt = new int[40];
        
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 8; j++) {
                codePolyInt[8 * i + j] = codePolyBitSet.get(8 * i + j) ? 1 : 0;
            }
        }*/
        
        /*int[] tempGen = new int[4];
        tempGen[0] = 1;
        tempGen[1] = 1;
        tempGen[2] = 0;
        tempGen[3] = 1;
        
        int[] tempData = new int[7];
        tempData[0] = 1;
        tempData[1] = 0;
        tempData[2] = 0;
        tempData[3] = 1;
        tempData[4] = 0;
        tempData[5] = 1;
        tempData[6] = 1;*/
          
        int correctingAbility = 2;
        
        /*data = tempData;
        codePolyInt = tempGen;*/
        
        //dane dzialajace
        //int [] workingData = {0,0,0,0,0,0,0,1,0,0,0,1,1,1,0,1,0,0,1,0,1,0,1,1,1,0,0,0,0,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,1,1,1,1,1,1,0,0,1,0,1,1,1,1,0,0,0,1,0,1,1,1,0,1,1,0,0,1,1,1,0,0,0,1,0,1,1,1,0,1,0,1,0,1,1,1,0};
        //int [] notWorkingData = {0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,1,0,1,0,1,1,1,1,1,0,0,0,0,1,1,1,0,1,0,1,1,1,1,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,1,0,1,1,1,1,0,0,1,0,0,0,0,0,1,0,0,1,0,0,0,0,1,0,1,0,0,0,0,0,1};
        
        //System.arraycopy(workingData, 0, data, 0, workingData.length);

        //data = rotateRight(data,1);
        
        System.out.printf("\nWielomian generujacy:");
        for(int i = 0; i < codePolyInt.length; i++) {
            System.out.printf(Integer.toString(codePolyInt[i]));
        }
        
        System.out.printf("\nOdebrane dane: ");
        for(int i = 0; i < data.length; i++) {
            System.out.printf(Integer.toString(data[i]));
        }
        
        //Obliczanie syndromu
        AbstractMap.SimpleEntry<int [], int []> result = math.divideVectors(data, codePolyInt);
        int[] resultVector = result.getKey();
        int[] restVector = result.getValue();
        
        System.out.printf("\nReszta z dzielenia zakodowanego słowa (syndrom błędu):");
        for(int i = 0; i < restVector.length; i++) {
            System.out.printf(Integer.toString(restVector[i]));
        }
 
        int[] decodedData = new int[64];
        int weight = hammingWeight(restVector);
        System.out.printf("\nWaga Hamminga syndromu: %d", weight);
        //Obliczanie wagi bledu i porownanie ze zdolnoscia korekcyjna t
        if(weight <= correctingAbility) {
            //Bledy w czesci kontrolnej, kopiujemy dobre dane
            //System.arraycopy(data, 32, decodedData, 0, decodedData.length);
        } else {
            boolean readyForCorrection = false;
            int rotateCount = 0;
            int dataRotated[] = data;
            //Przesuniecie cykliczne w lewo
            int minHamm = 32;
            while(!readyForCorrection) {
                rotateCount++;
                dataRotated = rotateLeft(dataRotated, 1);
                
                /*System.out.printf("\n%d. Obrot danych w lewo: ", rotateCount);
                for(int i = 0; i < dataRotated.length; i++) {
                    System.out.printf(Integer.toString(dataRotated[i]));
                }*/
                
                result = math.divideVectors(dataRotated, codePolyInt);
                resultVector = result.getKey();
                restVector = result.getValue();
                
                System.out.printf("\nSyndrom: ");
                for(int i = 0; i < restVector.length; i++) {
                    System.out.printf(Integer.toString(restVector[i]));
                }
                
                weight = hammingWeight(restVector);
                
                if(weight < minHamm) minHamm = weight;
                
                System.out.printf("\nWaga Hamminga syndromu: %d", weight);
                System.out.printf("\nMinimalna waga do tej pory: %d", minHamm);
                
                System.out.printf("\n");
                
                if(weight <= correctingAbility) {
                    readyForCorrection = true;
                } else if(rotateCount >= 64) {
                    break;
                }
            }
            if(readyForCorrection) {
                //Dodanie wektora i obrot w druga strone
                int[] res = math.minusVectors1(dataRotated, restVector);
                System.out.printf("\nPrzesuniecia i dane zdekodowane: %d ", rotateCount);
                res = rotateRight(res, rotateCount);
                for(int i = 0; i < res.length; i++) {
                    System.out.printf(Integer.toString(res[i]));
                }
            } else {
                System.out.printf("\nWystapil blad niekorygowalny!");
            }
        }
        
        /*System.out.printf("\nDane zdekodowane: ");
        for(int i = 0; i < decodedData.length; i++) {
            System.out.printf(Integer.toString(decodedData[i]));
        }*/
        
        System.out.printf("\n\n");
        
        return decodedData;
    }

}
