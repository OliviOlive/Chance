/*
 * 2015
 */

package oli.bbp.gfx;

import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 *
 * @author oliver
 */
public class GfxHelper {
    public static int getTextHeight(String s, FontMetrics fm, Graphics g) {
        return (int) Math.ceil(fm.getLineMetrics(s, g).getHeight());
    }
}
