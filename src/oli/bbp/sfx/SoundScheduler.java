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

    public abstract boolean supportsSourceSelection();
    
    public static class SoundEvent {
        public int startFrame;
        public File soundFile;
        public float volume;
        
        public int startOffset = 0;
        public int trimLength = -1;
        
        public SoundEvent(int start, File sndF, float vol) {
            this.startFrame = start;
            this.soundFile = sndF;
            this.volume = vol;
        }
        
        public SoundEvent(int start, File sndF, float vol, int startOff, int trimLen) {
            this.startFrame = start;
            this.soundFile = sndF;
            this.volume = vol;
            this.startOffset = startOff;
            this.trimLength = trimLen;
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
     * @param startOffset
     * @param trimLength
     */
    public abstract void playSound(File sound, float volume, int startOffset, int trimLength);
    
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
    
    public static void playSound(int frame, File sound, float volume, int startOffset, int trimLength) {
        prescheduled.add(new SoundEvent(frame, sound, volume, startOffset, trimLength));
    }
    
    public static void checkForPrescheduled(int frame) {
        for (SoundEvent se : prescheduled) {
            if (se.startFrame == frame) {
                if (se.startOffset != 0 || se.trimLength != -1) {
                    if (! SoundScheduler.instance.supportsSourceSelection()) {
                        continue;
                    }
                }
                SoundScheduler.instance.playSound(se.soundFile, se.volume, se.startOffset, se.trimLength);
            }
        }
    }
}
