package oli.bbp;

import java.awt.Graphics;
import oli.bbp.gui.ViewFrame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import oli.bbp.gfx.OliRenderer;

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
    
    public static void onScreenMode() {
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
        
        while (true) {
            // wait until 1/30 second has passed... i.e ~33 ms (accurate enough!)
            long curTim = System.currentTimeMillis();
            if (curTim < timeMillis) {
                try {
                    Thread.sleep(timeMillis - curTim);
                } catch (InterruptedException ex) {
                    log.log(Level.SEVERE, null, ex);
                }
            }
            timeMillis = curTim + 33; // schedule the next one in 33 ms
            
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
    
    public static void toFileMode(File f) {
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[1].equals("output")) {
            log.info("Rendering frames to directory.");
            File imgDir = new File(args[2]);
        } else {
            isOnscreen = true;
            log.info("Rendering frames to screen, in real-time.");
            onScreenMode();
        }
    }
    
}
