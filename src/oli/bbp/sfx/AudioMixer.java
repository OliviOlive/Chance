/*
 * 2015
 */
package oli.bbp.sfx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import oli.bbp.DimensionHelper;
import oli.bbp.sfx.SoundScheduler.SoundEvent;
import wavfile.WavFile;
import wavfile.WavFileException;

/**
 *
 * @author oliver
 */
public class AudioMixer {
    static final Logger log = Logger.getLogger( AudioMixer.class.getName() );
    
    public static class AudioInputTrack {
        public File audioFile;
        public AudioFormat format;
        public double[][] data;
        public int sampleCount;
        public int channelCount;
        
        public AudioInputTrack(File audioFile) {
            try {
                /*
                AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
                format = ais.getFormat();
                sampleCount = (int) ais.getFrameLength();
                channelCount = format.getChannels();
                data = new int[sampleCount * channelCount];
                */
                
                WavFile wf = WavFile.openWavFile(audioFile);
                
                channelCount = wf.getNumChannels();
                boolean dupeChannel0 = false;
                if (channelCount != 2) {
                    log.log(Level.WARNING, "Warning: {0} does not have exactly 2 channels.", audioFile.getName());
                    log.warning("Therefore, channel #0 will be duplicated as assuming mono.");
                    dupeChannel0 = true;
                    channelCount = 2;
                }
                sampleCount = (int) wf.getNumFrames();
                
                /*
                int preamp = 1 << (31 - wf.getValidBits());
                int subtract;
                if (wf.getValidBits() < 32) {
                    // adjust it and fit it back
                    // it is unknown whether any wav files will be unhappy
                    // with this re-centralising operation
                    subtract = 1 << (wf.getValidBits()-1);
                } else {
                    // probably already using 32bit signed PCM
                    subtract = 0;
                }
                
                
                log.log(Level.INFO, "Preamp is {0}", preamp);
                
                // owk removed this section during double usage
                */
                
                int subtract = 0;
                int preamp = 1;
                
                data = new double[channelCount][sampleCount];
                
                double[] singleSampleBuffer = new double[channelCount];
                
                for (int i = 0; i < sampleCount; ++i) {
                    wf.readFrames(singleSampleBuffer, 1);
                    data[0][i] = (singleSampleBuffer[0] - subtract) * preamp;
                    if (dupeChannel0) {
                        data[1][i] = (singleSampleBuffer[0] - subtract) * preamp;
                    } else {
                        data[1][i] = (singleSampleBuffer[1] - subtract) * preamp;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(AudioMixer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public boolean isSameFile(File compF) {
            return compF.equals(audioFile);
        }
    }
    public static class AudioInputTrackPointer {
        public AudioInputTrack track;
        public int startSample;
        public double strength;
        
        public int startOffset;
        public int trimLength;
        
        public boolean canRead(int pos) {
            if (startSample > pos)
                return false;
            
            int sampleI = (pos - startSample) + startOffset;
            
            if (trimLength != -1 && sampleI > trimLength) {
                return false; // out of the trim section
            }
            
            return sampleI < track.data[0].length;
        }
        
        public double read(int channel, int pos) {
            return (track.data[channel][(pos - startSample) + startOffset] * strength);
        }
    }
    
    public void mixDown(ArrayList<SoundEvent> events, File outFile) {
        ArrayList<AudioInputTrack> aits = new ArrayList<>();
        ArrayList<AudioInputTrackPointer> aitps = new ArrayList<>();
        
        // first open all necessary files and get 'pointers' for all events
        for (SoundEvent e : events) {
            AudioInputTrack usedTrack = null;
            for (AudioInputTrack a : aits) {
                if (a.isSameFile(e.soundFile)) {
                    usedTrack = a;
                    break;
                }
            }
            
            if (usedTrack == null) {
                usedTrack = new AudioInputTrack(e.soundFile);
                aits.add(usedTrack);
            }
            
            AudioInputTrackPointer p = new AudioInputTrackPointer();
            p.track = usedTrack;
            p.startSample = (int) ((double) e.startFrame / DimensionHelper.FRAMES_PER_SECOND) * SoundScheduler.sampleRate;
            p.strength = e.volume;
            p.startOffset = e.startOffset;
            p.trimLength = e.trimLength;
            aitps.add(p);
        }
        
        int soundtrackLength = SoundScheduler.sampleLen;
        
        try {
            WavFile outwf = WavFile.newWavFile(outFile, 2, soundtrackLength, 32, SoundScheduler.sampleRate);
            
            double[][] outSampleBuffer;
            
            for (int i = 0; i < soundtrackLength; ++i) {
                outSampleBuffer = new double[2][1];
                for (AudioInputTrackPointer tp : aitps) {
                    if (tp.canRead(i)) {
                        outSampleBuffer[0][0] += tp.read(0, i);
                        outSampleBuffer[1][0] += tp.read(1, i);
                    }
                }
                if (outwf.writeFrames(outSampleBuffer, 1) == 0) {
                    log.log(Level.INFO, "(WavFile not accepting more frames @ {0}).", i);
                }
            }
            
            outwf.close();
        } catch (IOException | WavFileException ex) {
            Logger.getLogger(AudioMixer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
