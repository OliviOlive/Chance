/*
 * 2015
 */
package oli.bbp.sfx;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author oliver
 */
public abstract class SoundScheduler {
    public static class SoundEvent {
        public int startFrame;
        public File soundFile;
        public float volume;
        
        public SoundEvent(int start, File sndF, float vol) {
            this.startFrame = start;
            this.soundFile = sndF;
            this.volume = vol;
        }
    }
    
    /**
     * The instance that should be used.
     */
    public static SoundScheduler instance;
    
    public static int sampleRate;
    public static int sampleLen;
    
    public static ArrayList<SoundEvent> prescheduled = new ArrayList<>();
    
    /**
     * Registers a sound for playback in this frame.
     * It is expected that in Display mode, this will play the sound.
     * It is expected that in toFile mode, this will log a sound event for the Mixer.
     * @param sound
     * @param volume
     */
    public abstract void playSound(File sound, float volume);
    
    /**
     * Pre-schedules a sound to be played.
     * This is a static method so that it can be used in the Script Reader.
     * @param frame
     * @param sound 
     * @param volume 
     */
    public static void playSound(int frame, File sound, float volume) {
        prescheduled.add(new SoundEvent(frame, sound, volume));
    }
    
    public static void checkForPrescheduled(int frame) {
        for (SoundEvent se : prescheduled) {
            if (se.startFrame == frame) {
                SoundScheduler.instance.playSound(se.soundFile, se.volume);
            }
        }
    }
}
