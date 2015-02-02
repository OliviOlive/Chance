/*
 * 2015
 */

package oli.bbp.gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import oli.bbp.Main;

public class OliRenderer {
    /**
     * The number of frames that have been rendered.
     */
    public static int frameNum = 0;
    
    public static void setupHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    
    /**
     * Processes keyframing, etc of next frame.
     */
    public static void preprocessFrame() {
        
    }
    
    /**
     * Actually draws the next frame.
     * @param g Graphics object to be rendered to
     */
    public static void renderFrame(Graphics2D g) {
        ++frameNum;
        g.setColor(Color.black);
        g.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
        
        String frameCounter = String.valueOf(frameNum);
        
        int fh = GfxHelper.getTextHeight(frameCounter, g.getFontMetrics(), g);
        
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, g.getFontMetrics().stringWidth(frameCounter), fh);
        
        g.setColor(Color.red);
        g.drawString(frameCounter, 0, fh);
    }
}
