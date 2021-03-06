/*
 * 2015
 */

package oli.bbp.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;
import oli.bbp.DimensionHelper;
import oli.bbp.Main;

/**
 *
 * @author oliver
 */
public class OliKeyListener implements KeyListener {
    
    private static final Logger log = Logger.getLogger( Main.class.getName() );

    @Override
    public void keyTyped(KeyEvent e) {
        //
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            log.info("Escape pressed.");
            System.exit(0);
        }
        
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // skip 10 seconds
            Main.skipFrames += DimensionHelper.FRAMES_PER_SECOND * 10;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //
    }
    
}
