package oli.bbp;

import java.awt.Graphics;
import oli.bbp.gui.ViewFrame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import oli.bbp.gfx.OliRenderer;
import oli.bbp.io.VoidStream;
import oli.bbp.sfx.DisplaySoundScheduler;
import oli.bbp.sfx.SoundScheduler;
import oli.bbp.sfx.ToFileSoundScheduler;

/**
 *
 * @author oliver
 */
public class Main {
    
    public static final Logger log = Logger.getLogger( Main.class.getName() );
    
    public static boolean isOnscreen = false;
    public static GraphicsEnvironment genv;
    public static GraphicsDevice gdev;
    public static GraphicsConfiguration gcfg;
    
    
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int REPORT_EVERY_FRAMES = 50;
    public static final int DIGITS_FRAMES = 6;
    
    public static volatile int skipFrames = 0;
    
    public static void onScreenMode() {
        SoundScheduler.instance = new DisplaySoundScheduler();
        
        genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gdev = genv.getDefaultScreenDevice();
        gcfg = gdev.getDefaultConfiguration();
        if (! gdev.isFullScreenSupported()) {
            log.warning("FullScreen is not supported.");
            return;
        }
        log.info("FullScreen seems supported.");
        ViewFrame vf = new ViewFrame();
        vf.setBounds(0, 0, WIDTH, HEIGHT);
        vf.setVisible(true);
        vf.setIgnoreRepaint(true);
        
        Window w = new Window(vf);
        vf.createBufferStrategy(2);
        BufferStrategy bufStrat = vf.getBufferStrategy();
        
        long timeMillis = System.currentTimeMillis();
        
        //long totalSleepy = 0;
        
        OliRenderer.renderStartTime = timeMillis;
        
        boolean asapMode = System.getProperty("olibbp.asap") != null;
        
        while (OliRenderer.frameNum < OliRenderer.frameDur) {
            long curTim = System.currentTimeMillis();
            if (curTim < timeMillis) {
                try {
                    long sleepy = timeMillis - curTim;
                    //totalSleepy += sleepy;
                    if (! asapMode) Thread.sleep(sleepy);       
                } catch (InterruptedException ex) {
                    log.log(Level.SEVERE, null, ex);
                }
            }
            curTim = System.currentTimeMillis();
            timeMillis = curTim + (1000 / DimensionHelper.FRAMES_PER_SECOND); // schedule the next one in 33 ms
            
            bufStrat.show();
            
            OliRenderer.preprocessFrame();
            
            Graphics g = bufStrat.getDrawGraphics();
            if (g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;
                try {
                    OliRenderer.renderFrame(g2d);
                } finally {
                    g2d.dispose();
                }
            } else {
                log.warning("Cannot cast Graphics to Graphics2D!");
                return;
            }
            
            
            while (skipFrames > 0) {
                --skipFrames;
                OliRenderer.preprocessFrame();
                ++OliRenderer.frameNum; // don't bother rendering it
            }
        }
    }
    
    public static void toFileMode(File directorySave) throws IOException {
        try {
            ToFileSoundScheduler tfss = new ToFileSoundScheduler();
            SoundScheduler.instance = tfss;
            
            DimensionHelper.CURRENT_DOWNSCALE = 1.0;
            
            while (OliRenderer.frameNum < OliRenderer.frameDur) {
                String filename = directorySave.getPath() + File.separatorChar + String.format("%8d.png", OliRenderer.frameNum).replace(' ', '0');
                OliRenderer.preprocessFrame();
                BufferedImage bi = new BufferedImage(DimensionHelper.RESOLUTION_WIDTH, DimensionHelper.RESOLUTION_HEIGHT, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = bi.createGraphics();
                OliRenderer.setupHints(g2d);
                OliRenderer.renderFrame(g2d);
                g2d.dispose();
                ImageIO.write(bi, "png", new File(filename));
                if (OliRenderer.frameNum % REPORT_EVERY_FRAMES == 0) {
                    log.log(Level.INFO, "Frame: {0}", OliRenderer.frameNum);
                }
            }
            
            String outPathS = directorySave.getPath() + File.separatorChar;
            
            log.info("Finished frames, starting mixdown.");
            
            tfss.mixAndSave(outPathS + "audio.wav");
            
            log.info("Finished mixdown.");
            
            log.info("Will now attempt to run conversion with avconv");
            
            Runtime rt = Runtime.getRuntime();
            String[] fcmd = new String[]{"avconv", "-r", Integer.toString(DimensionHelper.FRAMES_PER_SECOND), "-y", "-i", "./%08d.png", "-i", "./audio.wav", "-ar", "44100", "-strict", "experimental", "./result.mp4"};
            String joinD = "";
            for (String k : fcmd) {
                joinD += " " + k;
            }
            log.log(Level.INFO, "Running:{0}", joinD);
            Process p = rt.exec(fcmd, null, directorySave);
            VoidStream vse = new VoidStream(p.getErrorStream());
            VoidStream vso = new VoidStream(p.getInputStream());
            vse.start();
            vso.start(); // we don't want to know about stdout & stderr
            int errc = p.waitFor();
            if (errc != 0) {
                throw new RuntimeException("avconv did not suceed, returned errcode " + errc);
            } else {
                log.log(Level.INFO, "Cleaning up...");
                for (int i = 0; i < OliRenderer.frameDur; ++i) {
                    String filename = directorySave.getPath() + File.separatorChar + String.format("%8d.png", i).replace(' ', '0');
                    new File(filename).delete(); // could use deleteOnExit instead, but nah, might cause issues with many frames
                }
                new File(outPathS + "audio.wav").delete();

                log.log(Level.INFO, "Cleaned up!");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            log.log(Level.SEVERE, "Please ensure at least one argument is supplied: args[1]: input script file, args[2]optional: output directory");
            System.exit(1);
        }
        
        File inScript = new File(args[0]);
        if (! inScript.exists()) {
            System.err.println("Input file does not exist!");
            System.exit(2);
        }
        
        try {
            ScriptReader.parseScript(inScript);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(3);
        }
        
        if (args.length > 1) {
            log.info("Rendering frames to directory.");
            File imgDir = new File(args[1]);
            
            if (! imgDir.exists()) {
                log.log(Level.INFO, "Creating directory: {0}", imgDir.getAbsolutePath());
                imgDir.mkdirs();
            }
            
            try {
                toFileMode(imgDir);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            isOnscreen = true;
            log.info("Rendering frames to screen, in real-time.");
            onScreenMode();
        }
    }   
}