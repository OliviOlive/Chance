/*
 * 2015
 */
package oli.bbp.gfx;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 *
 * @author oliver
 */
public class GfxHelper {

    public static int getTextHeight(String s, FontMetrics fm, Graphics g) {
        return (int) Math.ceil(fm.getLineMetrics(s, g).getHeight());
    }

    public static void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        AffineTransform restoreTransform = g.getTransform();
        g.transform(at);
        
        final int ARR_SIZE = 12;
        
        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(0, 0, len, 0);
        g.fillPolygon(
                new int[]{ARR_SIZE + len, len, len, ARR_SIZE + len},
                new int[]{0,-ARR_SIZE, ARR_SIZE, 0},
                4
        );
        
        g.setTransform(restoreTransform);
    }
    
    public static void drawArrow(Graphics2D g, Point2D point, double angle, int len) {
        AffineTransform at = AffineTransform.getTranslateInstance(point.getX(), point.getY());
        at.concatenate(AffineTransform.getRotateInstance(angle));
        AffineTransform restoreTransform = g.getTransform();
        g.transform(at);
        
        int ARR_SIZE = 42;
        
        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(0, 0, len, 0);
        g.fillPolygon(
                new int[]{len, len - ARR_SIZE, len - ARR_SIZE, len},
                new int[]{0,-ARR_SIZE, ARR_SIZE, 0},
                4
        );
        
        g.setTransform(restoreTransform);
    }
}
