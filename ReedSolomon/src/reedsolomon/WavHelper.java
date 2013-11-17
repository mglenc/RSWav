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
                    infoInt[63 - i] = infoBitSet.get(63 - i) ? 1 : 0;
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
        
        for(int i = 0; i < 40; i++) {
            codePolyInt[39 - i] = codePolyBitSet.get(39 - i) ? 1 : 0;
        }
                
        AbstractMap.SimpleEntry<int [], int []> result = math.divideVectors(data, codePolyInt);
        int[] resultVector = result.getKey();
        int[] restVector = result.getValue();
        
        System.out.printf("\nWielomian wynikowy: ");
        for(int i = 0; i < resultVector.length; i++) {
            System.out.printf(Integer.toString(resultVector[i]));
        }
        
        System.out.printf(" Reszta z dzielenia: ");
        for(int i = 0; i < restVector.length; i++) {
            System.out.printf(Integer.toString(restVector[i]));
        }
        return 0;
    }

}
