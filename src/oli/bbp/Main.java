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
import oli.bbp.gfx.OliRenderer;
import oli.bbp.sfx.DisplaySoundScheduler;
import oli.bbp.sfx.SoundScheduler;
import oli.bbp.sfx.ToFileSoundScheduler;

/**
 *
 * @author oliver
 */
public class Main {
    
    private static final Logger log = Logger.getLogger( Main.class.getName() );
    
    public static boolean isOnscreen = false;
    public static GraphicsEnvironment genv;
    public static GraphicsDevice gdev;
    public static GraphicsConfiguration gcfg;
    
    
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int REPORT_EVERY_FRAMES = 600;
    public static final int DIGITS_FRAMES = 6;
    
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
        
        while (OliRenderer.frameNum < OliRenderer.frameDur) {
            long curTim = System.currentTimeMillis();
            if (curTim < timeMillis) {
                try {
                    long sleepy = timeMillis - curTim;
                    //totalSleepy += sleepy;
                    Thread.sleep(sleepy);       
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
        }
    }
    
    public static void toFileMode(File directorySave) throws IOException {
        ToFileSoundScheduler tfss = new ToFileSoundScheduler();
        SoundScheduler.instance = tfss;
        
        while (OliRenderer.frameNum < OliRenderer.frameDur) {
            String filename = directorySave.getPath() + File.separatorChar + String.format("%8d.png", OliRenderer.frameNum).replace(' ', '0');
            OliRenderer.preprocessFrame();
            BufferedImage bi = new BufferedImage(DimensionHelper.RESOLUTION_WIDTH, DimensionHelper.RESOLUTION_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bi.createGraphics();
            OliRenderer.setupHints(g2d);
            OliRenderer.renderFrame(g2d);
            g2d.dispose();
            // audio-run ImageIO.write(bi, "png", new File(filename));
            if (OliRenderer.frameNum % 200 == 0) {
                log.log(Level.INFO, "Frame: {0}", OliRenderer.frameNum);
            }
        }
        
        log.info("Finished frames, starting mixdown.");
        
        tfss.mixAndSave(directorySave.getPath() + File.separatorChar + "audio.wav");
        
        log.info("Finished mixdown.");
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