/*
 * 2015
 */
package oli.bbp.gfx;

import java.util.ArrayList;
import oli.bbp.gfx.gobj.Gobject;
import org.json.JSONArray;

/**
 *
 * @author oliver
 */
public class TweenDeclaration {
    /**
     * A static list of all declared Tweens.
     */
    public static ArrayList<TweenDeclaration> atd = new ArrayList<>();
    
    
    public String affectedProperty;
    public Gobject affectedGobject;
    public int startFrame, endFrame;
    public JSONArray json;
    
    public TweenDeclaration(String affProp, Gobject affectedGobject, int startFrame, int endFrame, JSONArray json) {
        this.affectedProperty = affProp;
        this.affectedGobject = affectedGobject;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.json = json;
    }
}
