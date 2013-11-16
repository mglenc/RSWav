/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package reedsolomon;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

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
        
        byte[] segmentData = new byte[256];
        
        int bytesResult = 0;
        
        try{
            while(bytesResult >= 0){
                bytesResult = audioInputStream.read(segmentData, 0, 240); //bloki po 256B i miejsce na 16B kontrolnych(8 błędów)
                //tu wywołanie kodera i dekodera zwracającego tablice byte'ów do odtworzenia
            }
        }
        catch(IOException ex){
                    return -3;
        } 
        
        return 1;
    }

}
