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
                coder(infoInt);
            }
        }
        catch(IOException ex){
                    return -3;
        } 
        
        return 1;
    }
    
    public int coder(int[] data) {
        Mathematic math = new Mathematic();
        
        byte[] coderData = new byte[5];
        coderData[0] = 64;
        coderData[1] = 120;
        coderData[2] = 70;
        coderData[3] = 15;
        coderData[4] = 1;
                
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
        
        //Obliczanie reszty z dzielenia zakodowanych danych przez weilomian generujacy
        result = math.divideVectors(dataMultiplied, codePolyInt);
        resultVector = result.getKey();
        restVector = result.getValue();
        
        System.out.printf("\nReszta z dzielenia zakodowanego słowa:");
        for(int i = 0; i < restVector.length; i++) {
            System.out.printf(Integer.toString(restVector[i]));
        }
        
        System.out.printf("\n");
        
        return 0;
    }

}
