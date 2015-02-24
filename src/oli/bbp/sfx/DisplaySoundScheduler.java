/*
 * 2015
 */
package oli.bbp.sfx;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author oliver
 */
public class DisplaySoundScheduler extends SoundScheduler {
    @Override
    public void playSound(File sound) {
        try {
            Clip c = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(sound);
            c.open(inputStream);
            c.start();
        } catch (Exception e) { // 'pokémon exception handling'
            e.printStackTrace(System.out);
        }
    }
    
}