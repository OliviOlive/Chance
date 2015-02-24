/*
 * 2015
 */
package oli.bbp.sfx;

import java.io.File;

/**
 *
 * @author oliver
 */
public abstract class SoundScheduler {
    /**
     * The instance that should be used.
     */
    public static SoundScheduler instance;
    
    /**
     * Registers a sound for playback in this frame.
     * It is expected that in Display mode, this will play the sound.
     * It is expected that in toFile mode, this will log a sound event for the Mixer.
     * @param sound
     */
    public abstract void playSound(File sound);
}
