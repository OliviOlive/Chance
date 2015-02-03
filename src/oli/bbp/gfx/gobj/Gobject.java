/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
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
    }
    
    /**
     * Adjusts properties of the Gobject to meet tweening specifications.
     * @param td
     * @param frameNum 
     */
    public void tween(TweenDeclaration td, int frameNum) {
        switch (td.affectedProperty) {
            case "bounds":
                
            break;
        }
    }
    
    /**
     * 
     * @param g2d 
     */
    public abstract void draw(Graphics2D g2d);
}
