/*
 * 2015
 */
package oli.bbp.sfx;

import java.io.File;
import java.util.ArrayList;
import oli.bbp.gfx.OliRenderer;

public class ToFileSoundScheduler extends SoundScheduler {
    public ArrayList<SoundEvent> soundEvents = new ArrayList<>();
    
    @Override
    public void playSound(File sound, float volume, int startOffset, int trimLength) {
        soundEvents.add(new SoundEvent(OliRenderer.frameNum, sound, volume, startOffset, trimLength));
    }
    
    public void mixAndSave(String soundFilename) {
        AudioMixer am = new AudioMixer();
        am.mixDown(soundEvents, new File(soundFilename));
    }

    @Override
    public boolean supportsSourceSelection() {
        return true;
    }
}
