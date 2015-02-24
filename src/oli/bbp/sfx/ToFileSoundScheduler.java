/*
 * 2015
 */
package oli.bbp.sfx;

import java.io.File;
import java.util.ArrayList;
import oli.bbp.gfx.OliRenderer;

public class ToFileSoundScheduler extends SoundScheduler {
    public static class SoundEvent {
        public int startFrame;
        public File soundFile;
        
        public SoundEvent(int start, File sndF) {
            this.startFrame = start;
            this.soundFile = sndF;
        }
    }
    
    public ArrayList<SoundEvent> soundEvents = new ArrayList<>();
    
    @Override
    public void playSound(File sound) {
        soundEvents.add(new SoundEvent(OliRenderer.frameNum, sound));
    }
}
