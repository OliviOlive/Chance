/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import oli.bbp.DimensionHelper;
import oli.bbp.ScriptReader;
import oli.bbp.gfx.TweenDeclaration;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author oliver
 */
public abstract class Gobject {
    public static class GobjectZIndexComparator implements Comparator<Gobject> {
        @Override
        public int compare(Gobject o1, Gobject o2) {
            return Integer.compare(o1.zIndex, o2.zIndex);
        }
    }
    
    /**
     * Static array of all Gobjects.
     */
    public static ArrayList<Gobject> ago = new ArrayList<>();

    public static Gobject getGobjectById(String id) {
        for (Gobject g: ago) {
            if (g.id.equals(id)) {
                return g;
            }
        }
        return null;
    }
    
    public String id;
    public Rectangle bounds;
    public float opacity = 0;
    public int zIndex = 0;
    
    public Gobject(String id, JSONObject ja) {
        this.id = id;
        if (! ja.has("bounds")) {
            throw new ScriptReader.ScriptFormatException("Missing 'bounds' attribute for " + this.getClass().getSimpleName() + "#" + id);
        }
        JSONArray jabounds = ja.getJSONArray("bounds");
        this.bounds = new Rectangle(
            DimensionHelper.getX(jabounds.get(0)),
            DimensionHelper.getY(jabounds.get(1)),
            DimensionHelper.getX(jabounds.get(2)),
            DimensionHelper.getY(jabounds.get(3))
        );
        if (ja.has("z-index")) {
            this.zIndex = ja.getInt("z-index");
        }
    }
    
    // blank
    public Gobject() {
        
    }
    
    public void postConstruct() {
        
    }
    
    /**
     * Adjusts properties of the Gobject to meet tweening specifications.
     * @param td
     * @param frameNum 
     */
    public void tween(TweenDeclaration td, int frameNum) {
        boolean isFirstFrame = td.startFrame == frameNum;
        boolean isLastFrame = td.endFrame == frameNum;
        int numFrame = (frameNum - td.startFrame) + 1;
        int lenFrames = td.endFrame - td.startFrame;
        if (isLastFrame) {
            this.instantChange(td, frameNum);
            return;
        }
        switch (td.affectedProperty) {
            case "bounds":
                if (isFirstFrame) {
                    // store initial bounds
                    td.gobmem.ensureCapacity(8);
                    td.gobmem.add(this.bounds.x);
                    td.gobmem.add(this.bounds.y);
                    td.gobmem.add(this.bounds.width);
                    td.gobmem.add(this.bounds.height);
                    
                    // also calculate & store gradients
                    td.gobmem.add(((double) td.json.getInt(0) - this.bounds.x) / lenFrames);
                    td.gobmem.add(((double) td.json.getInt(1) - this.bounds.y) / lenFrames);
                    td.gobmem.add(((double) td.json.getInt(2) - this.bounds.width) / lenFrames);
                    td.gobmem.add(((double) td.json.getInt(3) - this.bounds.height) / lenFrames);
                }
                this.bounds.setBounds(
                        (int) td.gobmem.get(0) + (int) Math.round(numFrame * (double) td.gobmem.get(4)),
                        (int) td.gobmem.get(1) + (int) Math.round(numFrame * (double) td.gobmem.get(5)),
                        (int) td.gobmem.get(2) + (int) Math.round(numFrame * (double) td.gobmem.get(6)),
                        (int) td.gobmem.get(3) + (int) Math.round(numFrame * (double) td.gobmem.get(7))
                );
            break;
            case "opacity":
                if (isFirstFrame) {
                    // store initial opacity
                    td.gobmem.add(this.opacity);
                    // store gradient
                    td.gobmem.add((double) (td.json.getDouble(0) - this.opacity) / lenFrames);
                }
                
                this.opacity = (float) td.gobmem.get(0) + (float) (numFrame * (double) td.gobmem.get(1));
            break;
        }
    }
    
    /**
     * Adjusts properties of the Gobject to meet instant change specifications.
     * @param td
     * @param frameNum 
     */
    public void instantChange(TweenDeclaration td, int frameNum) {
        switch (td.affectedProperty) {
            case "bounds":
                this.bounds.setBounds(
                        td.json.getInt(0),
                        td.json.getInt(1),
                        td.json.getInt(2),
                        td.json.getInt(3)
                );
            break;
            case "opacity":
                this.opacity = (float) td.json.getDouble(0);
            break;
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "#" + this.id;
    }
    
    /**
     * 
     * @param g2d 
     */
    public abstract void draw(Graphics2D g2d);
}
