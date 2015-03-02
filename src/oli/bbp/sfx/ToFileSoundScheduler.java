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
    public void playSound(File sound, float volume) {
        soundEvents.add(new SoundEvent(OliRenderer.frameNum, sound, volume));
    }
    
    public void mixAndSave(String soundFilename) {
        AudioMixer am = new AudioMixer();
        am.mixDown(soundEvents, new File(soundFilename));
    }
}
