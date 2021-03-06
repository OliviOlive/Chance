/*
 * 2015
 */

package oli.bbp.gfx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import oli.bbp.DimensionHelper;
import oli.bbp.Main;
import oli.bbp.gfx.gobj.Gobject;
import oli.bbp.sfx.SoundScheduler;

public class OliRenderer {    
    /**
     * The number of frames that have been rendered.
     */
    public static int frameNum = 0;
    
    /**
     * The number of frames that the display should run for, as defined in the script file.
     */
    public static int frameDur;
    
    public static long renderStartTime;
    
    public static final RenderingHints hints = new RenderingHints(new HashMap<Key, Object>());
    
    static {
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    
    public static void setupHints(Graphics2D g2d) {
        g2d.setRenderingHints(hints);
    }
    
    /**
     * Processes keyframing, etc of next frame.
     */
    public static void preprocessFrame() {
        // Update all gobjects according to the script (tweens & instant changes)
        for (TweenDeclaration td: TweenDeclaration.atd) {
            if (td.endFrame == null) {
                if (td.startFrame == frameNum) {
                    td.affectedGobject.instantChange(td, frameNum);
                }
            } else if (td.startFrame <= frameNum && td.endFrame >= frameNum) {
                td.affectedGobject.tween(td, frameNum);
            }
        }
    }
    
    public static boolean skipVid = System.getProperty("olibbp.skipVideo", "F").equals("true");
    
    /**
     * Actually draws the next frame.
     *  It additionally triggers sounds in the Sound Scheduler.
     * @param g Graphics object to be rendered to
     */
    public static void renderFrame(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
        
        SoundScheduler.checkForPrescheduled(frameNum);
        
        if (! skipVid) {
            if (Main.isOnscreen) {
                String frameCounter = "Frame Monitor: " + String.valueOf(frameNum) + " of " + String.valueOf(frameDur-1);
                String secCounter = "Supposed Time: " + String.valueOf((float) frameNum / DimensionHelper.FRAMES_PER_SECOND);
                String rsCounter = "Real Time: " + String.valueOf((float) (System.currentTimeMillis() - renderStartTime) / 1000);

                int fh = GfxHelper.getTextHeight(frameCounter, g.getFontMetrics(), g);

                FontMetrics fm = g.getFontMetrics();

                int wid = Math.max(fm.stringWidth(frameCounter), Math.max(fm.stringWidth(secCounter), fm.stringWidth(rsCounter)));

                g.setColor(Color.black);
                g.fillRect(0, 0, g.getFontMetrics().stringWidth(frameCounter), fh * 3);

                g.setColor(Color.cyan);
                g.drawString(frameCounter, 0, fh);
                g.drawString(secCounter, 0, fh*2);
                g.drawString(rsCounter, 0, fh*3);
            }

            for (Gobject gob: Gobject.ago) {
                if (gob.opacity > 0.0) {
                    BufferedImage bi = new BufferedImage(DimensionHelper.RESOLUTION_WIDTH, DimensionHelper.RESOLUTION_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D gbi = bi.createGraphics();
                    setupHints(gbi);
                    gob.draw(gbi);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, gob.opacity));
                    g.drawImage(bi, null, 0, 0);
                }
            }
        }
        
        ++frameNum;
    }
}
